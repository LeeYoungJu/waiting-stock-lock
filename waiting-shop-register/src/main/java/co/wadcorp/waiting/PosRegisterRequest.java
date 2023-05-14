package co.wadcorp.waiting;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class PosRegisterRequest {

  private UserInfo userInfo;
  private ShopInfo shopInfo;
  private BusinessInfo businessInfo;

  @Data
  @Builder
  public static class UserInfo {
    private String email;
    private String userPw;
    private String phone;
  }

  @Data
  @Builder
  public static class ShopInfo {
    private String shopName;
    private Boolean isCatchPos;
    private Boolean isCatchWaiting;
    private String posMode;
    private Boolean isCashManagement;

  }

  @Data
  @Builder
  public static class BusinessInfo {
    private String bizShopName;
    private String bizNum;
    private String bizAddress;
    private String bizPresidentName;
    private String bizPhone;
  }
}