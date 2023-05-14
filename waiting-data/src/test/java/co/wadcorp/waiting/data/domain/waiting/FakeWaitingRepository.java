package co.wadcorp.waiting.data.domain.waiting;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeWaitingRepository implements WaitingRepository {

  private final Map<Long, WaitingEntity> memoryMap = new HashMap<>();
  private final AtomicLong waitingSeq = new AtomicLong(0L);

  @Override
  public WaitingEntity save(WaitingEntity waitingEntity) {
    long value = waitingSeq.addAndGet(1);

    setId(waitingEntity, value);

    memoryMap.put(value, waitingEntity);
    return waitingEntity;
  }

  @Override
  public <S extends WaitingEntity> List<S> saveAll(Iterable<S> entities) {
    List<S> results = new ArrayList<>();
    for (S entity : entities) {
      save(entity);
      results.add(entity);
    }
    return results;
  }

  @Override
  public List<WaitingEntity> findAll() {
    return memoryMap.values().stream()
        .toList();
  }

  @Override
  public Optional<WaitingEntity> findByWaitingId(String waitingId) {
    Collection<WaitingEntity> values = memoryMap.values();

    return values.stream()
        .filter(item -> waitingId.equals(item.getWaitingId()))
        .findFirst();
  }

  @Override
  public List<WaitingEntity> findAllByWaitingIdInAndOperationDate(List<String> waitingIds,
      LocalDate operationDate) {
    return memoryMap.values().stream()
        .filter(item -> waitingIds.contains(item.getWaitingId()))
        .filter(item -> item.getOperationDate().isEqual(operationDate))
        .toList();
  }

  @Override
  public List<WaitingEntity> findAllByCustomerSeqAndStatusToday(Long customerSeq,
      WaitingStatus status, LocalDate operationDateFromNow) {

    Collection<WaitingEntity> values = memoryMap.values();

    return values.stream()
          .filter(item -> customerSeq.equals(item.getCustomerSeq()) && status == item.getWaitingStatus())
          .toList();
  }

  @Override
  public int countAllWaitingTeamBySeatOption(String shopId, WaitingStatus waitingStatus,
      String seatOptionName, LocalDate operationDateFromNow) {

    Collection<WaitingEntity> values = memoryMap.values();

    return (int) values.stream()
        .filter(item -> shopId.equals(item.getShopId()) && waitingStatus == item.getWaitingStatus() && seatOptionName.equals(item.getSeatOptionName()))
        .count();
  }

  @Override
  public boolean existsByShopIdAndWaitingStatusAndOperationDate(String shopId, WaitingStatus waitingStatus,
      LocalDate operationDateFromNow) {

    Collection<WaitingEntity> values = memoryMap.values();

    return values
        .stream()
        .anyMatch(item -> shopId.equals(item.getShopId()) && waitingStatus == item.getWaitingStatus() && operationDateFromNow.isEqual(item.getOperationDate()));
  }

  @Override
  public List<WaitingEntity> findAllByShopIdAndOperationDate(String shopId, LocalDate operationDate) {

    Collection<WaitingEntity> values = memoryMap.values();

    return values
        .stream()
        .filter(w -> shopId.equals(w.getShopId()) && operationDate.isEqual(w.getOperationDate()))
        .toList();
  }

  @Override
  public List<WaitingEntity> findAllByShopIdAndOperationDateAndWaitingStatus(String shopId,
      LocalDate operationDate, WaitingStatus waitingStatus) {
    Collection<WaitingEntity> values = memoryMap.values();

    return values
        .stream()
        .filter(w -> shopId.equals(w.getShopId()))
        .filter(w -> operationDate.isEqual(w.getOperationDate()))
        .filter(WaitingEntity::isWaitingStatus)
        .toList();
  }

  @Override
  public Optional<Integer> findMaxWaitingOrderByShopId(String shopId, LocalDate operationDate,
      WaitingStatus waitingStatus) {

    Collection<WaitingEntity> values = memoryMap.values();

    return Optional.of(
        values.stream()
            .filter(
                w -> shopId.equals(w.getShopId()) && operationDate.isEqual(w.getOperationDate()))
            .filter(w -> w.getWaitingStatus() == waitingStatus)
            .mapToInt(WaitingEntity::getWaitingOrder)
            .max().orElse(0)
    );
  }

  @Override
  public List<WaitingEntity> findAllByOperationDateAndWaitingStatusAndExpectedSittingDateTimeIsBetween(
      LocalDate operationDate, WaitingStatus waiting, ZonedDateTime before, ZonedDateTime after) {
    return null;
  }

  @Override
  public List<String> findAllByShopIdAndOperationDateAndSeatOptionNameAndWaitingOrderGreaterThanEqual(
      String shopId, LocalDate operationDate, String seatOptionName, int waitingOrder) {

    return memoryMap.values()
        .stream()
        .filter(item -> item.getShopId().equals(shopId))
        .filter(item -> item.getOperationDate().isEqual(operationDate))
        .filter(item -> item.getSeatOptionName().equals(seatOptionName))
        .filter(item -> item.getWaitingOrder() >= waitingOrder)
        .map(WaitingEntity::getWaitingId)
        .toList();
  }

  @Override
  public void deleteAllInBatch() {
    memoryMap.clear();
  }

  private void setId(WaitingEntity waitingEntity, Long value) {
    Field seq;
    try {
      seq = WaitingEntity.class.getDeclaredField("seq");

      seq.setAccessible(true);
      seq.set(waitingEntity, value);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
