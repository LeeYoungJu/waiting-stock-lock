package co.wadcorp.waiting.data.service.waiting;

import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistories;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.domain.waiting.validator.WaitingCancelValidator;
import co.wadcorp.waiting.data.domain.waiting.validator.WaitingCheckSettingsBeforeUndoValidator;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class WaitingChangeStatusService {

  private final WaitingRepository waitingRepository;
  private final WaitingHistoryRepository waitingHistoryRepository;
  private final HomeSettingsRepository homeSettingsRepository;
  private final OptionSettingsRepository optionSettingsRepository;

  public WaitingHistoryEntity cancelByCustomer(String waitingId) {
    WaitingEntity waiting = findByWaitingId(waitingId);

    WaitingCancelValidator.validateStatus(waiting);

    waiting.cancelByCustomer();

    return waitingHistoryRepository.save(transFromWaitingToHistory(waiting));
  }

  public WaitingHistoryEntity putOff(String waitingId, LocalDate operationDate,
      Long maxWaitingOrder) {
    WaitingEntity waiting = findByWaitingId(waitingId);

    WaitingHistories waitingHistories = new WaitingHistories(
        waitingHistoryRepository.findByWaitingSeq(waiting.getSeq()));

    waiting.putOff(maxWaitingOrder.intValue(), operationDate, waitingHistories);

    return waitingHistoryRepository.save(transFromWaitingToHistory(waiting));
  }

  public WaitingHistoryEntity undoByCustomer(String waitingId, LocalDate operationDate) {
    WaitingEntity waiting = findByWaitingId(waitingId);

    checkIfSettingsModified(waiting);

    // 고객 동시 웨이팅 횟수 조회
    List<WaitingEntity> waitingEntities = waitingRepository.findAllByCustomerSeqAndStatusToday(
        waiting.getCustomerSeq(), WaitingStatus.WAITING, operationDate
    );
    waiting.undoByCustomer(waitingEntities);
    return waitingHistoryRepository.save(transFromWaitingToHistory(waiting));
  }

  private WaitingEntity findByWaitingId(String waitingId) {
    return waitingRepository.findByWaitingId(waitingId)
        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_WAITING));
  }

  private void checkIfSettingsModified(WaitingEntity waiting) {
    HomeSettingsEntity homeSettingsEntity = homeSettingsRepository.findFirstByShopIdAndIsPublished(
            waiting.getShopId(), true)
        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "매장 설정 정보가 존재하지 않습니다."));
    OptionSettingsEntity optionSettingsEntity = optionSettingsRepository.findFirstByShopIdAndIsPublished(
            waiting.getShopId(), true)
        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "인원옵션 설정 정보가 존재하지 않습니다."));

    WaitingCheckSettingsBeforeUndoValidator.validate(waiting, homeSettingsEntity,
        optionSettingsEntity);
  }

  private WaitingHistoryEntity transFromWaitingToHistory(WaitingEntity waiting) {
    return new WaitingHistoryEntity(waiting);
  }

}
