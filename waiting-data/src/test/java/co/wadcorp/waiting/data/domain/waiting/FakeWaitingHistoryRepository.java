package co.wadcorp.waiting.data.domain.waiting;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeWaitingHistoryRepository implements WaitingHistoryRepository {

  private final Map<Long, WaitingHistoryEntity> memoryMap = new HashMap<>();
  private final AtomicLong waitingHistorySeq = new AtomicLong(0L);

  @Override
  public List<WaitingHistoryEntity> findByWaitingSeq(Long waitingSeq) {
    return memoryMap.values()
        .stream()
        .filter(item -> waitingSeq.equals(item.getWaitingSeq()))
        .toList();
  }

  @Override
  public Optional<WaitingHistoryEntity> findById(Long waitingHistorySeq) {
    return Optional.ofNullable(memoryMap.get(waitingHistorySeq));
  }

  @Override
  public WaitingHistoryEntity save(WaitingHistoryEntity waitingHistoryEntity) {
    setId(waitingHistoryEntity, waitingHistorySeq.addAndGet(1));

    memoryMap.put(waitingHistoryEntity.getSeq(), waitingHistoryEntity);
    return waitingHistoryEntity;
  }

  @Override
  public <S extends WaitingHistoryEntity> List<S> saveAll(Iterable<S> entities) {
    List<S> results = new ArrayList<>();
    for (S entity : entities) {
      save(entity);
      results.add(entity);
    }
    return results;
  }

  @Override
  public List<WaitingHistoryEntity> findAll() {
    return memoryMap.values().stream()
        .toList();
  }

  @Override
  public List<WaitingHistoryEntity> findAllBySeqIn(List<Long> waitingHistorySeqs) {
    return memoryMap.values()
        .stream()
        .filter(item -> waitingHistorySeqs.contains(item.getWaitingSeq()))
        .toList();
  }

  @Override
  public long countByWaitingDetailStatusAndWaitingId(WaitingDetailStatus detailStatus,
      String waitingId) {
    return memoryMap.values().stream()
        .filter(entity -> entity.getWaitingDetailStatus() == detailStatus)
        .filter(entity -> entity.getWaitingId().equals(waitingId))
        .count();
  }

  @Override
  public void deleteAllInBatch() {
    memoryMap.clear();
  }

  @Override
  public boolean existsByWaitingIdAndWaitingDetailStatus(String waitingId,
      WaitingDetailStatus waitingDetailStatus) {
    return memoryMap.values().stream()
        .filter(item -> item.getWaitingId().equals(waitingId))
        .anyMatch(item -> item.getWaitingDetailStatus() == waitingDetailStatus);
  }

  private void setId(WaitingHistoryEntity waitingHistoryEntity, Long value) {
    Field seq;
    try {
      seq = WaitingHistoryEntity.class.getDeclaredField("seq");

      seq.setAccessible(true);
      seq.set(waitingHistoryEntity, value);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
