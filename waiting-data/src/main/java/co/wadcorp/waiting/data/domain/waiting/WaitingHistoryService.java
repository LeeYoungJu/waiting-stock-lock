package co.wadcorp.waiting.data.domain.waiting;

import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class WaitingHistoryService {

  private final WaitingHistoryRepository repository;

  public WaitingHistoryEntity findById(long waitingHistorySeq) {
    return repository.findById(waitingHistorySeq)
        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_WAITING_HISTORY));
  }

  public List<WaitingHistoryEntity> findAllBySeqIn(List<Long> waitingHistorySeqs) {
    return repository.findAllBySeqIn(waitingHistorySeqs);
  }

  public long countCalledWaitingById(String waitingId) {
    return repository.countByWaitingDetailStatusAndWaitingId(WaitingDetailStatus.CALL, waitingId);
  }

  public WaitingHistoryEntity save(WaitingHistoryEntity waitingHistoryEntity) {
    return repository.save(waitingHistoryEntity);
  }

  public boolean existsByWaitingIdAndWaitingDetailStatus(String waitingId, WaitingDetailStatus waitingDetailStatus) {
    return repository.existsByWaitingIdAndWaitingDetailStatus(waitingId, waitingDetailStatus);
  }
}
