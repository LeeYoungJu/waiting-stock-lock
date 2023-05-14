package co.wadcorp.waiting.data.domain.settings;

import co.wadcorp.waiting.data.support.AlarmSettingsConverter;
import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.data.support.BooleanYnConverter;
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

@Getter
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "cw_alarm_settings")
public class AlarmSettingsEntity extends BaseEntity {

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
  @Convert(converter = AlarmSettingsConverter.class)
  private AlarmSettingsData alarmSettingsData;

  @Builder
  public AlarmSettingsEntity(String shopId, AlarmSettingsData alarmSettingsData) {
    this.shopId = shopId;
    this.isPublished = true;
    this.alarmSettingsData = alarmSettingsData;
  }

  public void unPublish() {
    this.alarmSettingsData.setDefaultIsAutoEnterAlarmIfNotExist();
    this.isPublished = false;
  }

  public Integer getAutoCancelPeriod() {
    return this.alarmSettingsData.getAutoCancelPeriod();
  }

  public boolean usedAutoCancel() {
    return this.alarmSettingsData.getIsUsedAutoCancel();
  }

  public boolean autoCancelOff() {
    return !usedAutoCancel();
  }

  public Integer getAutoAlarmOrdering() {
    return this.alarmSettingsData.getAutoAlarmOrdering();
  }

  public boolean isUsedAutoEnterAlarm() {
    this.alarmSettingsData.setDefaultIsAutoEnterAlarmIfNotExist();
    return this.alarmSettingsData.getIsAutoEnterAlarm();
  }
}
