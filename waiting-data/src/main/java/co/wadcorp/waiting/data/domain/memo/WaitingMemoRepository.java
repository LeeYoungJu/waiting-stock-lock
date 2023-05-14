package co.wadcorp.waiting.data.domain.memo;

import java.util.List;
import java.util.Optional;

public interface WaitingMemoRepository {

  WaitingMemoEntity save(WaitingMemoEntity entity);

  Optional<WaitingMemoEntity> findByWaitingId(String waitingId);

}
