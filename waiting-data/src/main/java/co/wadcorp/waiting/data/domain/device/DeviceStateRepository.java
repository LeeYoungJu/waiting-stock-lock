package co.wadcorp.waiting.data.domain.device;

import java.util.Optional;

public interface DeviceStateRepository {

  Optional<DeviceStateEntity> findByDeviceUuid(String deviceUUId);

  DeviceStateEntity save(DeviceStateEntity deviceStateEntity);

}
