package co.wadcorp.waiting.data.infra.setting;

import co.wadcorp.waiting.data.domain.settings.AlarmSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.AlarmSettingsRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAlarmSettingsRepository extends AlarmSettingsRepository, JpaRepository<AlarmSettingsEntity, Long> {
}
