package co.wadcorp.waiting.api.controller.waiting.web;

import co.wadcorp.waiting.api.model.waiting.response.WaitingCanUndoListResponse;
import co.wadcorp.waiting.api.service.waiting.WaitingUndoValidateApiService;
import co.wadcorp.waiting.api.service.waiting.web.WaitingCanUndoListWebService;
import co.wadcorp.waiting.api.service.waiting.web.WaitingChangeStatusWebService;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WaitingUndoWebController {

  private final WaitingChangeStatusWebService waitingChangeStatusWebService;
  private final WaitingCanUndoListWebService waitingCanUndoListWebService;
  private final WaitingUndoValidateApiService waitingUndoValidateApiService;


  /**
   * 캐치테이블 웨이팅 웹 복귀 목록 조회
   *
   * @param waitingId
   * @return 웨이팅 복귀 목록 조회
  * */
  @GetMapping(value = "/web/v1/waiting/{waitingId}/undo-waiting")
  public ApiResponse<WaitingCanUndoListResponse> getCanUndoWaiting(@PathVariable String waitingId) {
    return ApiResponse.ok(
        waitingCanUndoListWebService.getCanUndoList(waitingId, ZonedDateTimeUtils.ofSeoul(LocalDate.now(), LocalTime.now()))
    );
  }


  /**
   * 캐치테이블 웨이팅 웹 복귀
   *
   * @param waitingId
   * @return
   */
  @PostMapping(value = "/web/v1/waiting/{waitingId}/undo")
  public ApiResponse<?> undoWaiting(@PathVariable String waitingId) {
    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();

    waitingUndoValidateApiService.validateOrder(waitingId, operationDate);
    waitingChangeStatusWebService.undo(waitingId, operationDate);
    return ApiResponse.ok();
  }


}
