package co.wadcorp.waiting.api.service.waiting.management;

import co.wadcorp.waiting.api.model.waiting.vo.ShopOperationInfoVO;
import co.wadcorp.waiting.api.model.waiting.vo.WaitingCurrentStatusVO;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementWaitingCurrentStatusResponse;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.query.settings.HomeSettingsQueryRepository;
import co.wadcorp.waiting.data.query.waiting.ShopOperationInfoQueryRepository;
import co.wadcorp.waiting.data.query.waiting.dto.ShopOperationInfoDto;
import co.wadcorp.waiting.data.service.waiting.TableCurrentStatusService;
import co.wadcorp.waiting.data.service.waiting.dto.TableCurrentStatusDto;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ManagementWaitingCurrentStatusApiService {

  private final TableCurrentStatusService tableCurrentStatusService;
  private final ShopOperationInfoQueryRepository shopOperationInfoQueryRepository;
  private final HomeSettingsQueryRepository homeSettingsQueryRepository;

  public ManagementWaitingCurrentStatusResponse getCurrentStatus(
      String shopId, LocalDate operationDate, ZonedDateTime nowDateTime
  ) {

    ShopOperationInfoDto shopOperationInfoDto = shopOperationInfoQueryRepository.selectShopOperationInfo(
        shopId, operationDate
    );

    HomeSettingsEntity homeSettingsEntity = homeSettingsQueryRepository.findByShopId(shopId);

    TableCurrentStatusDto tableCurrentStatusDto = tableCurrentStatusService.get(shopId,
        operationDate, homeSettingsEntity.getWaitingModeType());

    return ManagementWaitingCurrentStatusResponse.builder()
        .currentStatus(WaitingCurrentStatusVO.toDto(tableCurrentStatusDto))
        .operationInfo(ShopOperationInfoVO.toDto(shopOperationInfoDto, nowDateTime))
        .build();
  }
}
