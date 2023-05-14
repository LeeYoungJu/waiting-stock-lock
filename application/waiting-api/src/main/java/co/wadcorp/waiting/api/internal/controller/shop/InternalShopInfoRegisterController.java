package co.wadcorp.waiting.api.internal.controller.shop;

import co.wadcorp.waiting.api.internal.controller.shop.dto.request.InternalDisablePutOffSettingsRequest;
import co.wadcorp.waiting.api.internal.controller.shop.dto.request.InternalRemoteShopOperationTimeSettingsRequest;
import co.wadcorp.waiting.api.internal.service.shop.InternalShopInfoRegisterApiService;
import co.wadcorp.waiting.data.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class InternalShopInfoRegisterController {

  private final InternalShopInfoRegisterApiService service;

  /**
   * [내부용] 원격 웨이팅 매장 운영시간 정보 세팅 API
   * <p>
   * 나중에 Admin 기능으로 변경되면 prefix 변경 고려 (ex. internal -> admin)
   *
   * @param shopId  웨이팅 매장 ID (short UUID)
   * @param request 매장 요일별 운영시간 정보
   */
  @PostMapping(value = "/internal/api/v1/shops/{shopId}/settings/remote/time-operations")
  public ApiResponse<Object> setRemoteOperationTimeSettings(@PathVariable String shopId,
      @Valid @RequestBody InternalRemoteShopOperationTimeSettingsRequest request) {
    service.setRemoteOperationTimeSettings(shopId, request.toServiceRequest());
    return ApiResponse.ok();
  }

  /**
   * [내부용] 매장 미루기 off 정보 세팅 API
   * <p>
   * 나중에 Admin 기능으로 변경되면 prefix 변경 고려 (ex. internal -> admin)
   *
   * @param shopId  웨이팅 매장 ID (short UUID)
   * @param request 미루기 off 사용 여부
   */
  @PostMapping(value = "/internal/api/v1/shops/{shopId}/settings/disable-put-off")
  public ApiResponse<Object> setDisablePutOff(@PathVariable String shopId,
      @Valid @RequestBody InternalDisablePutOffSettingsRequest request) {
    service.setDisablePutOff(shopId, request.toServiceRequest());
    return ApiResponse.ok();
  }

}
