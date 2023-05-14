package co.wadcorp.waiting.api.internal.service.waiting;

import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteOrderDto;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteOrderDto.OrderLineItem;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteWaitingRegisterServiceRequest;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteWaitingRegisterServiceRequest.PersonOptionVO;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.CreatedOrderDto;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingRegisterResponse;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.menu.MenuRepository;
import co.wadcorp.waiting.data.domain.settings.*;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData.PersonOptionSetting;
import co.wadcorp.waiting.data.domain.shop.ShopEntity;
import co.wadcorp.waiting.data.domain.shop.ShopRepository;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoRepository;
import co.wadcorp.waiting.data.domain.shop.operation.pause.RemoteAutoPauseInfo;
import co.wadcorp.waiting.data.domain.shop.operation.status.RegistrableStatus;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.StockRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.support.Price;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static co.wadcorp.waiting.data.domain.shop.operation.status.RegistrableStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//@Disabled
@ActiveProfiles("test")
@SpringBootTest
class RemoteWaitingRegisterApiServiceMultiThreadTest {

    @Autowired
    private RemoteWaitingRegisterApiService remoteWaitingRegisterApiService;
    @Autowired
    private ShopOperationInfoRepository shopOperationInfoRepository;
    @Autowired
    private OrderSettingsRepository orderSettingsRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private OptionSettingsRepository optionSettingsRepository;
    @Autowired
    private HomeSettingsRepository homeSettingsRepository;
    @Autowired
    private ShopRepository shopRepository;

    @DisplayName("원격 웨이팅 등록 시 주문정보도 저장할 수 있다.")
    @Test
    void registerWaitingWithOrder() throws InterruptedException {
        // given
        int threadCnt = 200;
        int threadPoolCnt = 60;

        String shopId = "shopId";
        String menuId = "menuId";
        LocalDate operationDate = LocalDate.of(2023, 5, 11);
        ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
        channelShopIdMapping.put(shopId, "1");

        ZonedDateTime nowDateTime = ZonedDateTimeUtils.ofSeoul(
                LocalDateTime.of(operationDate, LocalTime.of(15, 30)));

        createShopOperationInfo(shopId, operationDate, OPEN,
                LocalDateTime.of(operationDate, LocalTime.of(10, 0)),
                LocalDateTime.of(operationDate, LocalTime.of(22, 0)),
                LocalDateTime.of(operationDate, LocalTime.of(11, 0)),
                LocalDateTime.of(operationDate, LocalTime.of(21, 0)),
                LocalDateTime.of(operationDate, LocalTime.of(14, 0)),
                LocalDateTime.of(operationDate, LocalTime.of(15, 0))
        );

        createShop(shopId);
        createOrderSettings(shopId, true);
        createOptionSettings(shopId, "PERSON_OPTION_ID", "성인");
        createHomeSettings(shopId, "SHOP_SEAT_OPTION_ID", "TAKE_OUT_SEAT_OPTION_ID");

        createMenu(shopId, menuId, "메뉴1", 1, 10000, 50);
        createStock(menuId, operationDate, threadCnt);

        List<RemoteWaitingRegisterServiceRequest> requests = new ArrayList<>();

        IntStream.range(0, threadCnt).forEach(i -> {
            String phoneNumber = generatePhoneNumber();
            while(requests.contains(phoneNumber)) {
                System.out.println("duplicated phone number~~");
                phoneNumber = generatePhoneNumber();
            }

            RemoteWaitingRegisterServiceRequest request = RemoteWaitingRegisterServiceRequest.builder()
                    .tableId("SHOP_SEAT_OPTION_ID")
                    .totalPersonCount(2)
                    .personOptions(List.of(PersonOptionVO.builder()
                            .id("PERSON_OPTION_ID")
                            .count(1)
                            .additionalOptions(List.of())
                            .build())
                    )
                    .phoneNumber(phoneNumber)
                    .order(RemoteOrderDto.builder()
                            .totalPrice(BigDecimal.valueOf(10000))
                            .orderLineItems(List.of(
                                    OrderLineItem.builder()
                                            .menuId(menuId)
                                            .name("메뉴1")
                                            .unitPrice(BigDecimal.valueOf(10000))
                                            .linePrice(BigDecimal.valueOf(10000))
                                            .quantity(1)
                                            .build()
                            ))
                            .build()
                    )
                    .build();

            requests.add(request);
        });

//    when(waitingNumberService.getWaitingNumber(any(), any())).thenReturn(WaitingNumber.ofDefault());

        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolCnt);
        CountDownLatch latch = new CountDownLatch(threadCnt);

