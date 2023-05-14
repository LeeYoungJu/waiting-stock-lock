package co.wadcorp.waiting.api.internal.service.table;

import co.wadcorp.waiting.api.internal.service.table.dto.response.RemoteTableStatusResponse;
import co.wadcorp.waiting.api.internal.service.table.dto.response.RemoteTableStatusResponse.TableCurrentStatusVO;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.data.domain.settings.DefaultHomeSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.enums.WaitingModeType;
import co.wadcorp.waiting.data.query.settings.HomeSettingsQueryRepository;
import co.wadcorp.waiting.data.service.waiting.TableCurrentStatusService;
import co.wadcorp.waiting.data.service.waiting.dto.TableCurrentStatusDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RemoteTableCurrentStatusApiService {

  private final TableCurrentStatusService tableCurrentStatusService;
  private final HomeSettingsQueryRepository homeSettingsQueryRepository;

  public List<RemoteTableStatusResponse> findTableCurrentStatus(
      ChannelShopIdMapping channelShopIdMapping, LocalDate operationDate) {
    List<String> shopIds = channelShopIdMapping.getAllWaitingShopIds();

    Map<String, HomeSettingsEntity> homeSettingsMap = createHomeSettingsMap(shopIds);

    return shopIds.stream()
        .map(shopId -> {
          WaitingModeType modeType = getModeType(homeSettingsMap, shopId);
          TableCurrentStatusDto currentStatusDto = tableCurrentStatusService.get(shopId,
              operationDate, modeType);

          return RemoteTableStatusResponse.builder()
              .shopId(Long.valueOf(channelShopIdMapping.getChannelShopId(shopId)))
              .totalTeamCount(currentStatusDto.getTeamCount())
              .currentStatus(currentStatusDto.getSeatsCurrentStatuses().stream()
                  .map(currentStatus -> TableCurrentStatusVO.builder()
                      .tableId(currentStatus.getId())
                      .tableName(currentStatus.getSeatOptionName())
                      .teamCount(currentStatus.getTeamCount())
                      .expectedWaitingTime(currentStatus.getExpectedWaitingTime())
                      .isUsedExpectedWaitingPeriod(currentStatus.getIsUsedExpectedWaitingPeriod())
                      .isTakeOut(currentStatus.getSeatOption().getIsTakeOut())
                      .build()
                  )
                  .toList()
              )
              .build();
        })
        .toList();
  }

  private Map<String, HomeSettingsEntity> createHomeSettingsMap(List<String> shopIds) {
    List<HomeSettingsEntity> homeSettingsEntities = homeSettingsQueryRepository.findByShopIds(
        shopIds);
    return homeSettingsEntities.stream()
        .collect(Collectors.toMap(HomeSettingsEntity::getShopId, e -> e, (e1, e2) -> e1));
  }

  private WaitingModeType getModeType(Map<String, HomeSettingsEntity> homeSettingsMap,
      String shopId) {
    HomeSettingsEntity homeSettings = homeSettingsMap.getOrDefault(shopId,
        new HomeSettingsEntity(shopId, DefaultHomeSettingDataFactory.create()));
    HomeSettingsData homeSettingsData = homeSettings.getHomeSettingsData();

    if (homeSettingsData.isDefaultMode()) {
      return WaitingModeType.DEFAULT;
    }
    return WaitingModeType.TABLE;
  }

}
