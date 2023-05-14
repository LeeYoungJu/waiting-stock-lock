package co.wadcorp.waiting.data.infra.setting;

import co.wadcorp.waiting.data.domain.settings.OrderSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrderSettingsRepository extends OrderSettingsRepository,
    JpaRepository<OrderSettingsEntity, Long> {

}
