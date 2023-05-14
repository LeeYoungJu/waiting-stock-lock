package co.wadcorp.waiting.data.service.waiting;

import co.wadcorp.waiting.data.domain.waiting.WaitingSeatCanceledHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingSeatCanceledHistoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class WaitingSeatCanceledHistoryService {

  private final WaitingSeatCanceledHistoryRepository repository;

  public WaitingSeatCanceledHistoryEntity save(WaitingSeatCanceledHistoryEntity entity) {
    return repository.save(entity);
  }

  public List<WaitingSeatCanceledHistoryEntity> findAllBySeatWaitingSeq(Long seatWaitingSeq) {
    return repository.findAllBySeatWaitingSeq(seatWaitingSeq);
  }
}
