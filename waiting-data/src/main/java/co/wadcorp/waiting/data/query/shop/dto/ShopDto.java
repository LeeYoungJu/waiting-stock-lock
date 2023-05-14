package co.wadcorp.waiting.data.query.shop.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ShopDto {

  public static final ShopDto EMPTY = new ShopDto("", "", "", "");

  private String shopId;
  private String shopName;
  private String shopAddress;
  private String shopTelNumber;

  @QueryProjection
  public ShopDto(String shopId, String shopName, String shopAddress, String shopTelNumber) {
    this.shopId = shopId;
    this.shopName = shopName;
    this.shopAddress = shopAddress;
    this.shopTelNumber = shopTelNumber;
  }
}
