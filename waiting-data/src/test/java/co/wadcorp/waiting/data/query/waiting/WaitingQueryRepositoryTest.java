package co.wadcorp.waiting.data.query.waiting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.IntegrationTest;
import co.wadcorp.waiting.data.config.WaitingNumberConstructor;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingCurrentStatusCountDto;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class WaitingQueryRepositoryTest extends IntegrationTest {

  @Autowired
  private WaitingQueryRepository waitingQueryRepository;

  @Autowired
  private WaitingRepository waitingRepository;

  @DisplayName("매장 ID 리스트와 운영일 날짜로 현재 웨이팅 현황을 조회한다.")
  @Test
  void findCurrentWaitingStatuses() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 3, 9);

    createWaiting("shopId-1", operationDate, WaitingStatus.WAITING, WaitingDetailStatus.WAITING, 3,
        "홀");
    createWaiting("shopId-2", operationDate, WaitingStatus.WAITING, WaitingDetailStatus.WAITING, 3,
        "홀");
    createWaiting("shopId-3", operationDate.minusDays(1), WaitingStatus.WAITING,
        WaitingDetailStatus.WAITING, 3, "홀");
    createWaiting("shopId-4", operationDate, WaitingStatus.SITTING, WaitingDetailStatus.SITTING, 3,
        "홀");

    // when
    List<WaitingCurrentStatusCountDto> results = waitingQueryRepository.findCurrentWaitingStatuses(
        List.of("shopId-1", "shopId-2", "shopId-3", "shopId-4"), operationDate);

    // then
    assertThat(results).hasSize(2)
        .extracting("shopId", "totalPersonCount", "seatOptionName")
        .containsExactlyInAnyOrder(
            tuple("shopId-1", 3, "홀"),
            tuple("shopId-2", 3, "홀")
        );
  }

  private WaitingEntity createWaiting(
      String shopId, LocalDate operationDate, WaitingStatus waitingStatus,
      WaitingDetailStatus waitingDetailStatus, int totalPersonCount, String seatOptionName) {
    WaitingEntity waiting = WaitingEntity.builder()
        .shopId(shopId)
        .waitingId(UUIDUtil.shortUUID().toUpperCase())
        .operationDate(operationDate)
        .waitingStatus(waitingStatus)
        .waitingDetailStatus(waitingDetailStatus)
        .seatOptionName(seatOptionName)
        .totalPersonCount(totalPersonCount)
        .customerSeq(1L)
        .waitingCompleteDateTime(null)
        .waitingNumbers(
            WaitingNumber.builder()
                .waitingNumber(WaitingNumberConstructor.initWaitingNumber())
                .waitingOrder(1)
                .build()
        )
        .build();
    return waitingRepository.save(waiting);
  }

}