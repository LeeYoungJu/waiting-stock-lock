package co.wadcorp.waiting.api.internal.service.table;

import static co.wadcorp.libs.stream.StreamUtils.convert;

import co.wadcorp.waiting.api.internal.service.table.dto.response.RemoteTableSettingResponse;
import co.wadcorp.waiting.api.internal.service.table.dto.response.RemoteTableSettingResponse.ModeSettingsVO;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import co.wadcorp.waiting.data.query.settings.HomeSettingsQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RemoteTableApiService {

  private final HomeSettingsQueryRepository homeSettingsQueryRepository;

  public List<RemoteTableSettingResponse> findTableSettings(
      ChannelShopIdMapping channelShopIdMapping) {

    List<HomeSettingsEntity> homeSettingsEntities = homeSettingsQueryRepository.findByShopIds(
        channelShopIdMapping.getAllWaitingShopIds());

    return homeSettingsEntities.stream()
        .map(item -> {
          SeatOptions defaultModeSettings = item.getDefaultModeSettings();
          List<SeatOptions> tableModeSettings = item.getTableModeSettings();

          return RemoteTableSettingResponse.builder()
              .shopId(Long.valueOf(channelShopIdMapping.getChannelShopId(item.getShopId())))
              .waitingModeType(item.getWaitingModeType())
              .defaultModeSettings(
                  convertModeSettings(defaultModeSettings)
              )
              .tableModeSettings(
                  convert(tableModeSettings, RemoteTableApiService::convertModeSettings)
              )
              .build();
        })
        .toList();
  }

  private static ModeSettingsVO convertModeSettings(SeatOptions defaultModeSettings) {
    return ModeSettingsVO.builder()
        .id(defaultModeSettings.getId())
        .name(defaultModeSettings.getName())
        .minSeatCount(defaultModeSettings.getMinSeatCount())
        .maxSeatCount(defaultModeSettings.getMaxSeatCount())
        .expectedWaitingPeriod(defaultModeSettings.getExpectedWaitingPeriod())
        .isUsedExpectedWaitingPeriod(
            defaultModeSettings.getIsUsedExpectedWaitingPeriod()
        )
        .isTakeOut(defaultModeSettings.getIsTakeOut())
        .build();
  }

}
