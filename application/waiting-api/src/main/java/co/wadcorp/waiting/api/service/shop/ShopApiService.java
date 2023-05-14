package co.wadcorp.waiting.api.service.shop;

import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.waiting.api.controller.shop.dto.UpdateShopRequest;
import co.wadcorp.waiting.api.model.shop.response.ShopResponse;
import co.wadcorp.waiting.api.model.shop.response.ShopsResponse;
import co.wadcorp.waiting.data.domain.shop.ShopEntity;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.service.shop.ShopService;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.infra.pos.CatchtablePosShopClient;
import co.wadcorp.waiting.infra.pos.dto.PosApiResponse;
import co.wadcorp.waiting.infra.pos.dto.PosShopsResponse;
import co.wadcorp.waiting.shared.util.PhoneNumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class ShopApiService {

  private final CatchtablePosShopClient catchtablePosShopClient;
  private final ShopService shopService;

  public ShopsResponse getShops(String ctmAuth) {
    PosApiResponse<PosShopsResponse> response = catchtablePosShopClient.getShops(ctmAuth);

    if(response.isUnauthorized()) {
      throw new AppException(HttpStatus.UNAUTHORIZED);
    }

    if(response.isError()) {
      throw new AppException(HttpStatus.CONFLICT, response.getMessage(),
          response.getDisplayMessage(), response.getReason());
    }

    PosShopsResponse data = response.getData();
    return ShopsResponse.toDto(data.getShopList());
  }

  public ShopResponse getShop(String shopId) {
    ShopEntity shopEntity = shopService.findByShopId(shopId);

    return ShopResponse.toDto(shopEntity);
  }

  public ShopResponse updateShop(String shopId, UpdateShopRequest request) {
    ShopEntity shopEntity = shopService.findByShopId(shopId);

    PhoneNumber phoneNumber = PhoneNumberUtils.ofKr(request.shopTelNumber());
    if (!phoneNumber.isValid()) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_SHOP_PHONE);
    }
    shopEntity.updateTelNumber(phoneNumber.getLocal());

    return ShopResponse.toDto(shopEntity);
  }
}
