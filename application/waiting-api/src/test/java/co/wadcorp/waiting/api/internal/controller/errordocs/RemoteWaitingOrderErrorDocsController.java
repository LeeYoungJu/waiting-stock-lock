package co.wadcorp.waiting.api.internal.controller.errordocs;

import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteRegisterValidateMenuResponse;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteRegisterValidateMenuResponse.RemoteInvalidMenu;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RemoteWaitingOrderErrorDocsController {

  /**
   * 원격 웨이팅 선주문 에러 - 공통
   */
  @GetMapping(value = "/docs/internal/api/v1/orders/validation/error-common")
  public ResponseEntity<ApiResponse<Map>> commonError() {
    return ErrorDocsResponseFactory.make(ErrorCode.NOT_POSSIBLE_ORDER,
            Map.of("reason", ErrorCode.NOT_POSSIBLE_ORDER.getCode())
    );
  }

  /**
   * 원격 웨이팅 선주문 에러 - 재고 부족
   */
  @PostMapping(value = "/docs/internal/api/v1/orders/validation/outofstock")
  public ResponseEntity<ApiResponse<RemoteRegisterValidateMenuResponse>> outOfStock() {
    return ErrorDocsResponseFactory.make(ErrorCode.OUT_OF_STOCK, createValidateMenuResponse());
  }

  private RemoteRegisterValidateMenuResponse createValidateMenuResponse() {
    return RemoteRegisterValidateMenuResponse.builder()
        .reason(ErrorCode.OUT_OF_STOCK.getCode())
        .menus(List.of(
            RemoteInvalidMenu.builder()
                .id("B7PXDpL-QGuzNAcaiUTMBw")
                .name("콜라")
                .quantity(2)
                .remainingQuantity(1)
                .isOutOfStock(false)
                .build()
        ))
        .build();
  }
}
