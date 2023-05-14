package co.wadcorp.waiting.data.query.waiting;

import static co.wadcorp.waiting.data.domain.waiting.WaitingStatus.SITTING;
import static co.wadcorp.waiting.data.domain.waiting.WaitingStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.IntegrationTest;
import co.wadcorp.waiting.data.config.WaitingNumberConstructor;
import co.wadcorp.waiting.data.domain.waiting.PersonOptionsData;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingOrderCountDto;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class WaitingCountQueryRepositoryTest extends IntegrationTest {

  @Autowired
  private WaitingCountQueryRepository waitingCountQueryRepository;

  @Autowired
  private WaitingRepository waitingRepository;

  @DisplayName("특정 일자/매장/테이블의 대기 중인 모든 웨이팅을 조회한다.")
  @Test
  void findAllWaitingOrdersShopIdsIn() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 3, 13);

    createWaiting("shopId-1", operationDate, WAITING, WaitingDetailStatus.WAITING, 10, "홀");
    createWaiting("shopId-1", operationDate, WAITING, WaitingDetailStatus.WAITING, 11, "홀");
    createWaiting("shopId-2", operationDate, WAITING, WaitingDetailStatus.WAITING, 10, "홀");
    createWaiting("shopId-1", operationDate.minusDays(1), WAITING, WaitingDetailStatus.WAITING, 10,
        "홀");
    createWaiting("shopId-1", operationDate, SITTING, WaitingDetailStatus.SITTING, 9, "홀");
    createWaiting("shopId-1", operationDate, WAITING, WaitingDetailStatus.WAITING, 10, "바");

    // when
    List<WaitingOrderCountDto> results = waitingCountQueryRepository.findAllWaitingOrdersShopIdsIn(
        List.of("shopId-1"), operationDate);

    // then
    assertThat(results).hasSize(3)
        .extracting("shopId", "waitingOrder", "seatOptionName")
        .containsExactlyInAnyOrder(
            tuple("shopId-1", 10, "홀"),
            tuple("shopId-1", 11, "홀"),
            tuple("shopId-1", 10, "바")
        );
  }

  private WaitingEntity createWaiting(String shopId, LocalDate operationDate,
      WaitingStatus waitingStatus, WaitingDetailStatus waitingDetailStatus, int waitingOrder,
      String seatOptionName
  ) {
    WaitingEntity waiting = WaitingEntity.builder()
        .shopId(shopId)
        .waitingId(UUIDUtil.shortUUID().toUpperCase())
        .operationDate(operationDate)
        .waitingStatus(waitingStatus)
        .waitingDetailStatus(waitingDetailStatus)
        .seatOptionName(seatOptionName)
        .customerSeq(1L)
        .waitingCompleteDateTime(null)
        .waitingNumbers(
            WaitingNumber.builder()
                .waitingNumber(WaitingNumberConstructor.initWaitingNumber())
                .waitingOrder(waitingOrder)
                .build()
        )
        .personOptionsData(PersonOptionsData.builder().build())
        .build();
    return waitingRepository.save(waiting);
  }

}