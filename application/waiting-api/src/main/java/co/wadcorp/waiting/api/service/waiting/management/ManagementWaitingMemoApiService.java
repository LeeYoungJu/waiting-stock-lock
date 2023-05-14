package co.wadcorp.waiting.api.service.waiting.management;

import co.wadcorp.waiting.api.service.waiting.management.dto.request.WaitingMemoSaveServiceRequest;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.WaitingMemoSaveResponse;
import co.wadcorp.waiting.data.service.memo.WaitingMemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ManagementWaitingMemoApiService {

  private final WaitingMemoService waitingMemoService;

  @Transactional
  public WaitingMemoSaveResponse save(String shopId, WaitingMemoSaveServiceRequest request) {
    return WaitingMemoSaveResponse.of(
        waitingMemoService.save(request.toEntity(shopId))
    );
  }

  @Transactional
  public void delete(String waitingId) {
    waitingMemoService.delete(waitingId);
  }

}
