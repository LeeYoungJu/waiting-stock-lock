package co.wadcorp.waiting.batch.job.temp;

import static org.assertj.core.api.Assertions.assertThat;

import co.wadcorp.waiting.batch.job.BatchTestSupport;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import co.wadcorp.waiting.data.support.HomeSettingsConverter;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "chunkSize=1")
class HomeSettingsJsonFieldAppendBatchConfigurationTest extends BatchTestSupport {

  @Autowired
  @Qualifier(HomeSettingsJsonFieldAppendBatchConfiguration.JOB_NAME)
  private Job job;

  @Autowired
  private HomeSettingsRepository homeSettingsRepository;

  @AfterEach
  void tearDown() {
    homeSettingsRepository.deleteAllInBatch();
  }

  @Disabled
  @DisplayName("isPickup -> isTakeOut 마이그레이션")
  @Test
  void migration() throws Exception {
    // given
    String json = """
        {"waitingModeType":"TABLE","defaultModeSettings":{"id":"7oqN4jesQuufPMCLmgVuug","name":"착석","minSeatCount":2,"maxSeatCount":10,"expectedWaitingPeriod":5,"isUsedExpectedWaitingPeriod":true,"isDefault":true,"isPickup":true,"notUseExpectedWaitingPeriod":false},"tableModeSettings":[{"id":"tZCcLfzXQyGI6hKo8tVLrw","name":"웨이띵","minSeatCount":1,"maxSeatCount":5,"expectedWaitingPeriod":5,"isUsedExpectedWaitingPeriod":true,"isDefault":true,"isPickup":true,"notUseExpectedWaitingPeriod":false},{"id":"XVxg4bLoTcGO5qtrhqZ57A","name":"웨잇딩","minSeatCount":1,"maxSeatCount":10,"expectedWaitingPeriod":7,"isUsedExpectedWaitingPeriod":true,"isDefault":true,"isPickup":true,"notUseExpectedWaitingPeriod":false},{"id":"1vj8hVBC9Ddr6sHLBfBvgS","name":"웨익띵","minSeatCount":2,"maxSeatCount":8,"expectedWaitingPeriod":5,"isUsedExpectedWaitingPeriod":true,"isDefault":false,"isPickup":true,"notUseExpectedWaitingPeriod":false},{"id":"dp1zbixKZz442YHbzr22Tx","name":"웨이래","minSeatCount":2,"maxSeatCount":10,"expectedWaitingPeriod":5,"isUsedExpectedWaitingPeriod":true,"isDefault":false,"isPickup":true,"notUseExpectedWaitingPeriod":false}],"defaultMode":false}
        """; // 모든 isPickup이 true다.
    HomeSettingsConverter homeSettingsConverter = new HomeSettingsConverter();

    HomeSettingsEntity homeSettings1 = createHomeSettings(json, homeSettingsConverter);
    HomeSettingsEntity homeSettings2 = createHomeSettings(json, homeSettingsConverter);
    HomeSettingsEntity homeSettings3 = createHomeSettings(json, homeSettingsConverter);
    HomeSettingsEntity homeSettings4 = createHomeSettings(json, homeSettingsConverter);

    JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParametersBuilder()
        .toJobParameters();
    jobLauncherTestUtils.setJob(job);

    // when
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    List<HomeSettingsEntity> entities = homeSettingsRepository.findAll();
    assertThat(entities).hasSize(4);

    for (HomeSettingsEntity entity : entities) {
      SeatOptions defaultModeSettings = entity.getDefaultModeSettings();
      assertThat(defaultModeSettings.getIsTakeOut()).isTrue();

      List<SeatOptions> tableModeSettings = entity.getTableModeSettings();
      for (SeatOptions tableModeSetting : tableModeSettings) {
        assertThat(tableModeSetting.getIsTakeOut()).isTrue();
      }
    }
  }

  private HomeSettingsEntity createHomeSettings(String json,
      HomeSettingsConverter homeSettingsConverter) {
    HomeSettingsEntity entity = HomeSettingsEntity.builder()
        .shopId("shopId")
        .homeSettingsData(homeSettingsConverter.convertToEntityAttribute(json))
        .build();
    return homeSettingsRepository.save(entity);
  }

}