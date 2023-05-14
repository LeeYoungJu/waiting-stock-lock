package co.wadcorp.waiting.data.query.waiting;

import static co.wadcorp.waiting.data.domain.waiting.WaitingStatus.CANCEL;
import static co.wadcorp.waiting.data.domain.waiting.WaitingStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import co.wadcorp.waiting.IntegrationTest;
import co.wadcorp.waiting.data.config.WaitingNumberConstructor;
import co.wadcorp.waiting.data.domain.waiting.PersonOption;
import co.wadcorp.waiting.data.domain.waiting.PersonOptionsData;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingHistoryDetailStatusDto;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class WaitingHistoryQueryRepositoryTest extends IntegrationTest {

  @Autowired
  private WaitingHistoryQueryRepository waitingHistoryQueryRepository;

  @Autowired
  private WaitingHistoryRepository waitingHistoryRepository;

  @DisplayName("특정 웨이팅의 최신 변화 이력을 정해진 개수만큼 조회한다.")
  @Test
  void findLastWaitingHistoryDetailStatuses() {
    // given
    String waitingId = "waitingId-1";
    LocalDate operationDate = LocalDate.of(2023, 3, 15);

    createWaitingHistory(waitingId, operationDate, WAITING, WaitingDetailStatus.WAITING);
    createWaitingHistory(waitingId, operationDate, CANCEL, WaitingDetailStatus.CANCEL_BY_CUSTOMER);
    createWaitingHistory(waitingId, operationDate, WAITING, WaitingDetailStatus.UNDO);
    createWaitingHistory("waitingId-2", operationDate, WAITING, WaitingDetailStatus.WAITING);

    // when
    List<WaitingHistoryDetailStatusDto> results = waitingHistoryQueryRepository.findLastWaitingHistoryDetailStatuses(
        waitingId, 2);

    // then
    assertThat(results).hasSize(2)
        .extracting("waitingId", "waitingStatus", "waitingDetailStatus")
        .containsExactlyInAnyOrder(
            tuple(waitingId, WAITING, WaitingDetailStatus.UNDO),
            tuple(waitingId, CANCEL, WaitingDetailStatus.CANCEL_BY_CUSTOMER)
        );
  }

  private WaitingHistoryEntity createWaitingHistory(String waitingId, LocalDate operationDate,
      WaitingStatus waitingStatus, WaitingDetailStatus waitingDetailStatus) {
    WaitingHistoryEntity history = WaitingHistoryEntity.builder()
        .shopId("shopId")
        .waitingSeq(1L)
        .waitingId(waitingId)
        .operationDate(operationDate)
        .waitingStatus(waitingStatus)
        .waitingDetailStatus(waitingDetailStatus)
        .seatOptionName("홀")
        .customerSeq(1L)
        .waitingCompleteDateTime(null)
        .waitingNumbers(
            WaitingNumber.builder()
                .waitingNumber(WaitingNumberConstructor.initWaitingNumber())
                .waitingOrder(1)
                .build()
        )
        .personOptionsData(PersonOptionsData.builder()
            .personOptions(List.of(PersonOption.builder()
                .name("유아")
                .count(1)
                .build()
            ))
            .build()
        )
        .build();
    return waitingHistoryRepository.save(history);
  }


}