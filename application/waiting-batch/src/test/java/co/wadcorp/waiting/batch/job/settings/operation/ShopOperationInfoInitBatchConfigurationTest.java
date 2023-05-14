package co.wadcorp.waiting.batch.job.settings.operation;

import static co.wadcorp.waiting.data.enums.OperationDay.THURSDAY;
import static co.wadcorp.waiting.data.enums.OperationDay.TUESDAY;
import static co.wadcorp.waiting.data.enums.OperationDay.WEDNESDAY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import co.wadcorp.waiting.batch.job.BatchTestSupport;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.AutoPauseSettings;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.AutoPauseSettings.PauseReason;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.OperationTimeForDay;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.remote.RemoteOperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.remote.RemoteOperationTimeSettingsRepository;
import co.wadcorp.waiting.data.domain.shop.ShopEntity;
import co.wadcorp.waiting.data.domain.shop.ShopRepository;
import co.wadcorp.waiting.data.domain.shop.operation.status.RegistrableStatus;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoHistoryEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoHistoryRepository;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoRepository;
import co.wadcorp.waiting.data.enums.OperationDay;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
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
class ShopOperationInfoInitBatchConfigurationTest extends BatchTestSupport {

  @Autowired
  @Qualifier(ShopOperationInfoInitBatchConfiguration.JOB_NAME)
  private Job job;

  @Autowired
  private OperationTimeSettingsRepository operationTimeSettingsRepository;

  @Autowired
  private ShopOperationInfoRepository shopOperationInfoRepository;

  @Autowired
  private ShopOperationInfoHistoryRepository shopOperationInfoHistoryRepository;

  @Autowired
  private RemoteOperationTimeSettingsRepository remoteOperationTimeSettingsRepository;

  @Autowired
  private ShopRepository shopRepository;

  @AfterEach
  void tearDown() {
    operationTimeSettingsRepository.deleteAllInBatch();
    shopOperationInfoHistoryRepository.deleteAllInBatch();
    shopOperationInfoRepository.deleteAllInBatch();
    remoteOperationTimeSettingsRepository.deleteAllInBatch();
    shopRepository.deleteAllInBatch();
  }

