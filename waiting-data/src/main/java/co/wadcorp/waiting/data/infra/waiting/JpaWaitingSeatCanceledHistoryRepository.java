package co.wadcorp.waiting.data.infra.waiting;

import co.wadcorp.waiting.data.domain.waiting.WaitingSeatCanceledHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingSeatCanceledHistoryRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaWaitingSeatCanceledHistoryRepository extends
    WaitingSeatCanceledHistoryRepository, JpaRepository<WaitingSeatCanceledHistoryEntity, Long> {

  List<WaitingSeatCanceledHistoryEntity> findAllBySeatWaitingSeq(Long seatWaitingSeq);
}
