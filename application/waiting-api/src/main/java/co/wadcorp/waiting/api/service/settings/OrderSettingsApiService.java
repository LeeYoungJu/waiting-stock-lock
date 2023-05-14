package co.wadcorp.waiting.api.service.settings;

import static co.wadcorp.libs.stream.StreamUtils.convert;

import co.wadcorp.waiting.data.domain.shop.operation.status.OperationStatus;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderSettingsServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderSettingsResponse;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsData;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.query.displaymenu.DisplayMenuQueryRepository;
import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayMenuNamePriceDto;
import co.wadcorp.waiting.data.query.menu.MenuQueryRepository;
import co.wadcorp.waiting.data.query.menu.dto.MenuNamePriceDto;
import co.wadcorp.waiting.data.query.settings.OrderSettingsQueryRepository;
import co.wadcorp.waiting.data.service.settings.OrderSettingsService;
import co.wadcorp.waiting.data.service.waiting.ShopOperationInfoService;
import co.wadcorp.waiting.data.service.waiting.WaitingService;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderSettingsApiService {

  private final OrderSettingsQueryRepository orderSettingsQueryRepository;
  private final MenuQueryRepository menuQueryRepository;
  private final DisplayMenuQueryRepository displayMenuQueryRepository;
  private final OrderSettingsService orderSettingsService;

  private final WaitingService waitingService;
  private final ShopOperationInfoService shopOperationInfoService;

  public OrderSettingsResponse getOrderSettings(String shopId, LocalDate operationDate,
      ZonedDateTime nowZonedDateTime) {
    OrderSettingsData orderSettings = orderSettingsQueryRepository.findDataByShopId(shopId);

    ShopOperationInfoEntity shopOperationInfo = shopOperationInfoService.findByShopIdAndOperationDate(
        shopId, operationDate);
    boolean isOpen = isOpen(shopOperationInfo, nowZonedDateTime);
    boolean existsWaitingTeam = waitingService.existWaitingTeamByShopId(shopId, operationDate);

    return findOrderSettingsWithMenus(shopId, orderSettings, isOpen, existsWaitingTeam);
  }

  @Transactional
  public OrderSettingsResponse saveOrderSettings(String shopId, OrderSettingsServiceRequest request,
      LocalDate operationDate, ZonedDateTime nowZonedDateTime) {
    OrderSettingsEntity orderSettings = request.toEntity(shopId);
    OrderSettingsEntity savedOrderSettings = orderSettingsService.saveOrderSettings(orderSettings);

    boolean existsWaitingTeam = waitingService.validWaitingTeamExists(shopId, operationDate);
    ShopOperationInfoEntity shopOperationInfo = shopOperationInfoService.findByShopIdAndOperationDate(
        shopId, operationDate);
    boolean isOpen = isOpen(shopOperationInfo, nowZonedDateTime);

    return findOrderSettingsWithMenus(shopId, savedOrderSettings.getOrderSettingsData(), isOpen,
        existsWaitingTeam);
  }

  private OrderSettingsResponse findOrderSettingsWithMenus(String shopId,
      OrderSettingsData orderSettings, boolean isOpen, boolean existsWaitingTeam) {
    List<MenuNamePriceDto> menuDtos = sortByOrdering(menuQueryRepository.findMenuDtosBy(shopId));

    List<DisplayMenuNamePriceDto> shopMenuNamePriceDtos = sortByOrderingAndFilterDisplayMenus(
        displayMenuQueryRepository.findDisplayMenusBy(shopId, DisplayMappingType.SHOP));
    List<DisplayMenuNamePriceDto> takeOutMenuNamePriceDtos = sortByOrderingAndFilterDisplayMenus(
        displayMenuQueryRepository.findDisplayMenusBy(shopId, DisplayMappingType.TAKE_OUT));

    return OrderSettingsResponse.builder()
        .isPossibleOrder(orderSettings.isPossibleOrder())
        .menus(convert(menuDtos, OrderSettingsResponse.MenuDto::of))
        .shopMenus(convert(shopMenuNamePriceDtos, OrderSettingsResponse.MenuDto::of))
        .takeOutMenus(convert(takeOutMenuNamePriceDtos, OrderSettingsResponse.MenuDto::of))
        .isOpenedOperation(isOpen)
        .existsWaitingTeam(existsWaitingTeam)
        .build();
  }

  private List<MenuNamePriceDto> sortByOrdering(List<MenuNamePriceDto> menuDtos) {
    return menuDtos.stream()
        .sorted()
        .toList();
  }

  private List<DisplayMenuNamePriceDto> sortByOrderingAndFilterDisplayMenus(
      List<DisplayMenuNamePriceDto> menuNamePriceDtos) {
    return menuNamePriceDtos.stream()
        .filter(DisplayMenuNamePriceDto::isChecked)
        .sorted()
        .toList();
  }

  private static boolean isOpen(ShopOperationInfoEntity shopOperationInfo,
      ZonedDateTime nowZonedDateTime) {

    return OperationStatus.OPEN == OperationStatus.find(shopOperationInfo, nowZonedDateTime);
  }
}
