package co.wadcorp.waiting.data.service.device;

import co.wadcorp.waiting.data.domain.device.DeviceStateEntity;
import co.wadcorp.waiting.data.domain.device.DeviceStateHistoryEntity;
import co.wadcorp.waiting.data.domain.device.DeviceStateHistoryRepository;
import co.wadcorp.waiting.data.domain.device.DeviceStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class DeviceWebsocketService {

  private final DeviceStateRepository deviceStateRepository;
  private final DeviceStateHistoryRepository deviceStateHistoryRepository;

  public void connect(String deviceUuid, String hostAddress) {
    DeviceStateEntity deviceStateEntity = getByDeviceUuid(deviceUuid);

    deviceStateEntity.connect();
    deviceStateEntity.settingServerAddress(hostAddress);

    save(deviceStateEntity);
  }


  public void subscribe(String deviceUuid, String hostAddress, String shopId) {
    DeviceStateEntity deviceStateEntity = getByDeviceUuid(deviceUuid);

    deviceStateEntity.subscribe();
    deviceStateEntity.settingShopId(shopId);
    deviceStateEntity.settingServerAddress(hostAddress);

    save(deviceStateEntity);
  }

  public void disconnect(String deviceUuid, String hostAddress) {
    DeviceStateEntity deviceStateEntity = getByDeviceUuid(deviceUuid);

    deviceStateEntity.disconnect();
    deviceStateEntity.settingServerAddress(hostAddress);

    save(deviceStateEntity);
  }

  private DeviceStateEntity getByDeviceUuid(String deviceUuid) {
    return deviceStateRepository.findByDeviceUuid(deviceUuid)
        .orElseGet(() -> DeviceStateEntity.builder().deviceUuid(deviceUuid)
            .build());
  }

  private void save(DeviceStateEntity deviceStateEntity) {
    deviceStateRepository.save(deviceStateEntity);
    deviceStateHistoryRepository.save(new DeviceStateHistoryEntity(deviceStateEntity));
  }

}
