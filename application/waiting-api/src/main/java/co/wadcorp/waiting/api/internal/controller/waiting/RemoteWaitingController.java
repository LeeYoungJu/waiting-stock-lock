package co.wadcorp.waiting.api.internal.controller.waiting;

import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteWaitingCheckBeforeRegisterRequest;
import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteWaitingListRequest;
import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteWaitingOfOtherShopsRequest;
import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteWaitingRegisterRequest;
import co.wadcorp.waiting.api.internal.service.waiting.RemoteWaitingApiService;
import co.wadcorp.waiting.api.internal.service.waiting.RemoteWaitingCancelApiService;
import co.wadcorp.waiting.api.internal.service.waiting.RemoteWaitingPutOffApiService;
import co.wadcorp.waiting.api.internal.service.waiting.RemoteWaitingRegisterApiService;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteListWaitingResponse;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteOtherWaitingListResponse;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingRegisterResponse;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingResponse;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.api.resolver.channel.ShopId;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RemoteWaitingController {

  private final RemoteWaitingApiService remoteWaitingApiService;
  private final RemoteWaitingRegisterApiService remoteWaitingRegisterApiService;
  private final RemoteWaitingCancelApiService remoteWaitingCancelApiService;

  private final RemoteWaitingPutOffApiService remoteWaitingPutOffApiService;

  /**
   * 원격 웨이팅 등록
   */
  @PostMapping(value = "/internal/api/v1/shops/{shopIds}/waiting")
  public ApiResponse<RemoteWaitingRegisterResponse> registerWaiting(
      @ShopId ChannelShopIdMapping channelShopIdMapping,
      @Valid @RequestBody RemoteWaitingRegisterRequest request
  ) {

    return ApiResponse.ok(remoteWaitingRegisterApiService.register(
        channelShopIdMapping,
        OperationDateUtils.getOperationDateFromNow(),
        ZonedDateTimeUtils.nowOfSeoul(),
        request.toServiceRequest()
    ));
  }

  /**
   * 원격 웨이팅 취소
   */
  @PostMapping(value = "/internal/api/v1/shops/{shopIds}/waiting/{waitingId}/cancel")
  public ApiResponse<RemoteWaitingResponse> cancelWaiting(
      @ShopId ChannelShopIdMapping channelShopIdMapping,
      @PathVariable String waitingId) {

    return ApiResponse.ok(remoteWaitingCancelApiService.cancel(
            channelShopIdMapping,
            waitingId,
            OperationDateUtils.getOperationDateFromNow(),
            ZonedDateTimeUtils.nowOfSeoul()
        )
    );
  }

  /**
   * 원격 웨이팅 미루기
   */
  @PostMapping(value = "/internal/api/v1/shops/{shopIds}/waiting/{waitingId}/put-off")
  public ApiResponse<RemoteWaitingResponse> putOffWaiting(
      @ShopId ChannelShopIdMapping channelShopIdMapping,
      @PathVariable String waitingId) {

    return ApiResponse.ok(
        remoteWaitingPutOffApiService.putOff(
            channelShopIdMapping,
            waitingId,
            OperationDateUtils.getOperationDateFromNow(),
            ZonedDateTimeUtils.nowOfSeoul()
        )
    );
  }

  /**
   * 원격 웨이팅 목록
   */
  @PostMapping(value = "/internal/api/v1/waiting/list")
  public ApiResponse<List<RemoteListWaitingResponse>> findWaitings(
      @Valid @RequestBody RemoteWaitingListRequest request) {
    LocalDate operationDateFromNow = OperationDateUtils.getOperationDateFromNow();
    ZonedDateTime nowDateTime = ZonedDateTimeUtils.nowOfSeoul();

    return ApiResponse.ok(remoteWaitingApiService.findWaitings(
        request.toServiceRequest(operationDateFromNow), nowDateTime
    ));
  }

  /**
   * 원격 웨이팅 등록 전 중복/3회초과 여부 검증
   */
  @GetMapping(value = "/internal/api/v1/shops/{shopIds}/register/waiting/validation")
  public ApiResponse<?> checkWaitingBeforeRegisterByPhone(
      @ShopId ChannelShopIdMapping channelShopIdMapping,
      @Valid RemoteWaitingCheckBeforeRegisterRequest request
  ) {
    remoteWaitingApiService.checkWaitingBeforeRegister(
        channelShopIdMapping,
        OperationDateUtils.getOperationDateFromNow(),
        request.toServiceRequest()
    );
    return ApiResponse.ok();
  }

  /**
   * 원격 웨이팅 타매장 웨이팅 목록 조회
   */
  @GetMapping(value = "/internal/api/v1/shops/{shopIds}/register/waiting/others")
  public ApiResponse<List<RemoteOtherWaitingListResponse>> findAllWaitingsOfOtherShops(
      @ShopId ChannelShopIdMapping channelShopIdMapping,
      @Valid RemoteWaitingOfOtherShopsRequest request
  ) {
    return ApiResponse.ok(
        remoteWaitingApiService.findAllWaitingsOfOtherShops(
            channelShopIdMapping,
            OperationDateUtils.getOperationDateFromNow(),
            request.toServiceRequest()
        )
    );
  }

}
