package co.wadcorp.waiting.api.service.waiting;

import co.wadcorp.waiting.api.model.waiting.response.WaitingResponse;
import co.wadcorp.waiting.api.model.waiting.vo.WaitingVO;
import co.wadcorp.waiting.data.domain.message.SendType;
import co.wadcorp.waiting.data.query.message.MessageSendHistoryQueryRepository;
import co.wadcorp.waiting.data.query.order.WaitingOrderQueryRepository;
import co.wadcorp.waiting.data.query.order.dto.WaitingOrderDto;
import co.wadcorp.waiting.data.query.waiting.WaitingHistoryQueryRepository;
import co.wadcorp.waiting.data.query.waiting.WaitingQueryRepository;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingCalledCountDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingCalledHistoryDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingDto;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class WaitingApiService {

  private final WaitingQueryRepository waitingQueryRepository;
  private final WaitingOrderQueryRepository waitingOrderQueryRepository;
  private final WaitingHistoryQueryRepository waitingHistoryQueryRepository;
  private final MessageSendHistoryQueryRepository messageSendHistoryQueryRepository;

  public WaitingResponse getWaitingBy(String waitingId) {

    WaitingDto waiting = waitingQueryRepository.getWaiting(waitingId);

    // 호출 횟수 구하기
    WaitingCalledCountDto waitingCalledCountDto = getWaitingCalledCountDto(waiting.getWaitingId());
    // 입장 임박 알림톡 발송 여부 구하기
    boolean isSentReadyToEnterAlarm = isSentReadyToEnterAlarm(waiting.getWaitingId());
    // 선주문 구하기
    WaitingOrderDto waitingOrderDto = getWaitingOrderDto(waiting.getWaitingId());

    return WaitingResponse.builder()
        .waiting(WaitingVO.toDto(
            waiting, waitingCalledCountDto, isSentReadyToEnterAlarm, waitingOrderDto
        ))
        .build();
  }

  private WaitingCalledCountDto getWaitingCalledCountDto(String waitingId) {
    // 호출 횟수 조회 Query
    List<WaitingCalledHistoryDto> calledHistory = waitingHistoryQueryRepository.getWaitingCalledHistory(
        waitingId);

    if (calledHistory.isEmpty()) {
      return WaitingCalledCountDto.EMPTY;
    }

    return new WaitingCalledCountDto(
        calledHistory.size(),
        calledHistory.stream()
            .map(WaitingCalledHistoryDto::getRegDateTime)
            .max(ZonedDateTime::compareTo)
            .get()
    );
  }


  private boolean isSentReadyToEnterAlarm(String waitingId) {
    return messageSendHistoryQueryRepository.findByWaitingIdInAndSendType(
        waitingId,
        SendType.WAITING_READY_TO_ENTER
    ).isPresent();
  }


  private WaitingOrderDto getWaitingOrderDto(String waitingId) {
    return waitingOrderQueryRepository.findByWaitingId(waitingId)
        .orElse(WaitingOrderDto.EMPTY_ORDER);

  }
}
