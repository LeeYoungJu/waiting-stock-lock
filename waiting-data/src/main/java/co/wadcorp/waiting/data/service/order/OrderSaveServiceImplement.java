package co.wadcorp.waiting.data.service.order;

import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.waiting.data.domain.customer.CustomerEntity;
import co.wadcorp.waiting.data.domain.customer.ShopCustomerEntity;
import co.wadcorp.waiting.data.domain.order.OrderEntity;
import co.wadcorp.waiting.data.domain.order.OrderLineItemEntity;
import co.wadcorp.waiting.data.domain.order.OrderLineItemRepository;
import co.wadcorp.waiting.data.domain.order.OrderRepository;
import co.wadcorp.waiting.data.domain.order.history.OrderHistoryRepository;
import co.wadcorp.waiting.data.domain.order.history.OrderLineItemHistoryEntity;
import co.wadcorp.waiting.data.domain.order.history.OrderLineItemHistoryRepository;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData;
import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.status.OperationStatus;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.StockHistoryRepository;
import co.wadcorp.waiting.data.domain.stock.StockRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.validator.WaitingRegisterValidator;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.service.customer.CustomerService;
import co.wadcorp.waiting.data.service.customer.ShopCustomerService;
import co.wadcorp.waiting.data.service.settings.HomeSettingsService;
import co.wadcorp.waiting.data.service.settings.OptionSettingsService;
import co.wadcorp.waiting.data.service.waiting.ShopOperationInfoService;
import co.wadcorp.waiting.data.service.waiting.WaitingService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static co.wadcorp.libs.stream.StreamUtils.convert;
import static co.wadcorp.waiting.data.domain.customer.CustomerEntity.EMPTY_CUSTOMER_ENTITY;

@Service
@RequiredArgsConstructor
public class OrderSaveServiceImplement implements OrderSaveService {

    private static final int TAKE_OUT_PERSON_COUNT = 1;

    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final StockRepository stockRepository;
    private final OrderLineItemRepository orderLineItemRepository;
    private final OrderLineItemHistoryRepository orderLineItemHistoryRepository;

    private final ShopOperationInfoService shopOperationInfoService;
    private final CustomerService customerService;
    private final ShopCustomerService shopCustomerService;
    private final WaitingService waitingService;
    private final OptionSettingsService optionSettingsService;
    private final HomeSettingsService homeSettingsService;

    @Transactional
    public OrderEntity save(String  waitingShopId,
                            LocalDate operationDate,
                            ZonedDateTime nowDateTime,
                            OrderEntity orderEntity, PhoneNumber phoneNumber) {
        ShopOperationInfoEntity shopOperationInfoEntity = shopOperationInfoService.findByShopIdAndOperationDate(waitingShopId, operationDate);

        if (OperationStatus.findWithRemoteTime(shopOperationInfoEntity, nowDateTime)
                != OperationStatus.OPEN) {
            throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_OPEN_WAITING_OPERATION);
        }

        ShopCustomerEntity shopCustomer = getShopCustomer(waitingShopId, phoneNumber);
        shopCustomer.updateVisitCount();

        // 중복 웨이팅 검증
        List<WaitingEntity> waitingList = waitingService.getWaitingByCustomerSeqToday(
                shopCustomer.getCustomerSeq(),
                operationDate);
        WaitingRegisterValidator.validate(waitingShopId, waitingList);

        HomeSettingsData homeSettings = homeSettingsService.getHomeSettings(waitingShopId)
                .getHomeSettingsData();
        OptionSettingsData optionSettings = optionSettingsService.getOptionSettings(waitingShopId)
                .getOptionSettingsData();

        // 좌석 이름으로 매장이용방식(좌석옵션) 조회
        SeatOptions seatOptions = homeSettings.findSeatOptionsBySeatOptionId(request.getTableId());

        // 총 착석 인원 (인원옵션설정 사용 매장은 비착석 인원 제외)
        Integer totalPersonCount = getTotalSeatCountByPersonOption(
                request.getTotalPersonCount(),
                request.getPersonOptions(),
                optionSettings,
                seatOptions
        );

        // 착석 인원 검증
        seatOptions.validSeatCount(totalPersonCount);





        OrderEntity savedOrderEntity = orderRepository.save(orderEntity);
        orderHistoryRepository.save(savedOrderEntity.toHistoryEntity());

        saveOrderLineItem(orderEntity);

        // 판매수량 증가
        List<OrderLineItemEntity> orderLineItems = orderEntity.getOrderLineItems();
        orderLineItems.forEach(
                item -> {
                    StockEntity stock = stockRepository.findByMenuIdAndOperationDate(item.getMenuId(), orderEntity.getOperationDate())
                            .orElseThrow(() -> AppException.ofBadRequest(ErrorCode.NOT_FOUND_STOCK));
                    stock.increaseSalesQuantity(item.getQuantity());
                    stockRepository.save(stock);
//          stockRepository.increaseSalesQuantity(
//                  item.getMenuId(), orderEntity.getOperationDate(), item.getQuantity()
//          );
                }
        );

        return savedOrderEntity;
    }

    private void saveOrderLineItem(OrderEntity orderEntity) {
        List<OrderLineItemEntity> orderLineItemEntities = orderLineItemRepository.saveAll(
                orderEntity.getOrderLineItems());

        List<OrderLineItemHistoryEntity> orderLineItemHistoryEntities = convert(orderLineItemEntities,
                OrderLineItemEntity::toHistoryEntity);

        orderLineItemHistoryRepository.saveAll(orderLineItemHistoryEntities);
    }

    private ShopCustomerEntity getShopCustomer(String shopId, PhoneNumber phoneNumber) {
        // 고객 정보 조회 & 저장
        CustomerEntity customer = customerService.getCustomerByCustomerPhone(phoneNumber);

        if (customer == EMPTY_CUSTOMER_ENTITY) {
            customer = customerService.saveCustomerEntity(CustomerEntity.create(phoneNumber));
        }

        // shop_customer 저장
        ShopCustomerEntity shopCustomer = shopCustomerService.getShopCustomerById(shopId,
                customer.getSeq());
        return shopCustomerService.saveShopCustomer(shopCustomer);
    }

    private Integer getTotalSeatCountByPersonOption(
            Integer totalPersonCount,
            List<RemoteWaitingRegisterServiceRequest.PersonOptionVO> personOptions,
            OptionSettingsData optionSettings,
            SeatOptions seatOptions
    ) {

        if (seatOptions.getIsTakeOut()) {
            return TAKE_OUT_PERSON_COUNT;
        }

        if (optionSettings.isNotUsePersonOptionSetting()) {
            return totalPersonCount;
        }

        return optionSettings.getPersonOptionSettings().stream()
                .filter(OptionSettingsData.PersonOptionSetting::getIsSeat)
                .map(e -> personOptions.stream()
                        .filter(r -> StringUtils.equals(r.getId(), e.getId()))
                        .findFirst()
                        .map(RemoteWaitingRegisterServiceRequest.PersonOptionVO::getCount).orElse(0))
                .mapToInt(Integer::valueOf)
                .sum();
    }
}
