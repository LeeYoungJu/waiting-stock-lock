package co.wadcorp.waiting.batch.job.waiting.expiration;

import static co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus.CANCEL_BY_SHOP;
import static co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus.EXPIRATION;
import static co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus.PUT_OFF;
import static co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus.SITTING;
import static co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.batch.job.BatchTestSupport;
import co.wadcorp.waiting.data.config.WaitingNumberConstructor;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import java.time.LocalDate;
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
class WaitingExpirationBatchConfigurationTest extends BatchTestSupport {

  @Autowired
  @Qualifier(WaitingExpirationBatchConfiguration.JOB_NAME)
  private Job job;

  @Autowired
  private WaitingRepository waitingRepository;

  @Autowired
  private WaitingHistoryRepository waitingHistoryRepository;

  @AfterEach
  void tearDown() {
    waitingRepository.deleteAllInBatch();
    waitingHistoryRepository.deleteAllInBatch();
  }

  @DisplayName("운영 시간 정보를 기반으로 웨이팅 운영 정보를 생성한다.")
  @Test
  void saveByBatch() throws Exception {
    // given
    String operationDateString = "2023-02-27";
    LocalDate operationDate = LocalDate.of(2023, 2, 27);

    createWaiting("shopId-1", operationDate, WaitingStatus.WAITING, WAITING);
    createWaiting("shopId-2", operationDate, WaitingStatus.WAITING, PUT_OFF);
    createWaiting("shopId-3", operationDate, WaitingStatus.SITTING, SITTING); // 대상 아님
    createWaiting("shopId-4", operationDate, WaitingStatus.CANCEL, CANCEL_BY_SHOP); // 대상 아님
    createWaiting("shopId-5", operationDate, WaitingStatus.EXPIRATION, EXPIRATION); // 대상 아님
    createWaiting("shopId-6", operationDate.plusDays(1), WaitingStatus.WAITING,
        WAITING); // 날짜 달라서 대상 아님

    JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParametersBuilder()
        .addString("operationDate", operationDateString)
        .toJobParameters();

    // when
    jobLauncherTestUtils.setJob(job);
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    List<WaitingEntity> waitings = waitingRepository.findAll();
    assertThat(waitings).hasSize(6)
        .extracting("shopId", "operationDate", "waitingStatus", "waitingDetailStatus")
        .containsExactlyInAnyOrder(
            tuple("shopId-1", operationDate, WaitingStatus.EXPIRATION, EXPIRATION),
            tuple("shopId-2", operationDate, WaitingStatus.EXPIRATION, EXPIRATION),
            tuple("shopId-3", operationDate, WaitingStatus.SITTING, SITTING),
            tuple("shopId-4", operationDate, WaitingStatus.CANCEL, CANCEL_BY_SHOP),
            tuple("shopId-5", operationDate, WaitingStatus.EXPIRATION, EXPIRATION),
            tuple("shopId-6", operationDate.plusDays(1), WaitingStatus.WAITING, WAITING)
        );

    List<WaitingHistoryEntity> waitingHistories = waitingHistoryRepository.findAll();
    assertThat(waitingHistories).hasSize(2)
        .extracting("shopId", "operationDate", "waitingStatus", "waitingDetailStatus")
        .containsExactlyInAnyOrder(
            tuple("shopId-1", operationDate, WaitingStatus.EXPIRATION, EXPIRATION),
            tuple("shopId-2", operationDate, WaitingStatus.EXPIRATION, EXPIRATION)
        );
  }

  private WaitingEntity createWaiting(
      String shopId, LocalDate operationDate, WaitingStatus waitingStatus,
      WaitingDetailStatus waitingDetailStatus) {
    WaitingEntity waiting = WaitingEntity.builder()
        .shopId(shopId)
        .waitingId(UUIDUtil.shortUUID().toUpperCase())
        .operationDate(operationDate)
        .waitingStatus(waitingStatus)
        .waitingDetailStatus(waitingDetailStatus)
        .seatOptionName("홀")
        .customerSeq(1L)
        .waitingCompleteDateTime(null)
        .waitingNumbers(WaitingNumber.builder()
            .waitingNumber(WaitingNumberConstructor.initWaitingNumber())
            .waitingOrder(1)
            .build()
        )
        .build();
    return waitingRepository.save(waiting);
  }

}