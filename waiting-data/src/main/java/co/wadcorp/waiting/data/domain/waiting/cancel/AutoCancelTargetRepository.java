package co.wadcorp.waiting.data.domain.waiting.cancel;

import java.util.List;
import java.util.Optional;

public interface AutoCancelTargetRepository {

  AutoCancelTargetEntity save(AutoCancelTargetEntity autoCancelTarget);

  <S extends AutoCancelTargetEntity> List<S> saveAll(Iterable<S> entities);

  List<AutoCancelTargetEntity> findAll();

  Optional<AutoCancelTargetEntity> findByWaitingId(String waitingId);

  void deleteAllInBatch();

}
