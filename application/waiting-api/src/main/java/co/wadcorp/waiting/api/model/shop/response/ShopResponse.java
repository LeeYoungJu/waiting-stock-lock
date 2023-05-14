package co.wadcorp.waiting.api.model.shop.response;

import co.wadcorp.waiting.data.domain.shop.ShopEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ShopResponse {

  private final String shopId;
  private final String shopName;
  private final String shopAddress;
  private final String shopTelNumber;

  @Builder
  private ShopResponse(String shopId, String shopName, String shopAddress, String shopTelNumber) {
    this.shopId = shopId;
    this.shopName = shopName;
    this.shopAddress = shopAddress;
    this.shopTelNumber = shopTelNumber;
  }

  public static ShopResponse toDto(ShopEntity shopEntity) {
    return ShopResponse.builder()
        .shopId(shopEntity.getShopId())
        .shopName(shopEntity.getShopName())
        .shopAddress(shopEntity.getShopAddress())
        .shopTelNumber(shopEntity.getShopTelNumber())
        .build();
  }
}
