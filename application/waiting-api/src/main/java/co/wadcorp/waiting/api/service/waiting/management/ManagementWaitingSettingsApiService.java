package co.wadcorp.waiting.api.service.waiting.management;

import co.wadcorp.waiting.api.model.settings.response.ManagementSettingsResponse;
import co.wadcorp.waiting.api.model.settings.vo.AlarmSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.HomeSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.OperationTimeSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.OptionSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.OrderSettingsManagementVO;
import co.wadcorp.waiting.data.domain.settings.AlarmSettingsData;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsData;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.query.menu.MenuQueryRepository;
import co.wadcorp.waiting.data.query.settings.OrderSettingsQueryRepository;
import co.wadcorp.waiting.data.query.stock.StockQueryRepository;
import co.wadcorp.waiting.data.service.settings.AlarmSettingsService;
import co.wadcorp.waiting.data.service.settings.HomeSettingsService;
import co.wadcorp.waiting.data.service.settings.OperationTimeSettingsService;
import co.wadcorp.waiting.data.service.settings.OptionSettingsService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ManagementWaitingSettingsApiService {

  private final HomeSettingsService homeSettingsService;
  private final OptionSettingsService optionSettingsService;
  private final OperationTimeSettingsService operationTimeSettingsService;
  private final AlarmSettingsService alarmSettingsService;
  private final OrderSettingsQueryRepository orderSettingsQueryRepository;
  private final MenuQueryRepository menuQueryRepository;
  private final StockQueryRepository stockQueryRepository;

  public ManagementSettingsResponse getAllManagementSettings(String shopId,
      LocalDate operationDate) {
    HomeSettingsData homeSettings = homeSettingsService.getHomeSettings(shopId)
        .getHomeSettingsData();
    OptionSettingsData optionSettings = optionSettingsService.getOptionSettings(shopId)
        .getOptionSettingsData();
    OperationTimeSettingsData operationTimeSettings = operationTimeSettingsService
        .getOperationTimeSettings(shopId)
        .getOperationTimeSettingsData();
    AlarmSettingsData alarmSettings = alarmSettingsService.getAlarmSettings(shopId)
        .getAlarmSettingsData();
    OrderSettingsData orderSettings = orderSettingsQueryRepository.findDataByShopId(shopId);

    return ManagementSettingsResponse.builder()
        .homeSettings(HomeSettingsVO.toDto(homeSettings))
        .optionSettings(OptionSettingsVO.toDto(optionSettings))
        .operationTimeSettings(OperationTimeSettingsVO.toDto(operationTimeSettings))
        .alarmSettings(AlarmSettingsVO.toDto(alarmSettings))
        .orderSettings(OrderSettingsManagementVO.toDto(
            orderSettings,
            countOfMenusUnderStockThreshold(shopId, operationDate)
        ))
        .build();
  }

  private long countOfMenusUnderStockThreshold(String shopId, LocalDate operationDate) {
    List<String> menuIds = menuQueryRepository.findAllMenuIdsBy(shopId);
    List<StockEntity> stocks = stockQueryRepository.findAllUsedStocksBy(menuIds, operationDate);

    return stocks.stream()
        .filter(StockEntity::isNotOutOfStock)
        .filter(StockEntity::isStockUnderThreshold)
        .count();
  }

}
