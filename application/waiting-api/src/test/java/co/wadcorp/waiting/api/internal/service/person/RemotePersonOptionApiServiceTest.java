package co.wadcorp.waiting.api.internal.service.person;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.internal.service.person.dto.response.RemotePersonOptionResponse;
import co.wadcorp.waiting.api.internal.service.table.RemoteTableApiService;
import co.wadcorp.waiting.api.internal.service.table.dto.response.RemoteTableSettingResponse;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.data.domain.settings.DefaultHomeSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.DefaultOptionSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsRepository;
import co.wadcorp.waiting.data.enums.WaitingModeType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
class RemotePersonOptionApiServiceTest extends IntegrationTest {

  @Autowired
  private OptionSettingsRepository optionSettingsRepository;

  @Autowired
  private RemotePersonOptionApiService remotePersonOptionApiService;

  @DisplayName("매장 인원 옵션 정보를 조회한다.")
  @Test
  void findPersonOptions() {
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    String channelShopId = "5";
    String shopId = "shopId";
    channelShopIdMapping.put(shopId, channelShopId);

    optionSettingsRepository.save(new OptionSettingsEntity(shopId,
        DefaultOptionSettingDataFactory.create()));

    List<RemotePersonOptionResponse> personOptions = remotePersonOptionApiService.findPersonOptions(
        channelShopIdMapping
    );

    assertThat(personOptions.size()).isEqualTo(1);
    RemotePersonOptionResponse remotePersonOptionResponse = personOptions.get(0);

    assertThat(remotePersonOptionResponse)
        .extracting(
            "shopId", "isUsedPersonOptionSetting"
        )
        .containsExactly(Long.valueOf(channelShopId), false);
  }
}