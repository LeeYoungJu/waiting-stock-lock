package co.wadcorp.waiting.api.internal.service.shop.dto.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.wadcorp.waiting.api.internal.service.shop.dto.request.InternalRemoteShopOperationTimeSettingsServiceRequest.RemoteOperationTimeSettingsServiceDto;
import co.wadcorp.waiting.data.enums.OperationDay;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InternalRemoteShopOperationTimeSettingsServiceRequestTest {

  @DisplayName("중복되는 요일 설정은 불가하다.")
  @Test
  void haveDuplicateOperationDaySettings() {
    // given
    InternalRemoteShopOperationTimeSettingsServiceRequest request = InternalRemoteShopOperationTimeSettingsServiceRequest.builder()
        .settings(List.of(
            RemoteOperationTimeSettingsServiceDto.builder()
                .operationDay(OperationDay.MONDAY)
                .build(),
            RemoteOperationTimeSettingsServiceDto.builder()
                .operationDay(OperationDay.MONDAY)
                .build()
        ))
        .build();

    // when // then
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> request.toEntities("shopId")
    );

    assertThat(exception.getMessage()).isEqualTo("요일 설정이 중복입니다.");
  }

}