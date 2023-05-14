package co.wadcorp.waiting.data.domain.settings.remote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.wadcorp.waiting.data.enums.OperationDay;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ShopRemoteOperationTimeSettingsTest {

  @DisplayName("같은 요일에 대해 중복 등록하려고 하면 예외가 발생한다.")
  @Test
  void put() {
    // given
    ShopRemoteOperationTimeSettings shopSettings = ShopRemoteOperationTimeSettings.of(
        RemoteOperationTimeSettingsEntity.builder()
            .shopId("shopId-1")
            .operationDay(OperationDay.MONDAY)
            .build()
    );

    // when // then
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> shopSettings.put(RemoteOperationTimeSettingsEntity.builder()
            .shopId("shopId-1")
            .operationDay(OperationDay.MONDAY)
            .build()
        )
    );
    assertThat(exception.getMessage()).isEqualTo("요일 설정이 중복입니다. shopId=shopId-1");
  }

}