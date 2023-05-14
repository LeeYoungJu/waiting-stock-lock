package co.wadcorp.waiting.data.infra.waiting;

import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaWaitingHistoryRepository extends WaitingHistoryRepository, JpaRepository<WaitingHistoryEntity, Long> {

  List<WaitingHistoryEntity> findByWaitingSeq(Long waitingSeq);

  List<WaitingHistoryEntity> findAllBySeqIn(List<Long> waitingHistorySeqs);

  long countByWaitingDetailStatusAndWaitingId(WaitingDetailStatus detailStatus, String waitingId);

  boolean existsByWaitingIdAndWaitingDetailStatus(String waitingId, WaitingDetailStatus waitingDetailStatus);

}
