package co.wadcorp.waiting.data.service.waiting;

import co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelTargetEntity;
import co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelTargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class AutoCancelTargetService {

  private final AutoCancelTargetRepository autoCancelTargetRepository;

  public AutoCancelTargetEntity save(AutoCancelTargetEntity autoCancelTarget) {
    return autoCancelTargetRepository.save(autoCancelTarget);
  }

  public AutoCancelTargetEntity findByWaitingId(String waitingId) {
    return autoCancelTargetRepository.findByWaitingId(waitingId)
        .orElse(AutoCancelTargetEntity.EMPTY);
  }
}