        for (int i = 0; i < threadCnt; i++) {
            final int idx = i;
            executorService.submit(() -> {
                System.out.println("============" + idx + "=============");
                try {
                    remoteWaitingRegisterApiService.register(
                            channelShopIdMapping, operationDate,
                            nowDateTime, requests.get(idx));
                } catch (Exception e) {
                    System.out.println(e);
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        StockEntity stock = stockRepository.findByMenuIdAndOperationDate(menuId, operationDate).get();
        assertEquals(threadCnt, stock.getSalesQuantity());
    }

    private String generatePhoneNumber() {
        Random rand = new Random();
        int num1 = (rand.nextInt(7) + 1) * 100 + (rand.nextInt(8) * 10) + rand.nextInt(8);
        int num2 = rand.nextInt(743);
        int num3 = rand.nextInt(10000);

        DecimalFormat df3 = new DecimalFormat("000"); // 3 zeros
        DecimalFormat df4 = new DecimalFormat("0000"); // 4 zeros

        return df3.format(num1) + "-" + df3.format(num2) + "-" + df4.format(num3);
    }

    private ShopOperationInfoEntity createShopOperationInfo(String shopId, LocalDate operationDate,
                                                            RegistrableStatus registrableStatus, LocalDateTime operationStartDateTime,
                                                            LocalDateTime operationEndDateTime, LocalDateTime remoteOperationStartDateTime,
                                                            LocalDateTime remoteOperationEndDateTime) {
        ShopOperationInfoEntity shopOperationInfo = ShopOperationInfoEntity.builder()
                .shopId(shopId)
                .operationDate(operationDate)
                .registrableStatus(registrableStatus)
                .operationStartDateTime(ZonedDateTimeUtils.ofSeoul(operationStartDateTime))
                .operationEndDateTime(ZonedDateTimeUtils.ofSeoul(operationEndDateTime))
                .remoteOperationStartDateTime(ZonedDateTimeUtils.ofSeoul(remoteOperationStartDateTime))
                .remoteOperationEndDateTime(ZonedDateTimeUtils.ofSeoul(remoteOperationEndDateTime))
                .build();

        return shopOperationInfoRepository.save(shopOperationInfo);
    }

    private ShopOperationInfoEntity createShopOperationInfo(String shopId, LocalDate operationDate,
                                                            RegistrableStatus registrableStatus, LocalDateTime operationStartDateTime,
                                                            LocalDateTime operationEndDateTime, LocalDateTime remoteOperationStartDateTime,
                                                            LocalDateTime remoteOperationEndDateTime, LocalDateTime remoteAutoPauseStartDateTime,
                                                            LocalDateTime remoteAutoPauseEndDateTime) {
        ShopOperationInfoEntity shopOperationInfo = ShopOperationInfoEntity.builder()
                .shopId(shopId)
                .operationDate(operationDate)
                .registrableStatus(registrableStatus)
                .operationStartDateTime(ZonedDateTimeUtils.ofSeoul(operationStartDateTime))
                .operationEndDateTime(ZonedDateTimeUtils.ofSeoul(operationEndDateTime))
                .remoteOperationStartDateTime(ZonedDateTimeUtils.ofSeoul(remoteOperationStartDateTime))
                .remoteOperationEndDateTime(ZonedDateTimeUtils.ofSeoul(remoteOperationEndDateTime))
                .remoteAutoPauseInfo(RemoteAutoPauseInfo.builder()
                        .remoteAutoPauseStartDateTime(ZonedDateTimeUtils.ofSeoul(remoteAutoPauseStartDateTime))
                        .remoteAutoPauseEndDateTime(ZonedDateTimeUtils.ofSeoul(remoteAutoPauseEndDateTime))
                        .build()
                )
                .build();

        return shopOperationInfoRepository.save(shopOperationInfo);
    }

    private void createShop(String shopId) {
        ShopEntity shopEntity = ShopEntity.builder()
                .shopId(shopId)
                .shopName("TEST_SHOP")
                .shopAddress("SHOP_ADDRESS")
                .shopTelNumber("010-0000-0000")
                .isUsedRemoteWaiting(true)
                .isTest(false)
                .isMembership(true)
                .build();

        shopRepository.save(shopEntity);
    }

    private void createOrderSettings(String shopId, boolean isPossibleOrder) {
        OrderSettingsEntity orderSettingsEntity = OrderSettingsEntity.builder()
                .shopId(shopId)
                .orderSettingsData(OrderSettingsData.builder()
                        .isPossibleOrder(isPossibleOrder)
                        .build()
                )
                .build();

        orderSettingsRepository.save(orderSettingsEntity);
    }

    private void createOptionSettings(String shopId, String personOptionSettingsId, String name) {
        OptionSettingsEntity optionSettingsEntity = new OptionSettingsEntity(shopId,
                OptionSettingsData.of(true, List.of(
                        PersonOptionSetting.builder()
                                .id(personOptionSettingsId)
                                .name(name)
                                .isDisplayed(true)
                                .isSeat(true)
                                .isDefault(true)
                                .canModify(true)
                                .additionalOptions(List.of())
                                .build()
                )));

        optionSettingsRepository.save(optionSettingsEntity);
    }

    private void createHomeSettings(String shopId, String shopSeatOptionId,
                                    String takeoutSeatOptionId) {
        HomeSettingsEntity homeSettingsEntity = HomeSettingsEntity.builder()
                .shopId(shopId)
                .homeSettingsData(HomeSettingsData.builder()
                        .waitingModeType("DEFAULT")
                        .defaultModeSettings(SeatOptions.builder()
                                .id("DEFAULT_MODE_SETTINGS_ID")
                                .name("포장")
                                .minSeatCount(1)
                                .maxSeatCount(7)
                                .expectedWaitingPeriod(5)
                                .isUsedExpectedWaitingPeriod(false)
                                .isDefault(true)
                                .isTakeOut(true)
                                .build()
                        )
                        .tableModeSettings(List.of(
                                SeatOptions.builder()
                                        .id(shopSeatOptionId)
                                        .name("홀 2인석")
                                        .minSeatCount(1)
                                        .maxSeatCount(2)
                                        .expectedWaitingPeriod(5)
                                        .isUsedExpectedWaitingPeriod(true)
                                        .isDefault(true)
                                        .isTakeOut(false)
                                        .build(),
                                SeatOptions.builder()
                                        .id(takeoutSeatOptionId)
                                        .name("포장2")
                                        .minSeatCount(3)
                                        .maxSeatCount(4)
                                        .expectedWaitingPeriod(15)
                                        .isUsedExpectedWaitingPeriod(true)
                                        .isDefault(true)
                                        .isTakeOut(true)
                                        .build()
                        ))
                        .build()
                )
                .build();

        homeSettingsRepository.save(homeSettingsEntity);
    }

    private MenuEntity createMenu(String shopId, String menuId, String name, int ordering,
                                  int unitPrice, int dailyStock) {
        return menuRepository.save(MenuEntity.builder()
                .shopId(shopId)
                .menuId(menuId)
                .name(name)
                .ordering(ordering)
                .unitPrice(Price.of(unitPrice))
                .isUsedDailyStock(true)
                .dailyStock(dailyStock)
                .build()
        );
    }

    private StockEntity createStock(String menuId, LocalDate operationDate, int stock) {
        return stockRepository.save(StockEntity.builder()
                .menuId(menuId)
                .operationDate(operationDate)
                .isUsedDailyStock(true)
                .stock(stock)
                .salesQuantity(0)
                .isOutOfStock(false)
                .build()
        );
    }

}