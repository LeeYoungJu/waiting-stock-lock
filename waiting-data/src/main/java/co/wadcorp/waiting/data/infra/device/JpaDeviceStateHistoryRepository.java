package co.wadcorp.waiting.data.infra.device;

import co.wadcorp.waiting.data.domain.device.DeviceStateHistoryEntity;
import co.wadcorp.waiting.data.domain.device.DeviceStateHistoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDeviceStateHistoryRepository extends DeviceStateHistoryRepository,
    JpaRepository<DeviceStateHistoryEntity, Long> {

}
