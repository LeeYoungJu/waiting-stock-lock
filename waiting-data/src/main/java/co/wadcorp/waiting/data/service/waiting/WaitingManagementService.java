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
import co.wadcorp.waiting.data.domain.waiting.validator.WaitingCheckSettingsBeforeUndoValidator;
import co.wadcorp.waiting.data.domain.waiting.validator.WaitingShopIdValidator;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Transactional
@Service
@RequiredArgsConstructor
public class WaitingManagementService {

  private final WaitingRepository waitingRepository;
  private final WaitingHistoryRepository waitingHistoryRepository;
  private final HomeSettingsRepository homeSettingsRepository;
  private final OptionSettingsRepository optionSettingsRepository;

  public WaitingHistoryEntity call(String shopId, String waitingId, LocalDate operationDate) {
    WaitingEntity waiting = findByWaitingId(waitingId);
    WaitingShopIdValidator.validateWaitingSameShopId(shopId, waiting);

    WaitingHistories histories = new WaitingHistories(
        waitingHistoryRepository.findByWaitingSeq(waiting.getSeq()));

    waiting.calling(operationDate, histories);
    return waitingHistoryRepository.save(transFromWaitingToHistory(waiting));
  }

  public WaitingHistoryEntity sitting(String shopId, String waitingId,
      LocalDate requestOperationDate) {
    WaitingEntity waiting = findByWaitingId(waitingId);
    WaitingShopIdValidator.validateWaitingSameShopId(shopId, waiting);

    waiting.sitting(requestOperationDate);
    return waitingHistoryRepository.save(transFromWaitingToHistory(waiting));
  }


  public WaitingHistoryEntity cancelByCustomer(String shopId, String waitingId) {
    WaitingEntity waiting = findByWaitingId(waitingId);
    WaitingShopIdValidator.validateWaitingSameShopId(shopId, waiting);

    waiting.cancelByCustomer();
    return waitingHistoryRepository.save(transFromWaitingToHistory(waiting));
  }

  public WaitingHistoryEntity cancelByShop(String shopId, String waitingId) {
    WaitingEntity waiting = findByWaitingId(waitingId);
    WaitingShopIdValidator.validateWaitingSameShopId(shopId, waiting);

    waiting.cancelByShop();
    return waitingHistoryRepository.save(transFromWaitingToHistory(waiting));
  }

  public WaitingHistoryEntity cancelByOutOfStock(String shopId, String waitingId) {
    WaitingEntity waiting = findByWaitingId(waitingId);
    WaitingShopIdValidator.validateWaitingSameShopId(shopId, waiting);

    waiting.cancelByOutOfStock();
    return waitingHistoryRepository.save(transFromWaitingToHistory(waiting));
  }

  public WaitingHistoryEntity cancelBySitting(String waitingId) {
    WaitingEntity waiting = findByWaitingId(waitingId);

    waiting.cancelBySitting();
    return waitingHistoryRepository.save(transFromWaitingToHistory(waiting));
  }

  public WaitingHistoryEntity noShow(String shopId, String waitingId) {
    WaitingEntity waiting = findByWaitingId(waitingId);
    WaitingShopIdValidator.validateWaitingSameShopId(shopId, waiting);

    waiting.noShow();
    return waitingHistoryRepository.save(transFromWaitingToHistory(waiting));
  }

  public WaitingHistoryEntity undo(String shopId, String waitingId, LocalDate operationDate) {
    WaitingEntity waiting = findByWaitingId(waitingId);
    WaitingShopIdValidator.validateWaitingSameShopId(shopId, waiting);

    checkIfSettingsModified(waiting);

    List<WaitingEntity> waitingEntities = waitingRepository.findAllByCustomerSeqAndStatusToday(
        waiting.getCustomerSeq(), WaitingStatus.WAITING, operationDate
    );
    waiting.undo(waitingEntities);
    return waitingHistoryRepository.save(transFromWaitingToHistory(waiting));
  }

  public WaitingHistoryEntity delayed(String shopId, String waitingId, LocalDate operationDate) {
    WaitingEntity waiting = findByWaitingId(waitingId);
    WaitingShopIdValidator.validateWaitingSameShopId(shopId, waiting);

    waiting.delayed(operationDate);
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

  public WaitingHistoryEntity readyToEnter(String shopId, String waitingId,
      LocalDate operationDate) {
    WaitingEntity waiting = findByWaitingId(waitingId);
    WaitingShopIdValidator.validateWaitingSameShopId(shopId, waiting);

    WaitingHistories histories = new WaitingHistories(
        waitingHistoryRepository.findByWaitingSeq(waiting.getSeq()));

    waiting.readyToEnter(operationDate, histories);
    return waitingHistoryRepository.save(transFromWaitingToHistory(waiting));

  }
}
