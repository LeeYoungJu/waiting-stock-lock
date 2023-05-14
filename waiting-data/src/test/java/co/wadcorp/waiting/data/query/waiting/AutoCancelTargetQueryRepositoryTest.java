package co.wadcorp.waiting.data.query.waiting;

import static co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelProcessingStatus.CREATED;
import static co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelProcessingStatus.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;

import co.wadcorp.waiting.IntegrationTest;
import co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelProcessingStatus;
import co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelTargetEntity;
import co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelTargetRepository;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class AutoCancelTargetQueryRepositoryTest extends IntegrationTest {

  @Autowired
  private AutoCancelTargetQueryRepository autoCancelTargetQueryRepository;

  @Autowired
  private AutoCancelTargetRepository autoCancelTargetRepository;

  @DisplayName("현재 시간 이전에 생성된 자동 취소 대상을 조회한다.")
  @Test
  void findByExpectedTime() {
    // given
    ZonedDateTime now = ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 23, 18, 0));

    AutoCancelTargetEntity target1 = createTarget("waitingId-1", CREATED, now.minusSeconds(1));
    AutoCancelTargetEntity target2 = createTarget("waitingId-2", SUCCESS, now.minusSeconds(1));
    AutoCancelTargetEntity target3 = createTarget("waitingId-3", CREATED, now);
    autoCancelTargetRepository.saveAll(List.of(target1, target2, target3));

    // when
    List<AutoCancelTargetEntity> results = autoCancelTargetQueryRepository.findByExpectedTimeWithLimit(
        now, 100);

    // then
    assertThat(results).hasSize(1)
        .extracting("waitingId")
        .contains("waitingId-1");
  }

  @DisplayName("정해진 개수만큼 자동 취소 대상을 조회한다.")
  @Test
  void findByLimit() {
    // given
    ZonedDateTime now = ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 23, 18, 0));

    AutoCancelTargetEntity target1 = createTarget("waitingId-1", CREATED, now.minusSeconds(1));
    AutoCancelTargetEntity target2 = createTarget("waitingId-2", CREATED, now.minusSeconds(1));
    autoCancelTargetRepository.saveAll(List.of(target1, target2));

    // when
    List<AutoCancelTargetEntity> results = autoCancelTargetQueryRepository.findByExpectedTimeWithLimit(
        now, 1);

    // then
    assertThat(results).hasSize(1)
        .extracting("waitingId")
        .contains("waitingId-1");
  }

  private static AutoCancelTargetEntity createTarget(String waitingId,
      AutoCancelProcessingStatus processingStatus, ZonedDateTime expectedCancelDateTime) {
    return AutoCancelTargetEntity.builder()
        .shopId("shopId")
        .waitingId(waitingId)
        .processingStatus(processingStatus)
        .expectedCancelDateTime(expectedCancelDateTime)
        .build();
  }

}