package co.wadcorp.waiting.infra.pos.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

@Setter
@Getter
public class PosSearchShopsResponse {

  private PagingInfo pagingInfo;
  private List<SearchShopInfo> data;

  public PosSearchShopsResponse() {
  }

  public PosSearchShopsResponse(PagingInfo pagingInfo) {
    this.pagingInfo = pagingInfo;
  }

  public static class PagingInfo {
    private int total;
    private int page;
    private int pageSize;
  }

  @ToString
  @Getter
  @Setter
  public static class SearchShopInfo {
    private long shopSeq;
    private String shopId;
    private BasicInfo basicInfo;
    private BusinessInfo businessInfo;

    @ToString
    @Setter
    @Getter
    public static class BasicInfo {
      private String shopId;
      private String shopName;
    }

    @ToString
    @Setter
    @Getter
    public static class BusinessInfo {
      private String bizAddress;
      private String bizPhone;
    }

    public String getShopName() {
      return basicInfo != null
          ? basicInfo.getShopName()
          : "";
    }

    public String getShopAddress() {
      return businessInfo != null && StringUtils.hasText(businessInfo.getBizAddress())
          ? businessInfo.getBizAddress()
          : "";
    }

    public String getShopTelNumber() {
      return businessInfo != null && StringUtils.hasText(businessInfo.getBizPhone())
          ? businessInfo.getBizPhone()
          : "";
    }
  }

}
