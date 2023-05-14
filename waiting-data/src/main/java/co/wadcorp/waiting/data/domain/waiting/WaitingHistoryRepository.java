package co.wadcorp.waiting.data.domain.waiting;

import java.util.List;
import java.util.Optional;

public interface WaitingHistoryRepository {

  List<WaitingHistoryEntity> findByWaitingSeq(Long waitingSeq);

  Optional<WaitingHistoryEntity> findById(Long waitingSeq);

  WaitingHistoryEntity save(WaitingHistoryEntity waitingHistoryEntity);

  <S extends WaitingHistoryEntity> List<S> saveAll(Iterable<S> entities);

  List<WaitingHistoryEntity> findAll();

  List<WaitingHistoryEntity> findAllBySeqIn(List<Long> waitingHistorySeqs);

  long countByWaitingDetailStatusAndWaitingId(WaitingDetailStatus detailStatus, String waitingId);

  void deleteAllInBatch();

  boolean existsByWaitingIdAndWaitingDetailStatus(String waitingId, WaitingDetailStatus waitingDetailStatus);
}
