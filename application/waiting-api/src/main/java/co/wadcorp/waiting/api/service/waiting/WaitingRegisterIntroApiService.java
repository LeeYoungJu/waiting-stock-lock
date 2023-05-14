package co.wadcorp.waiting.api.service.waiting;

import co.wadcorp.waiting.api.model.settings.response.RegisterSettingsResponse;
import co.wadcorp.waiting.api.model.waiting.response.RegisterCurrentStatusResponse;
import co.wadcorp.waiting.api.model.waiting.vo.ShopOperationInfoVO;
import co.wadcorp.waiting.api.model.waiting.vo.WaitingCurrentStatusVO;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsData;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsData;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInitializeFactory;
import co.wadcorp.waiting.data.enums.WaitingModeType;
import co.wadcorp.waiting.data.query.settings.OrderSettingsQueryRepository;
import co.wadcorp.waiting.data.query.waiting.ShopOperationInfoQueryRepository;
import co.wadcorp.waiting.data.query.waiting.dto.ShopOperationInfoDto;
import co.wadcorp.waiting.data.service.settings.HomeSettingsService;
import co.wadcorp.waiting.data.service.settings.OperationTimeSettingsService;
import co.wadcorp.waiting.data.service.settings.OptionSettingsService;
import co.wadcorp.waiting.data.service.settings.PrecautionSettingsService;
import co.wadcorp.waiting.data.service.waiting.ShopOperationInfoService;
import co.wadcorp.waiting.data.service.waiting.TableCurrentStatusService;
import co.wadcorp.waiting.data.service.waiting.dto.TableCurrentStatusDto;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WaitingRegisterIntroApiService {

  private final OperationTimeSettingsService operationTimeSettingsService;
  private final ShopOperationInfoService shopOperationInfoService;

  private final HomeSettingsService homeSettingsService;
  private final OptionSettingsService optionSettingsService;
  private final PrecautionSettingsService precautionSettingsService;
  private final OrderSettingsQueryRepository orderSettingsQueryRepository;

  private final ShopOperationInfoQueryRepository shopOperationInfoQueryRepository;

  private final TableCurrentStatusService tableCurrentStatusService;

  public RegisterCurrentStatusResponse getDefaultCurrentStatus(
      String shopId, LocalDate operationDate, ZonedDateTime nowDateTime
  ) {
    TableCurrentStatusDto defaultCurrentStatusDto = tableCurrentStatusService.get(shopId,
        operationDate, WaitingModeType.DEFAULT);
    ShopOperationInfoDto shopOperationInfoDto = getShopOperationInfoDto(shopId, operationDate
    );

    return RegisterCurrentStatusResponse.builder()
        .currentStatus(WaitingCurrentStatusVO.toDto(defaultCurrentStatusDto))
        .operationInfo(ShopOperationInfoVO.toDto(shopOperationInfoDto, nowDateTime))
        .build();
  }

  public RegisterCurrentStatusResponse getTableCurrentStatus(String shopId, LocalDate operationDate,
      ZonedDateTime nowDateTime) {

    TableCurrentStatusDto tableCurrentStatusDto = tableCurrentStatusService.get(shopId,
        operationDate, WaitingModeType.TABLE);

    ShopOperationInfoDto shopOperationInfoDto = getShopOperationInfoDto(shopId, operationDate
    );

    return RegisterCurrentStatusResponse.builder()
        .currentStatus(WaitingCurrentStatusVO.toDto(tableCurrentStatusDto))
        .operationInfo(ShopOperationInfoVO.toDto(shopOperationInfoDto, nowDateTime))
        .build();
  }


  @Transactional(readOnly = true)
  public RegisterSettingsResponse getAllRegisterSettings(String shopId) {
    HomeSettingsData homeSettings = homeSettingsService.getHomeSettings(shopId)
        .getHomeSettingsData();
    OptionSettingsData optionSettings = optionSettingsService.getOptionSettings(shopId)
        .getOptionSettingsData();

    PrecautionSettingsData precautionSettingsData = precautionSettingsService.getPrecautionSettings(
            shopId)
        .getPrecautionSettingsData();

    OrderSettingsData orderSettingsData = orderSettingsQueryRepository.findDataByShopId(
        shopId);

    // TODO 목 전용 객체 사용
    return RegisterSettingsResponse.toDto(homeSettings, optionSettings, precautionSettingsData,
        orderSettingsData);
  }

  private ShopOperationInfoDto getShopOperationInfoDto(String shopId, LocalDate operationDate) {

    ShopOperationInfoDto shopOperationInfoDto = shopOperationInfoQueryRepository.selectShopOperationInfo(
        shopId, operationDate);

    if (Objects.nonNull(shopOperationInfoDto)) {
      return shopOperationInfoDto;
    }

    OperationTimeSettingsEntity operationTimeSettings = operationTimeSettingsService.getOperationTimeSettings(
        shopId);

    ShopOperationInfoEntity shopOperationInfoEntity = ShopOperationInitializeFactory.initialize(
        operationTimeSettings, operationDate);

    ShopOperationInfoEntity save = shopOperationInfoService.save(shopOperationInfoEntity);

    return ShopOperationInfoDto.builder()
        .operationDate(save.getOperationDate())
        .registrableStatus(save.getRegistrableStatus())
        .operationStartDateTime(save.getOperationStartDateTime())
        .operationEndDateTime(save.getOperationEndDateTime())
        .manualPauseStartDateTime(save.getManualPauseStartDateTime())
        .manualPauseEndDateTime(save.getManualPauseEndDateTime())
        .manualPauseReasonId(save.getManualPauseReasonId())
        .manualPauseReason(save.getManualPauseReason())
        .autoPauseStartDateTime(save.getAutoPauseStartDateTime())
        .autoPauseEndDateTime(save.getAutoPauseEndDateTime())
        .autoPauseReasonId(save.getAutoPauseReasonId())
        .autoPauseReason(save.getAutoPauseReason())
        .closedReason(save.getClosedReason())
        .build();
  }
}
