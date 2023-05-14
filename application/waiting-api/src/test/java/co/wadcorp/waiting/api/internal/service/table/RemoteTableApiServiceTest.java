package co.wadcorp.waiting.api.internal.service.table;

import static org.assertj.core.api.Assertions.assertThat;

import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.internal.service.table.dto.response.RemoteTableSettingResponse;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.data.domain.settings.DefaultHomeSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsRepository;
import co.wadcorp.waiting.data.enums.WaitingModeType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RemoteTableApiServiceTest extends IntegrationTest {

  @Autowired
  private HomeSettingsRepository homeSettingsRepository;

  @Autowired
  private RemoteTableApiService remoteTableApiService;

  @DisplayName("매장 테이블 정보를 조회한다.")
  @Test
  void findTableSettings() {
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    String channelShopId = "5";
    String shopId = "shopId";
    channelShopIdMapping.put(shopId, channelShopId);

    homeSettingsRepository.save(new HomeSettingsEntity(shopId,
        DefaultHomeSettingDataFactory.create()));

    List<RemoteTableSettingResponse> tableSettings = remoteTableApiService.findTableSettings(
        channelShopIdMapping
    );

    assertThat(tableSettings.size()).isEqualTo(1);
    RemoteTableSettingResponse remoteTableSettingResponse = tableSettings.get(0);

    assertThat(remoteTableSettingResponse)
        .extracting(
            "shopId", "waitingModeType"
        )
        .containsExactly(Long.valueOf(channelShopId), WaitingModeType.DEFAULT);
  }

}