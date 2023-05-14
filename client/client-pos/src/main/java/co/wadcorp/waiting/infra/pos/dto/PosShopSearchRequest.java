package co.wadcorp.waiting.infra.pos.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@JsonInclude(Include.NON_NULL)
@Setter
@Getter
public class PosShopSearchRequest {

  private List<ShopServiceType> shopServiceTypes;
  private int page;
  private int pageSize;
  private Boolean isMembership;
  private List<FetchType> fetchTypes;

  @Builder
  private PosShopSearchRequest(List<ShopServiceType> shopServiceTypes, int page, int pageSize,
      Boolean isMembership, List<FetchType> fetchTypes) {
    this.shopServiceTypes = shopServiceTypes;
    this.page = page;
    this.pageSize = pageSize;
    this.isMembership = isMembership;
    this.fetchTypes = fetchTypes;
  }

  public enum ShopServiceType {
    WAITING, POS
  }

  public enum FetchType {
    BASIC, SHOP_USER, SERVICE_INFO, BUSINESS_INFO
  }

}
