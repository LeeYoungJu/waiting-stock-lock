package co.wadcorp.waiting.data.domain.shop;

import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.data.support.BooleanYnConverter;
import co.wadcorp.waiting.shared.util.PhoneNumberUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.StringUtils;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode(callSuper = true)
@Table(name = "cw_shop")
public class ShopEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "shop_id")
  private String shopId;

  @Column(name = "shop_name")
  private String shopName;

  @Column(name = "shop_address")
  private String shopAddress;

  @Column(name = "shop_tel_number")
  private String shopTelNumber;

  @Column(name = "remote_waiting_use_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private Boolean isUsedRemoteWaiting;

  @Column(name = "test_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private boolean isTest;

  @Column(name = "membership_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private boolean isMembership;

  @Builder
  public ShopEntity(String shopId, String shopName, String shopAddress, String shopTelNumber,
      Boolean isUsedRemoteWaiting, boolean isTest, boolean isMembership) {
    this.shopId = shopId;
    this.shopName = shopName;
    this.shopAddress = shopAddress;
    this.shopTelNumber = convertToLocal(shopTelNumber);
    this.isUsedRemoteWaiting = isUsedRemoteWaiting;
    this.isTest = isTest;
    this.isMembership = isMembership;
  }

  public void updateTelNumber(String shopTelNumber) {
    this.shopTelNumber = shopTelNumber;
  }

  public void update(String shopName, String shopAddress) {
    this.shopName = shopName;
    this.shopAddress = shopAddress;
  }

  public void update(String shopName, String shopAddress, String shopTelNumber) {
    update(shopName, shopAddress);
    this.shopTelNumber = convertToLocal(shopTelNumber);
  }

  private String convertToLocal(String shopTelNumber) {
    if (shopTelNumber == null) {
      return "";
    }

    String localPhoneNumber = PhoneNumberUtils.ofKr(shopTelNumber).getLocal();
    return localPhoneNumber != null
        ? localPhoneNumber
        : "";
  }

  public boolean isTest() {
    return this.isTest;
  }

  public boolean hasShopTelNumber() {
    return StringUtils.hasText(this.shopTelNumber);
  }

}
