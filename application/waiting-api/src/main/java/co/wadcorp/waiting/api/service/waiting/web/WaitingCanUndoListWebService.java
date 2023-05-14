package co.wadcorp.waiting.api.service.waiting.web;

import static co.wadcorp.libs.stream.StreamUtils.convert;
import static co.wadcorp.libs.stream.StreamUtils.convertToMap;

import co.wadcorp.waiting.api.model.waiting.response.WaitingCanUndoListResponse;
import co.wadcorp.waiting.api.model.waiting.vo.CanUndoWaitingVO;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingSeatCanceledHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.query.shop.ShopQueryRepository;
import co.wadcorp.waiting.data.query.shop.dto.ShopDto;
import co.wadcorp.waiting.data.query.waiting.WaitingQueryRepository;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingDto;
import co.wadcorp.waiting.data.service.waiting.WaitingSeatCanceledHistoryService;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class WaitingCanUndoListWebService {

  private static final int CAN_UNDO_COUNTDOWN_MINUTE = 30;

  private final WaitingQueryRepository waitingQueryRepository;
  private final ShopQueryRepository shopQueryRepository;

  private final WaitingSeatCanceledHistoryService waitingSeatCanceledHistoryService;

  public WaitingCanUndoListResponse getCanUndoList(String waitingId, ZonedDateTime nowDateTime) {

    WaitingDto waitingDto = waitingQueryRepository.getWaiting(waitingId);

    List<WaitingSeatCanceledHistoryEntity> waitingSeq = waitingSeatCanceledHistoryService.findAllBySeatWaitingSeq(
        waitingDto.getSeq());

    if (waitingSeq.isEmpty() || waitingDto.getWaitingStatus() != WaitingStatus.SITTING) {
      return WaitingCanUndoListResponse.EMPTY_RESULT;
    }

    ZonedDateTime waitingCompleteDateTime = waitingDto.getWaitingCompleteDateTime();
    ZonedDateTime canUndoDateTime = waitingCompleteDateTime.plusMinutes(CAN_UNDO_COUNTDOWN_MINUTE);
    Duration duration = Duration.between(waitingCompleteDateTime, nowDateTime);

    List<WaitingDto> canceledWaitingList = waitingQueryRepository.getWaitingByWaitingSeq(
        convert(waitingSeq, WaitingSeatCanceledHistoryEntity::getCanceledWaitingSeq)
    );

    // grouping
    Map<Long, WaitingSeatCanceledHistoryEntity> groupSeatCanceledHistories = convertToMap(
        waitingSeq, WaitingSeatCanceledHistoryEntity::getCanceledWaitingSeq);

    List<CanUndoWaitingVO> canUndoWaitingVOS = canceledWaitingList.stream()
        .filter(item -> item.getWaitingDetailStatus() == WaitingDetailStatus.CANCEL_BY_SITTING)
        .map(item -> {
          WaitingSeatCanceledHistoryEntity canceledHistory = groupSeatCanceledHistories.get(
              item.getSeq());

          ShopDto shopDto = shopQueryRepository.getByShopId(item.getShopId());

          return CanUndoWaitingVO.toDto(item, shopDto.getShopName(),
              canceledHistory.getCanceledWaitingTeamCount(),
              canceledHistory.getCanceledWaitingExpectedWaitingPeriod(), duration.toMinutes());
        })
        .toList();

    return new WaitingCanUndoListResponse(canUndoDateTime, canUndoWaitingVOS);
  }

}
