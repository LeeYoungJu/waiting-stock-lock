package co.wadcorp.waiting.data.query.shop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShopSeqShopIdDto {

  private Long seq;
  private String shopId;

  public ShopSeqShopIdDto(Long seq, String shopId) {
    this.seq = seq;
    this.shopId = shopId;
  }

}
