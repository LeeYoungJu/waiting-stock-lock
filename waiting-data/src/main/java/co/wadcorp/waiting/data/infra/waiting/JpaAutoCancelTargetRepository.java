package co.wadcorp.waiting.data.infra.waiting;

import co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelTargetEntity;
import co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelTargetRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAutoCancelTargetRepository extends AutoCancelTargetRepository,
    JpaRepository<AutoCancelTargetEntity, Long> {

  Optional<AutoCancelTargetEntity> findByWaitingId(String waitingId);

}
