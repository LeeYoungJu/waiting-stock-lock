package co.wadcorp.waiting.data.domain.device;

import co.wadcorp.waiting.data.support.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
@Table(name = "cw_device_state",
    indexes = {
        @Index(name = "cw_device_state_shop_id_index", columnList = "shop_id")
    }
)
public class DeviceStateEntity extends BaseEntity {

  @Id
  @Column(name = "device_uuid")
  private String deviceUuid;

  @Column(name = "shop_id")
  private String shopId;

  @Column(name = "server_address")
  private String serverAddress;

  @Column(name = "connection_state")
  @Enumerated(EnumType.STRING)
  private ConnectType connectionState;

  public DeviceStateEntity() {
  }

  @Builder
  public DeviceStateEntity(String deviceUuid, String shopId, String serverAddress,
      ConnectType connectionState) {
    this.deviceUuid = deviceUuid;
    this.shopId = shopId;
    this.serverAddress = serverAddress;
    this.connectionState = connectionState;
  }

  public void connect() {
    this.connectionState = ConnectType.CONNECTED;
  }

  public void subscribe() {
    this.connectionState = ConnectType.SUBSCRIBED;
  }

  public void disconnect() {
    this.connectionState = ConnectType.DISCONNECTED;
  }

  public void settingServerAddress(String serverAddress) {
    this.serverAddress = serverAddress;
  }

  public void settingShopId(String shopId) {
    this.shopId = shopId;
  }
}
