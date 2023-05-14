package co.wadcorp.waiting.data.domain.settings;

import co.wadcorp.waiting.data.enums.WaitingModeType;
import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.data.support.BooleanYnConverter;
import co.wadcorp.waiting.data.support.HomeSettingsConverter;
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
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString
@Table(name = "cw_home_settings")
public class HomeSettingsEntity extends BaseEntity {

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
  @Convert(converter = HomeSettingsConverter.class)
  private HomeSettingsData homeSettingsData;

  @Builder
  public HomeSettingsEntity(String shopId, HomeSettingsData homeSettingsData) {
    this.shopId = shopId;
    this.isPublished = true;
    this.homeSettingsData = homeSettingsData;
  }

  public void unPublish() {
    this.isPublished = false;
  }

  public WaitingModeType getWaitingModeType() {
    return WaitingModeType.valueOf(this.homeSettingsData.getWaitingModeType());
  }

  public SeatOptions getDefaultModeSettings() {
    return this.homeSettingsData.getDefaultModeSettings();
  }

  public List<SeatOptions> getTableModeSettings() {
    return this.homeSettingsData.getTableModeSettings();
  }

  public List<String> getTableModeSeatOptionNames(List<String> seatOptionIds) {
    if (seatOptionIds == null) {
      return List.of();
    }

    return this.homeSettingsData.getTableModeSettings()
        .stream()
        .filter(seatOption -> seatOptionIds.contains(seatOption.getId()))
        .map(SeatOptions::getName)
        .toList();
  }

}
