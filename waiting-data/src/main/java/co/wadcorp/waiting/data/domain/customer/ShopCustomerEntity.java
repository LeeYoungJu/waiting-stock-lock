package co.wadcorp.waiting.data.domain.customer;

import co.wadcorp.waiting.data.support.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode(callSuper = true)
@Table(name = "cw_shop_customer")
public class ShopCustomerEntity extends BaseEntity {

  @EmbeddedId
  private ShopCustomerId shopCustomerId;

  @Column(name = "name")
  private String name;

  @Column(name = "memo")
  private String memo;

  @Column(name = "visit_count")
  private int visitCount;

  @Column(name = "cancel_count")
  private int cancelCount;

  @Column(name = "noshow_count")
  private int noshowCount;

  @Column(name = "sitting_count")
  private int sittingCount;

  @Column(name = "last_visit_date")
  private LocalDate lastVisitDate;

  @Builder
  public ShopCustomerEntity(ShopCustomerId shopCustomerId, String name, String memo, int visitCount,
      int cancelCount, int noshowCount, int sittingCount, LocalDate lastVisitDate) {
    this.shopCustomerId = shopCustomerId;
    this.name = name;
    this.memo = memo;
    this.visitCount = visitCount;
    this.cancelCount = cancelCount;
    this.noshowCount = noshowCount;
    this.sittingCount = sittingCount;
    this.lastVisitDate = lastVisitDate;
  }

  public static ShopCustomerEntity ofDefault(String shopId, Long customerSeq) {
    return ofDefault(shopId, customerSeq, null);
  }

  public static ShopCustomerEntity ofDefault(String shopId, Long customerSeq, String customerName) {
    return ShopCustomerEntity.builder()
        .shopCustomerId(ShopCustomerId.builder()
            .shopId(shopId)
            .customerSeq(customerSeq)
            .build())
        .name(customerName)
        .visitCount(0)
        .cancelCount(0)
        .noshowCount(0)
        .sittingCount(0)
        .build();
  }

  public static ShopCustomerEntity ofName(String name) {
    ShopCustomerEntity shopCustomerEntity = new ShopCustomerEntity();
    shopCustomerEntity.name = name;
    return shopCustomerEntity;

  }

  public void updateVisitCount() {
    this.visitCount++;
    this.lastVisitDate = LocalDate.now();
  }

  public Long getCustomerSeq() {
    if (Objects.isNull(this.shopCustomerId)) {
      return null; // 고객 ID가 없는경우는 수기등록 할 때
    }

    return this.shopCustomerId.getCustomerSeq();
  }

  public void increaseCancelCount() {
    this.cancelCount++;
  }

  public void increaseNoShowCount() {
    this.noshowCount++;
  }

  public void increaseSittingCount() {
    this.sittingCount++;
  }

  public void decreaseCancelCount() {
    this.cancelCount--;
  }

  public void decreaseNoShowCount() {
    this.noshowCount--;
  }

  public void decreaseSittingCount() {
    this.sittingCount--;
  }

}
