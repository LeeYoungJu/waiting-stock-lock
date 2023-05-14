package co.wadcorp.waiting.api.service.waiting;

import co.wadcorp.waiting.api.model.waiting.response.WaitingHistoriesResponse;
import co.wadcorp.waiting.data.query.waiting.WaitingHistoryQueryRepository;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingHistoriesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class WaitingHistoryApiService {

  private final WaitingHistoryQueryRepository waitingHistoryQueryRepository;

  @Transactional(readOnly = true)
  public WaitingHistoriesResponse getWaitingHistories(String shopId, String waitingId) {
    WaitingHistoriesDto waitingHistoriesDto = waitingHistoryQueryRepository.getWaitingHistories(shopId,
        waitingId);

    return WaitingHistoriesResponse.toDto(waitingHistoriesDto.getWaiting(),
        waitingHistoriesDto.getWaitingHistories());
  }
}
