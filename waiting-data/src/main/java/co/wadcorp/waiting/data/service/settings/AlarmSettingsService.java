package co.wadcorp.waiting.data.service.settings;

import co.wadcorp.waiting.data.domain.settings.AlarmSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.AlarmSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.DefaultAlarmSettingsDataFactory;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmSettingsService {

  private final AlarmSettingsRepository alarmSettingsRepository;

  public AlarmSettingsEntity getAlarmSettings(String shopId) {
    return alarmSettingsRepository.findFirstByShopIdAndIsPublished(shopId, true)
        .orElseGet(() -> createDefaultAlarmSettings(shopId));
  }

  public AlarmSettingsEntity save(AlarmSettingsEntity entity) {
    alarmSettingsRepository.findAllByShopIdInAndIsPublished(List.of(entity.getShopId()), true)
        .forEach(AlarmSettingsEntity::unPublish);

    return alarmSettingsRepository.save(entity);
  }

  private AlarmSettingsEntity createDefaultAlarmSettings(String shopId) {
    return new AlarmSettingsEntity(shopId, DefaultAlarmSettingsDataFactory.create());
  }
}
