package co.wadcorp.waiting.api.model.shop.vo;

import co.wadcorp.waiting.infra.pos.dto.BusinessInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BusinessInfoVO {

  private final String bizShopName;
  private final String bizNum;
  private final String bizAddress;
  private final String bizPresidentName;
  private final String bizPhone;
  private final String vanCorporation;

  @Builder
  public BusinessInfoVO(String bizShopName, String bizNum, String bizAddress,
      String bizPresidentName,
      String bizPhone, String vanCorporation) {
    this.bizShopName = bizShopName;
    this.bizNum = bizNum;
    this.bizAddress = bizAddress;
    this.bizPresidentName = bizPresidentName;
    this.bizPhone = bizPhone;
    this.vanCorporation = vanCorporation;
  }

  public static BusinessInfoVO toDto(BusinessInfo businessInfo) {
    return BusinessInfoVO.builder()
        .bizShopName(businessInfo.getBizShopName())
        .bizNum(businessInfo.getBizNum())
        .bizAddress(businessInfo.getBizAddress())
        .bizPresidentName(businessInfo.getBizPresidentName())
        .bizPhone(businessInfo.getBizPhone())
        .vanCorporation(businessInfo.getVanCorporation())
        .build();
  }
}
