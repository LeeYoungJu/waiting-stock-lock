package co.wadcorp.waiting.data.domain.device;

import co.wadcorp.waiting.data.support.BaseHistoryEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "cw_device_state_history",
    indexes = {
        @Index(name = "cw_device_state_history_shop_id_index", columnList = "shop_id")
    }
)
public class DeviceStateHistoryEntity extends BaseHistoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "device_uuid")
  private String deviceUuid;

  @Column(name = "shop_id")
  private String shopId;

  @Column(name = "server_address")
  private String serverAddress;

  @Column(name = "connection_state")
  @Enumerated(EnumType.STRING)
  private ConnectType connectionState;

  public DeviceStateHistoryEntity() {
  }

  public DeviceStateHistoryEntity(DeviceStateEntity deviceState) {
    this.deviceUuid = deviceState.getDeviceUuid();
    this.shopId = deviceState.getShopId();
    this.serverAddress = deviceState.getServerAddress();
    this.connectionState = deviceState.getConnectionState();
  }
}