  @DisplayName("운영 시간 정보를 기반으로 웨이팅 운영 정보를 생성한다.")
  @Test
  void saveByBatch() throws Exception {
    // given
    String operationDateString = "2023-02-15"; // 수요일
    String shopId = "shopId";
    LocalTime operationStartTime = LocalTime.of(10, 0);
    LocalTime operationEndTime = LocalTime.of(20, 0);
    LocalTime autoPauseStartTime = LocalTime.of(14, 0);
    LocalTime autoPauseEndTime = LocalTime.of(15, 0);
    String autoPauseReason = "웨이팅이 잠시 정지되었어요. 잠시만 기다려주세요.";

    createShop(shopId);

    operationTimeSettingsRepository.save(
        OperationTimeSettingsEntity.builder()
            .shopId(shopId)
            .operationTimeSettingsData(OperationTimeSettingsData.builder()
                .operationTimeForDays(List.of(OperationTimeForDay.builder()
                    .day(String.valueOf(WEDNESDAY))
                    .operationStartTime(operationStartTime)
                    .operationEndTime(operationEndTime)
                    .isClosedDay(false)
                    .build()
                ))
                .isUsedAutoPause(true)
                .autoPauseSettings(AutoPauseSettings.builder()
                    .autoPauseStartTime(autoPauseStartTime)
                    .autoPauseEndTime(autoPauseEndTime)
                    .pauseReasons(List.of(new PauseReason("pauseReasonId", true, autoPauseReason)))
                    .build()
                )
                .build()
            )
            .build()
    );

    JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParametersBuilder()
        .addString("operationStartDate", operationDateString)
        .addString("operationEndDate", operationDateString)
        .toJobParameters();

    // when
    jobLauncherTestUtils.setJob(job);
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    List<ShopOperationInfoEntity> entities = shopOperationInfoRepository.findAll();
    assertThat(entities).hasSize(1)
        .extracting("shopId", "operationDate", "registrableStatus",
            "operationStartDateTime", "operationEndDateTime",
            "manualPauseInfo.manualPauseStartDateTime", "manualPauseInfo.manualPauseEndDateTime",
            "manualPauseInfo.manualPauseReason",
            "autoPauseInfo.autoPauseStartDateTime", "autoPauseInfo.autoPauseEndDateTime",
            "autoPauseInfo.autoPauseReason", "closedReason"
        )
        .contains(
            tuple(shopId, LocalDate.of(2023, 2, 15), RegistrableStatus.OPEN,
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 15), operationStartTime),
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 15), operationEndTime),
                null, null, null,
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 15), autoPauseStartTime),
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 15), autoPauseEndTime),
                autoPauseReason,
                null
            )
        );

    List<ShopOperationInfoHistoryEntity> historyEntities = shopOperationInfoHistoryRepository.findAll();
    assertThat(historyEntities).hasSize(1)
        .extracting("shopId", "operationDate", "registrableStatus",
            "operationStartDateTime", "operationEndDateTime",
            "manualPauseInfo.manualPauseStartDateTime", "manualPauseInfo.manualPauseEndDateTime",
            "manualPauseInfo.manualPauseReason",
            "autoPauseInfo.autoPauseStartDateTime", "autoPauseInfo.autoPauseEndDateTime",
            "autoPauseInfo.autoPauseReason", "closedReason",
            "shopOperationInfoSeq"
        )
        .contains(
            tuple(shopId, LocalDate.of(2023, 2, 15), RegistrableStatus.OPEN,
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 15), operationStartTime),
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 15), operationEndTime),
                null, null, null,
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 15), autoPauseStartTime),
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 15), autoPauseEndTime),
                autoPauseReason,
                null,
                entities.get(0).getSeq()
            )
        );
  }

  @DisplayName("기존에 웨이팅 운영 정보가 존재한다면 신규 생성 시도 시 무시한다.")
  @Test
  void saveByBatchIfInfoAlreadyExists() throws Exception {
    // given
    String operationDateString = "2023-02-15"; // 수요일
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 2, 15);
    LocalTime operationStartTime = LocalTime.of(10, 0);
    LocalTime operationEndTime = LocalTime.of(20, 0);
    LocalTime autoPauseStartTime = LocalTime.of(14, 0);
    LocalTime autoPauseEndTime = LocalTime.of(15, 0);
    String autoPauseReason = "웨이팅이 잠시 정지되었어요. 잠시만 기다려주세요.";

    createShop(shopId);

    LocalTime existingOperationStartTime = LocalTime.of(11, 0);
    LocalTime existingOperationEndTime = LocalTime.of(21, 0);

    shopOperationInfoRepository.save(
        ShopOperationInfoEntity.builder()
            .shopId(shopId)
            .operationDate(operationDate)
            .operationStartDateTime(
                ZonedDateTimeUtils.ofSeoul(operationDate, existingOperationStartTime)
            )
            .operationEndDateTime(
                ZonedDateTimeUtils.ofSeoul(operationDate, existingOperationEndTime)
            )
            .registrableStatus(RegistrableStatus.OPEN)
            .build()
    );

    operationTimeSettingsRepository.save(
        OperationTimeSettingsEntity.builder()
            .shopId(shopId)
            .operationTimeSettingsData(OperationTimeSettingsData.builder()
                .operationTimeForDays(List.of(OperationTimeForDay.builder()
                    .day(String.valueOf(WEDNESDAY))
                    .operationStartTime(operationStartTime)
                    .operationEndTime(operationEndTime)
                    .isClosedDay(false)
                    .build()
                ))
                .isUsedAutoPause(true)
                .autoPauseSettings(AutoPauseSettings.builder()
                    .autoPauseStartTime(autoPauseStartTime)
                    .autoPauseEndTime(autoPauseEndTime)
                    .pauseReasons(List.of(new PauseReason("pauseReasonId", true, autoPauseReason)))
                    .build()
                )
                .build()
            )
            .build()
    );

    JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParametersBuilder()
        .addString("operationStartDate", operationDateString)
        .addString("operationEndDate", operationDateString)
        .toJobParameters();

    // when
    jobLauncherTestUtils.setJob(job);
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    List<ShopOperationInfoEntity> entities = shopOperationInfoRepository.findAll();
    assertThat(entities).hasSize(1)
        .extracting("shopId", "operationDate", "registrableStatus",
            "operationStartDateTime", "operationEndDateTime",
            "manualPauseStartDateTime", "manualPauseEndDateTime", "manualPauseReason",
            "autoPauseStartDateTime", "autoPauseEndDateTime", "autoPauseReason", "closedReason"
        )
        .contains(
            tuple(shopId, operationDate, RegistrableStatus.OPEN,
                ZonedDateTimeUtils.ofSeoul(operationDate, existingOperationStartTime),
                ZonedDateTimeUtils.ofSeoul(operationDate, existingOperationEndTime),
                null, null, null,
                null, null, null,
                null
            )
        );
  }

  @DisplayName("기간을 지정하여 웨이팅 운영 정보를 생성할 수 있다. 운영 정보가 이미 존재하는 날짜는 스킵한다.")
  @Test
  void saveByPeriod() throws Exception {
    // given
    String operationStartDateString = "2023-02-14"; // 화요일
    String operationEndDateString = "2023-02-16"; // 목요일
    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";

    createShop(shopId1);
    createShop(shopId2);

    createOperationTimeSettings(shopId1, List.of(
        createOperationTimeForDay(TUESDAY, LocalTime.of(9, 0), LocalTime.of(19, 0)),
        createOperationTimeForDay(WEDNESDAY, LocalTime.of(10, 0), LocalTime.of(20, 0)),
        createOperationTimeForDay(THURSDAY, LocalTime.of(11, 0), LocalTime.of(21, 0))
    ));
    createOperationTimeSettings(shopId2, List.of(
        createOperationTimeForDay(TUESDAY, LocalTime.of(9, 0), LocalTime.of(19, 0)),
        createOperationTimeForDay(WEDNESDAY, LocalTime.of(10, 0), LocalTime.of(20, 0)),
        createOperationTimeForDay(THURSDAY, LocalTime.of(11, 0), LocalTime.of(21, 0))
    ));

    shopOperationInfoRepository.save( // shopId1 화요일 정보는 이미 존재
        ShopOperationInfoEntity.builder()
            .shopId(shopId1)
            .operationDate(LocalDate.of(2023, 2, 14))
            .operationStartDateTime(
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 14), LocalTime.of(8, 0))
            )
            .operationEndDateTime(
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 14), LocalTime.of(18, 0))
            )
            .registrableStatus(RegistrableStatus.OPEN)
            .build()
    );

    JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParametersBuilder()
        .addString("operationStartDate", operationStartDateString)
        .addString("operationEndDate", operationEndDateString)
        .toJobParameters();

    // when
    jobLauncherTestUtils.setJob(job);
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    List<ShopOperationInfoEntity> entities = shopOperationInfoRepository.findAll();
    assertThat(entities).hasSize(6)
        .extracting("shopId", "operationDate", "registrableStatus",
            "operationStartDateTime", "operationEndDateTime"
        )
        .containsExactlyInAnyOrder(
            tuple(shopId1, LocalDate.of(2023, 2, 14), RegistrableStatus.OPEN,
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 14), LocalTime.of(8, 0)),
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 14), LocalTime.of(18, 0))
            ),
            tuple(shopId1, LocalDate.of(2023, 2, 15), RegistrableStatus.OPEN,
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 15), LocalTime.of(10, 0)),
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 15), LocalTime.of(20, 0))
            ),
            tuple(shopId1, LocalDate.of(2023, 2, 16), RegistrableStatus.OPEN,
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 16), LocalTime.of(11, 0)),
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 16), LocalTime.of(21, 0))
            ),
            tuple(shopId2, LocalDate.of(2023, 2, 14), RegistrableStatus.OPEN,
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 14), LocalTime.of(9, 0)),
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 14), LocalTime.of(19, 0))
            ),
            tuple(shopId2, LocalDate.of(2023, 2, 15), RegistrableStatus.OPEN,
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 15), LocalTime.of(10, 0)),
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 15), LocalTime.of(20, 0))
            ),
            tuple(shopId2, LocalDate.of(2023, 2, 16), RegistrableStatus.OPEN,
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 16), LocalTime.of(11, 0)),
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 16), LocalTime.of(21, 0))
            )
        );
  }

  @DisplayName("실수로 활성화된 운영 시간 정보가 2개 이상 존재하더라도 문제 없이 운영 정보를 생성한다.")
  @Test
  void saveWithDuplicateTimeSettings() throws Exception {
    // given
    String operationDateString = "2023-02-15"; // 수요일
    String shopId = "shopId";

    createShop(shopId);

    createOperationTimeSettings(shopId, List.of(
        createOperationTimeForDay(WEDNESDAY, LocalTime.of(10, 0), LocalTime.of(20, 0))
    ));
    createOperationTimeSettings(shopId, List.of(
        createOperationTimeForDay(WEDNESDAY, LocalTime.of(10, 0), LocalTime.of(20, 0))
    ));

    JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParametersBuilder()
        .addString("operationStartDate", operationDateString)
        .addString("operationEndDate", operationDateString)
        .toJobParameters();

    // when
    jobLauncherTestUtils.setJob(job);
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    List<ShopOperationInfoEntity> entities = shopOperationInfoRepository.findAll();
    assertThat(entities).hasSize(1)
        .extracting("shopId", "operationDate", "registrableStatus",
            "operationStartDateTime", "operationEndDateTime"
        )
        .contains(
            tuple(shopId, LocalDate.of(2023, 2, 15), RegistrableStatus.OPEN,
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 15), LocalTime.of(10, 0)),
                ZonedDateTimeUtils.ofSeoul(LocalDate.of(2023, 2, 15), LocalTime.of(20, 0))
            )
        );
  }

  @DisplayName("원격 운영 시간 정보가 존재하는 매장이라면 해당 정보도 운영 정보에 넣는다.")
  @Test
  void saveWithRemoteOperationTimeSettings() throws Exception {
    // given
    String operationDateString = "2023-02-15"; // 수요일
    LocalDate operationDate = LocalDate.of(2023, 2, 15);
    String shopId = "shopId";

    createShop(shopId);

    createOperationTimeSettings(shopId,
        List.of(createOperationTimeForDay(WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(23, 0))),
        LocalTime.of(13, 0), LocalTime.of(16, 0)
    );

    remoteOperationTimeSettingsRepository.save(RemoteOperationTimeSettingsEntity.builder()
        .shopId(shopId)
        .operationDay(WEDNESDAY)
        .isClosedDay(false)
        .operationStartTime(LocalTime.of(10, 0))
        .operationEndTime(LocalTime.of(22, 0))
        .isUsedAutoPause(true)
        .autoPauseStartTime(LocalTime.of(14, 0))
        .autoPauseEndTime(LocalTime.of(15, 0))
        .build()
    );

    JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParametersBuilder()
        .addString("operationStartDate", operationDateString)
        .addString("operationEndDate", operationDateString)
        .toJobParameters();

    // when
    jobLauncherTestUtils.setJob(job);
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    List<ShopOperationInfoEntity> entities = shopOperationInfoRepository.findAll();
    assertThat(entities).hasSize(1)
        .extracting(
            "remoteOperationStartDateTime", "remoteOperationEndDateTime",
            "remoteAutoPauseInfo.remoteAutoPauseStartDateTime",
            "remoteAutoPauseInfo.remoteAutoPauseEndDateTime"
        )
        .contains(
            tuple(
                ZonedDateTimeUtils.ofSeoul(operationDate, LocalTime.of(10, 0)),
                ZonedDateTimeUtils.ofSeoul(operationDate, LocalTime.of(22, 0)),
                ZonedDateTimeUtils.ofSeoul(operationDate, LocalTime.of(14, 0)),
                ZonedDateTimeUtils.ofSeoul(operationDate, LocalTime.of(15, 0))
            )
        );
  }

  @DisplayName("원격 운영 시간 정보가 존재하지 않는 매장이라면 원격 시간정보 컬럼은 현장 시간정보로 갈음한다.")
  @Test
  void saveWithoutRemoteOperationTimeSettings() throws Exception {
    // given
    String operationDateString = "2023-02-15"; // 수요일
    LocalDate operationDate = LocalDate.of(2023, 2, 15);
    String shopId = "shopId";

    createShop(shopId);

    createOperationTimeSettings(shopId,
        List.of(createOperationTimeForDay(WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(23, 0))),
        LocalTime.of(13, 0), LocalTime.of(16, 0)
    );

    JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParametersBuilder()
        .addString("operationStartDate", operationDateString)
        .addString("operationEndDate", operationDateString)
        .toJobParameters();

    // when
    jobLauncherTestUtils.setJob(job);
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    List<ShopOperationInfoEntity> entities = shopOperationInfoRepository.findAll();
    assertThat(entities).hasSize(1)
        .extracting(
            "remoteOperationStartDateTime", "remoteOperationEndDateTime",
            "remoteAutoPauseInfo.remoteAutoPauseStartDateTime",
            "remoteAutoPauseInfo.remoteAutoPauseEndDateTime"
        )
        .contains(
            tuple(
                ZonedDateTimeUtils.ofSeoul(operationDate, LocalTime.of(9, 0)),
                ZonedDateTimeUtils.ofSeoul(operationDate, LocalTime.of(23, 0)),
                ZonedDateTimeUtils.ofSeoul(operationDate, LocalTime.of(13, 0)),
                ZonedDateTimeUtils.ofSeoul(operationDate, LocalTime.of(16, 0))
            )
        );
  }

  private ShopEntity createShop(String shopId) {
    ShopEntity shop = ShopEntity.builder()
        .shopId(shopId)
        .shopName("shopName")
        .shopAddress("shopAddress")
        .shopTelNumber("shopTelNumber")
        .build();
    return shopRepository.save(shop);
  }

  private OperationTimeSettingsEntity createOperationTimeSettings(String shopId,
      List<OperationTimeForDay> operationTimeForDays) {
    OperationTimeSettingsEntity operationTimeSettings = OperationTimeSettingsEntity.builder()
        .shopId(shopId)
        .operationTimeSettingsData(OperationTimeSettingsData.builder()
            .operationTimeForDays(operationTimeForDays)
            .isUsedAutoPause(false)
            .build()
        )
        .build();
    return operationTimeSettingsRepository.save(operationTimeSettings);
  }

  private OperationTimeSettingsEntity createOperationTimeSettings(String shopId,
      List<OperationTimeForDay> operationTimeForDays,
      LocalTime autoPauseStartTime, LocalTime autoPauseEndTime) {
    OperationTimeSettingsEntity operationTimeSettings = OperationTimeSettingsEntity.builder()
        .shopId(shopId)
        .operationTimeSettingsData(OperationTimeSettingsData.builder()
            .operationTimeForDays(operationTimeForDays)
            .isUsedAutoPause(true)
            .autoPauseSettings(AutoPauseSettings.builder()
                .autoPauseStartTime(autoPauseStartTime)
                .autoPauseEndTime(autoPauseEndTime)
                .pauseReasons(List.of())
                .build()
            )
            .build()
        )
        .build();
    return operationTimeSettingsRepository.save(operationTimeSettings);
  }

  private OperationTimeForDay createOperationTimeForDay(OperationDay operationDay,
      LocalTime operationStartTime, LocalTime operationEndTime) {
    return OperationTimeForDay.builder()
        .day(String.valueOf(operationDay))
        .operationStartTime(operationStartTime)
        .operationEndTime(operationEndTime)
        .isClosedDay(false)
        .build();
  }

}