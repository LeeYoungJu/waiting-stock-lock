package co.wadcorp.waiting.data.infra.setting;

import co.wadcorp.waiting.data.domain.settings.OptionSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOptionSettingsRepository extends OptionSettingsRepository, JpaRepository<OptionSettingsEntity, Long> {
}
