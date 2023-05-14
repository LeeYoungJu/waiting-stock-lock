package co.wadcorp.waiting.api.internal.service.waiting;

import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteWaitingOrderValidateRequest;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteWaitingOrderValidateServiceRequest;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteRegisterValidateMenuResponse;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteRegisterValidateMenuResponse.RemoteInvalidMenu;
import co.wadcorp.waiting.api.internal.service.waiting.validator.RemoteOrderMenuStockValidator;
import co.wadcorp.waiting.api.internal.service.waiting.validator.RemoteOrderMenuValidator;
import co.wadcorp.waiting.api.internal.service.waiting.validator.RemoteOrderSettingsValidator;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsData;
import co.wadcorp.waiting.data.domain.stock.InvalidStockMenu;
import co.wadcorp.waiting.data.domain.stock.MenuQuantity;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.validator.MenuStockValidator;
import co.wadcorp.waiting.data.domain.stock.validator.exception.StockException;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.service.menu.MenuService;
import co.wadcorp.waiting.data.service.settings.OrderSettingsService;
import co.wadcorp.waiting.data.service.stock.StockService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RemoteWaitingOrderValidateApiService {

  private final OrderSettingsService orderSettingsService;
  private final MenuService menuService;
  private final StockService stockService;

  /**
   * 원격 웨이팅 선주문 등록 데이터 validate
   * @param channelShopIdMapping
   * @param operationDate
   * @param request
   */
  public void validateOrderMenus(ChannelShopIdMapping channelShopIdMapping,
      LocalDate operationDate,
      RemoteWaitingOrderValidateServiceRequest request) {
    channelShopIdMapping.checkOnlyOneShopId();
    String shopId = channelShopIdMapping.getFirstWaitingShopId();

    OrderSettingsData orderSettings = orderSettingsService.getOrderSettings(shopId)
        .getOrderSettingsData();

    // 선주문 설정 검증
    RemoteOrderSettingsValidator.validate(orderSettings);

    // 메뉴 검증
    RemoteOrderMenuValidator.validate(request.getOrder());

    // 메뉴 재고 검증
    List<String> menuIds = request.getMenuIds();
    Map<String, MenuEntity> menuEntityMap = menuService.getMenuIdMenuEntityMap(menuIds);
    Map<String, StockEntity> menuStockMap = stockService.getMenuIdMenuStockMap(
        operationDate, menuIds);
    RemoteOrderMenuStockValidator.validate(request.getOrder(), menuEntityMap, menuStockMap);
  }

}
