package co.wadcorp.waiting.data.domain.waiting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeWaitingSeatCanceledHistoryRepository implements
    WaitingSeatCanceledHistoryRepository {

  private final Map<Long, List<WaitingSeatCanceledHistoryEntity>> memoryMap = new HashMap<>();

  @Override
  public WaitingSeatCanceledHistoryEntity save(
      WaitingSeatCanceledHistoryEntity waitingSeatCanceledHistoryEntity) {
    List<WaitingSeatCanceledHistoryEntity> waitingSeatCanceledHistoryEntities = memoryMap.getOrDefault(
        waitingSeatCanceledHistoryEntity.getSeatWaitingSeq(), new ArrayList<>());

    waitingSeatCanceledHistoryEntities.add(waitingSeatCanceledHistoryEntity);
    memoryMap.put(waitingSeatCanceledHistoryEntity.getSeatWaitingSeq(),
        waitingSeatCanceledHistoryEntities);

    return waitingSeatCanceledHistoryEntity;
  }

  @Override
  public List<WaitingSeatCanceledHistoryEntity> findAllBySeatWaitingSeq(Long seatWaitingSeq) {
    return memoryMap.get(seatWaitingSeq);
  }
}
