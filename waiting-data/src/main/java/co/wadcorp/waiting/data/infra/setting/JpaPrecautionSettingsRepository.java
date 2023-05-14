package co.wadcorp.waiting.data.infra.setting;

import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPrecautionSettingsRepository extends PrecautionSettingsRepository, JpaRepository<PrecautionSettingsEntity, Long> {
}
