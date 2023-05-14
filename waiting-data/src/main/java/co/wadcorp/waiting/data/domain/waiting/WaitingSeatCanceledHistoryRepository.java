package co.wadcorp.waiting.data.domain.waiting;

import java.util.List;

public interface WaitingSeatCanceledHistoryRepository {

  WaitingSeatCanceledHistoryEntity save(
      WaitingSeatCanceledHistoryEntity waitingSeatCanceledHistoryEntity);

  List<WaitingSeatCanceledHistoryEntity> findAllBySeatWaitingSeq(Long seatWaitingSeq);
}
