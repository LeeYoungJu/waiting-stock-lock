package co.wadcorp.waiting.api.internal.service.shop;

import static co.wadcorp.waiting.data.enums.OperationDay.MONDAY;
import static co.wadcorp.waiting.data.enums.OperationDay.TUESDAY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.internal.service.shop.dto.request.InternalDisablePutOffSettingsServiceRequest;
import co.wadcorp.waiting.api.internal.service.shop.dto.request.InternalRemoteShopOperationTimeSettingsServiceRequest;
import co.wadcorp.waiting.api.internal.service.shop.dto.request.InternalRemoteShopOperationTimeSettingsServiceRequest.RemoteOperationTimeSettingsServiceDto;
import co.wadcorp.waiting.data.domain.settings.putoff.DisablePutOffEntity;
import co.wadcorp.waiting.data.domain.settings.putoff.DisablePutOffRepository;
import co.wadcorp.waiting.data.domain.settings.remote.RemoteOperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.remote.RemoteOperationTimeSettingsRepository;
import co.wadcorp.waiting.data.enums.OperationDay;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class InternalShopInfoRegisterApiServiceTest extends IntegrationTest {

  @Autowired
  private InternalShopInfoRegisterApiService internalShopInfoRegisterApiService;

  @Autowired
  private RemoteOperationTimeSettingsRepository remoteOperationTimeSettingsRepository;

  @Autowired
  private DisablePutOffRepository disablePutOffRepository;

  @DisplayName("원격 요일별 운영시간 정보를 저장한다. 이전 설정은 비활성화 시킨다.")
  @Test
  void setRemoteOperationTimeSettings() {
    // given
    String shopId = "shopId-1";

    createRemoteOperationTimeSettings(shopId, MONDAY, LocalTime.of(10, 0), LocalTime.of(22, 0),
        true, false, null, null);
    createRemoteOperationTimeSettings(shopId, TUESDAY, LocalTime.of(10, 0), LocalTime.of(22, 0),
        true, false, null, null);
    createRemoteOperationTimeSettings("shopId-2", MONDAY, LocalTime.of(10, 0), LocalTime.of(22, 0),
        true, false, null, null);

    InternalRemoteShopOperationTimeSettingsServiceRequest request = InternalRemoteShopOperationTimeSettingsServiceRequest.builder()
        .settings(List.of(
            RemoteOperationTimeSettingsServiceDto.builder()
                .operationDay(MONDAY)
                .operationStartTime(LocalTime.of(9, 0))
                .operationEndTime(LocalTime.of(23, 0))
                .isClosedDay(false)
                .isUsedAutoPause(true)
                .autoPauseStartTime(LocalTime.of(14, 0))
                .autoPauseEndTime(LocalTime.of(15, 0))
                .build()
        ))
        .build();

    // when
    internalShopInfoRegisterApiService.setRemoteOperationTimeSettings(shopId, request);

    // then
    List<RemoteOperationTimeSettingsEntity> entities = remoteOperationTimeSettingsRepository.findAll();
    assertThat(entities).hasSize(4)
        .extracting("shopId", "operationDay", "operationStartTime", "operationEndTime",
            "isClosedDay", "isUsedAutoPause", "autoPauseStartTime", "autoPauseEndTime",
            "isPublished")
        .containsExactlyInAnyOrder(
            tuple(shopId, MONDAY, LocalTime.of(10, 0), LocalTime.of(22, 0), true, false, null,
                null, false),
            tuple(shopId, TUESDAY, LocalTime.of(10, 0), LocalTime.of(22, 0), true, false, null,
                null, false),
            tuple("shopId-2", MONDAY, LocalTime.of(10, 0), LocalTime.of(22, 0), true, false, null,
                null, true),
            tuple(shopId, MONDAY, LocalTime.of(9, 0), LocalTime.of(23, 0), false, true,
                LocalTime.of(14, 0), LocalTime.of(15, 0), true)
        );
  }

  @DisplayName("미루기 off를 설정한다. 이전 설정은 비활성화 시킨다.")
  @Test
  void setDisablePutOff() {
    // given
    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";

    createDisablePutOff(shopId1);
    createDisablePutOff(shopId2);

    InternalDisablePutOffSettingsServiceRequest request = InternalDisablePutOffSettingsServiceRequest.builder()
        .disablePutOff(true)
        .build();

    // when
    internalShopInfoRegisterApiService.setDisablePutOff(shopId1, request);

    // then
    List<DisablePutOffEntity> entities = disablePutOffRepository.findAll();
    assertThat(entities).hasSize(3)
        .extracting("shopId", "isPublished")
        .containsExactlyInAnyOrder(
            tuple(shopId1, false),
            tuple(shopId2, true),
            tuple(shopId1, true)
        );
  }

  @DisplayName("미루기 off를 해제하는 경우 새로 저장되는 Row는 없고, 이전 설정을 비활성화 시킨다.")
  @Test
  void releaseDisablePutOff() {
    // given
    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";

    createDisablePutOff(shopId1);
    createDisablePutOff(shopId2);

    InternalDisablePutOffSettingsServiceRequest request = InternalDisablePutOffSettingsServiceRequest.builder()
        .disablePutOff(false)
        .build();

    // when
    internalShopInfoRegisterApiService.setDisablePutOff(shopId1, request);

    // then
    List<DisablePutOffEntity> entities = disablePutOffRepository.findAll();
    assertThat(entities).hasSize(2)
        .extracting("shopId", "isPublished")
        .containsExactlyInAnyOrder(
            tuple(shopId1, false),
            tuple(shopId2, true)
        );
  }

  private RemoteOperationTimeSettingsEntity createRemoteOperationTimeSettings(String shopId,
      OperationDay operationDay, LocalTime operationStartTime, LocalTime operationEndTime,
      boolean isClosedDay, boolean isUsedAutoPause, LocalTime autoPauseStartTime,
      LocalTime autoPauseEndTime) {
    RemoteOperationTimeSettingsEntity remoteOperationTimeSettings = RemoteOperationTimeSettingsEntity.builder()
        .shopId(shopId)
        .operationDay(operationDay)
        .operationStartTime(operationStartTime)
        .operationEndTime(operationEndTime)
        .isClosedDay(isClosedDay)
        .isUsedAutoPause(isUsedAutoPause)
        .autoPauseStartTime(autoPauseStartTime)
        .autoPauseEndTime(autoPauseEndTime)
        .build();

    return remoteOperationTimeSettingsRepository.save(remoteOperationTimeSettings);
  }

  private DisablePutOffEntity createDisablePutOff(String shopId) {
    DisablePutOffEntity disablePutOff = DisablePutOffEntity.builder()
        .shopId(shopId)
        .build();
    return disablePutOffRepository.save(disablePutOff);
  }

}