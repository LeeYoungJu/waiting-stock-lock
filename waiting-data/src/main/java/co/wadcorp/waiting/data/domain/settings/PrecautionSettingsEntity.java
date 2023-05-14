package co.wadcorp.waiting.data.domain.settings;

import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.data.support.BooleanYnConverter;
import co.wadcorp.waiting.data.support.PrecautionSettingsConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "cw_precaution_settings")
public class PrecautionSettingsEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "shop_id")
  private String shopId;

  @Column(name = "publish_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private Boolean isPublished;

  @Column(name = "data", columnDefinition = "text")
  @Convert(converter = PrecautionSettingsConverter.class)
  private PrecautionSettingsData precautionSettingsData;

  @Builder
  public PrecautionSettingsEntity(String shopId, PrecautionSettingsData precautionSettingsData) {
    this.shopId = shopId;
    this.isPublished = true;
    this.precautionSettingsData = precautionSettingsData;
  }

  public void unPublish() {
    this.isPublished = false;
  }

  public String getMessagePrecaution() {
    return this.precautionSettingsData.getMessagePrecaution();
  }

  public boolean isUsedPrecautions() {
    return this.precautionSettingsData.getIsUsedPrecautions();
  }

  public List<Precaution> getPrecautions() {
    List<Precaution> precautions = this.precautionSettingsData.getPrecautions();
    if (precautions == null || precautions.isEmpty()) {
      return List.of();
    }

    return precautions;
  }

  public String getPrecautionsText() {
    return this.precautionSettingsData.getPrecautionsText();
  }
}
