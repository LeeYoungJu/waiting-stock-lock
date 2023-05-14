package co.wadcorp.waiting.data.domain.settings;

import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.data.support.BooleanYnConverter;
import co.wadcorp.waiting.data.support.OrderSettingsConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "cw_order_settings",
    indexes = {
        @Index(name = "cw_order_settings_shop_id_index", columnList = "shop_id")
    }
)
public class OrderSettingsEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "shop_id")
  private String shopId;

  @Column(name = "publish_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private boolean isPublished;

  @Column(name = "data", columnDefinition = "text")
  @Convert(converter = OrderSettingsConverter.class)
  private OrderSettingsData orderSettingsData;

  @Builder
  private OrderSettingsEntity(String shopId, OrderSettingsData orderSettingsData) {
    this.shopId = shopId;
    this.isPublished = true;
    this.orderSettingsData = orderSettingsData;
  }

  public void unPublish() {
    this.isPublished = false;
  }

  public boolean isPossibleOrder() {
    return orderSettingsData.isPossibleOrder();
  }

}
