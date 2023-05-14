package co.wadcorp.waiting.data.infra.device;

import co.wadcorp.waiting.data.domain.device.DeviceStateEntity;
import co.wadcorp.waiting.data.domain.device.DeviceStateRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDeviceStateRepository extends DeviceStateRepository,
    JpaRepository<DeviceStateEntity, Long> {

}
