package co.wadcorp.waiting.data.service.memo;

import co.wadcorp.waiting.data.domain.memo.WaitingMemoEntity;
import co.wadcorp.waiting.data.domain.memo.WaitingMemoHistoryEntity;
import co.wadcorp.waiting.data.domain.memo.WaitingMemoHistoryRepository;
import co.wadcorp.waiting.data.domain.memo.WaitingMemoRepository;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class WaitingMemoService {

  private final WaitingMemoRepository waitingMemoRepository;

  private final WaitingMemoHistoryRepository waitingMemoHistoryRepository;

  public WaitingMemoEntity save(WaitingMemoEntity entity) {
    WaitingMemoEntity savedMemo = merge(entity);
    saveHistory(savedMemo);
    return savedMemo;
  }

  public void delete(String waitingId) {
    WaitingMemoEntity waitingMemo = waitingMemoRepository.findByWaitingId(waitingId)
        .orElseThrow(() -> AppException.ofBadRequest(ErrorCode.NOT_FOUND_WAITING_MEMO));
    waitingMemo.updateMemo("");
    saveHistory(waitingMemo);
  }

  private WaitingMemoEntity merge(WaitingMemoEntity entity) {
    Optional<WaitingMemoEntity> memoOptional = waitingMemoRepository.findByWaitingId(
        entity.getWaitingId());

    if(memoOptional.isEmpty()) {
      return waitingMemoRepository.save(entity);
    }

    WaitingMemoEntity waitingMemo = memoOptional.get();
    waitingMemo.updateMemo(entity.getMemo());
    return waitingMemo;
  }

  private void saveHistory(WaitingMemoEntity entity) {
    waitingMemoHistoryRepository.save(
        WaitingMemoHistoryEntity.of(entity)
    );
  }

}
