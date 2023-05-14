package co.wadcorp.waiting.data.domain.memo;

import java.util.List;

public interface WaitingMemoHistoryRepository {

  WaitingMemoHistoryEntity save(WaitingMemoHistoryEntity entity);

  List<WaitingMemoHistoryEntity> findAllByWaitingId(String waitingId);
}
