package co.wadcorp.waiting.api.controller.shop;

import co.wadcorp.waiting.api.controller.shop.dto.UpdateShopRequest;
import co.wadcorp.waiting.api.model.shop.response.ShopResponse;
import co.wadcorp.waiting.api.model.shop.response.ShopsResponse;
import co.wadcorp.waiting.api.resolver.channel.ShopId;
import co.wadcorp.waiting.api.service.shop.ShopApiService;
import co.wadcorp.waiting.data.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ShopController {

  private final ShopApiService shopApiService;

  /**
   * 매장 목록 조회 API
   *
   * @return
   */
  @GetMapping("/api/v1/shops")
  public ApiResponse<ShopsResponse> shops(@RequestHeader("X-CTM-AUTH") String ctmAuth) {
    return ApiResponse.ok(shopApiService.getShops(ctmAuth));
  }

  /**
   * 단일 매장 조회 API
   *
   * @param shopId
   * @return
   */
  @GetMapping("/api/v1/shops/{shopId}")
  public ApiResponse<ShopResponse> shop(@PathVariable String shopId,
      @RequestHeader("X-CTM-AUTH") String ctmAuth) {
    return ApiResponse.ok(shopApiService.getShop(shopId));
  }

  /**
   * 매장 정보 업데이트 API (phone)
   *
   * @param shopId
   * @return
   */
  @PostMapping("/api/v1/shops/{shopId}/update")
  public ApiResponse<ShopResponse> update(@PathVariable String shopId, @RequestBody UpdateShopRequest request) {
    return ApiResponse.ok(shopApiService.updateShop(shopId, request));
  }

}
