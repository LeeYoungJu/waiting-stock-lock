package co.wadcorp.waiting.api.controller.waiting.management;

import co.wadcorp.waiting.api.model.waiting.request.CancelWaitingByManagementRequest;
import co.wadcorp.waiting.api.service.waiting.WaitingUndoValidateApiService;
import co.wadcorp.waiting.api.service.waiting.management.ManagementWaitingApiService;
import co.wadcorp.waiting.api.service.waiting.management.dto.request.CallWaitingServiceRequest;
import co.wadcorp.waiting.api.service.waiting.management.dto.request.CancelWaitingServiceRequest;
import co.wadcorp.waiting.api.service.waiting.management.dto.request.SittingWaitingServiceRequest;
import co.wadcorp.waiting.api.service.waiting.management.dto.request.UndoWaitingServiceRequest;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ManagementWaitingChangeStatusController {


  private final ManagementWaitingApiService managementWaitingApiService;
  private final WaitingUndoValidateApiService waitingUndoValidateApiService;

  /**
   * 웨이팅 관리 - 호출
   *
   * @param shopId
   * @param waitingId
   * @return
   */
  @PostMapping(value = "/api/v1/shops/{shopId}/management/waiting/{waitingId}/call")
  public ApiResponse<?> calling(@PathVariable String shopId, @PathVariable String waitingId,
      @RequestHeader("X-REQUEST-ID") String deviceId
  ) {
    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
    ZonedDateTime currentDateTime = ZonedDateTimeUtils.nowOfSeoul();

    managementWaitingApiService.call(
        CallWaitingServiceRequest.builder()
            .shopId(shopId)
            .waitingId(waitingId)
            .operationDate(operationDate)
            .currentDateTime(currentDateTime)
            .deviceId(deviceId)
            .build()
    );

    return ApiResponse.ok();
  }

  /**
   * 웨이팅 관리 - 착석
   *
   * @param shopId
   * @param waitingId
   * @return
   */
  @PostMapping(value = "/api/v1/shops/{shopId}/management/waiting/{waitingId}/sitting")
  public ApiResponse<?> sitting(@PathVariable String shopId, @PathVariable String waitingId,
      @RequestHeader("X-REQUEST-ID") String deviceId
  ) {
    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();

    managementWaitingApiService.sitting(
        SittingWaitingServiceRequest.builder()
            .shopId(shopId)
            .waitingId(waitingId)
            .operationDate(operationDate)
            .deviceId(deviceId)
            .build()
    );

    return ApiResponse.ok();
  }


  /**
   * 웨이팅 관리 - 취소
   *
   * @param shopId
   * @param waitingId
   * @return
   */
  @PostMapping(value = "/api/v1/shops/{shopId}/management/waiting/{waitingId}/cancel")
  public ApiResponse<?> cancel(@PathVariable String shopId, @PathVariable String waitingId,
      @RequestHeader("X-REQUEST-ID") String deviceId,
      @RequestBody CancelWaitingByManagementRequest request) {
    managementWaitingApiService.cancel(
        CancelWaitingServiceRequest.builder()
            .shopId(shopId)
            .waitingId(waitingId)
            .cancelReason(request.cancelReason())
            .operationDate(OperationDateUtils.getOperationDateFromNow())
            .deviceId(deviceId)
            .build()
    );

    return ApiResponse.ok();
  }


  /**
   * 웨이팅 관리 - 되돌리기 예외상황 확인
   *
   * @param shopId
   * @param waitingId
   * @return
   */
  @PostMapping(value = "/api/v1/shops/{shopId}/management/waiting/{waitingId}/undo/validation")
  public ApiResponse<?> validateUndo(@PathVariable String shopId, @PathVariable String waitingId) {
    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
    waitingUndoValidateApiService.validateOrder(waitingId, operationDate);

    return ApiResponse.ok();
  }


  /**
   * 웨이팅 관리 - 되돌리기
   *
   * @param shopId
   * @param waitingId
   * @return
   */
  @PostMapping(value = "/api/v1/shops/{shopId}/management/waiting/{waitingId}/undo")
  public ApiResponse<?> undo(@PathVariable String shopId, @PathVariable String waitingId,
      @RequestHeader("X-REQUEST-ID") String deviceId
  ) {
    managementWaitingApiService.undo(
        UndoWaitingServiceRequest.builder()
            .shopId(shopId)
            .waitingId(waitingId)
            .operationDate(OperationDateUtils.getOperationDateFromNow())
            .deviceId(deviceId)
            .build()
    );
    return ApiResponse.ok();
  }
}
