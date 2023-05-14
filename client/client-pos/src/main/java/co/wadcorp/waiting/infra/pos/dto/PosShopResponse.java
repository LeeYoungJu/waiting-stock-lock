package co.wadcorp.waiting.infra.pos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PosShopResponse {
  private ShopInfo shopInfo;
  private BusinessInfo businessInfo;

  public PosShopResponse() {
  }

  public String getShopName() {
    return this.shopInfo.getShopName();
  }
}
