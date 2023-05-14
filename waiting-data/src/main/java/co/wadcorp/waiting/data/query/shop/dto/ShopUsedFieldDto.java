package co.wadcorp.waiting.data.query.shop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShopUsedFieldDto {

  private String shopId;
  private Boolean isUsedRemoteWaiting;
  private Boolean isMembership;

}
