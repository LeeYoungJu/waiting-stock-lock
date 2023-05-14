package co.wadcorp.waiting.data.query.settings;

import static org.assertj.core.api.Assertions.assertThat;

import co.wadcorp.waiting.IntegrationTest;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class HomeSettingsQueryRepositoryTest extends IntegrationTest {

  @Autowired
  private HomeSettingsQueryRepository homeSettingsQueryRepository;

  @Autowired
  private HomeSettingsRepository homeSettingsRepository;

  @DisplayName("매장 ID 리스트로 홈 세팅 리스트를 조회한다.")
  @Test
  void findByShopId() {
    // given
    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";
    String shopId3 = "shopId-3";
    String shopId4 = "shopId-4";

    HomeSettingsEntity homeSettings1 = createHomeSettings(shopId1);
    HomeSettingsEntity homeSettings2 = createHomeSettings(shopId2);
    HomeSettingsEntity homeSettings3 = createHomeSettings(shopId3);
    HomeSettingsEntity homeSettings4 = createHomeSettings(shopId4);

    homeSettings1.unPublish();
    homeSettingsRepository.save(homeSettings1);

    // when
    List<HomeSettingsEntity> results = homeSettingsQueryRepository.findByShopIds(
        List.of(shopId1, shopId2, shopId3));

    // then
    assertThat(results).hasSize(2)
        .extracting("shopId")
        .contains(shopId2, shopId3);
  }

  private HomeSettingsEntity createHomeSettings(String shopId) {
    HomeSettingsEntity homeSettings = HomeSettingsEntity.builder()
        .shopId(shopId)
        .homeSettingsData(HomeSettingsData.builder()
            .waitingModeType("DEFAULT")
            .defaultModeSettings(SeatOptions.builder()
                .expectedWaitingPeriod(5)
                .isUsedExpectedWaitingPeriod(true)
                .build()
            )
            .build()
        )
        .build();
    return homeSettingsRepository.save(homeSettings);
  }

}