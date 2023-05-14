package co.wadcorp.waiting.batch.job.settings.operation.store;

import static org.assertj.core.api.Assertions.assertThat;

import co.wadcorp.waiting.data.domain.settings.remote.RemoteOperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.remote.ShopRemoteOperationTimeSettings;
import co.wadcorp.waiting.data.enums.OperationDay;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

class ShopOperationInfoInitBatchStoreTest {

  @DisplayName("store에 원격 운영시간 정보를 매장 별로 저장할 수 있다.")
  @TestFactory
  Stream<DynamicTest> putRemoteOperationTimeSettings() {
    ShopOperationInfoInitBatchStore store = new ShopOperationInfoInitBatchStore();
    String shopId = "shopId";

    LocalTime operationStartTimeOnMonday = LocalTime.of(10, 0);
    LocalTime operationStartTimeOnTuesday = LocalTime.of(11, 0);

    return Stream.of(
        DynamicTest.dynamicTest(
            "기존에 저장하지 않았던 매장 ID로 운영시간 정보 저장 시 새로운 객체를 만든다.",
            () -> {
              // given
              RemoteOperationTimeSettingsEntity entity = RemoteOperationTimeSettingsEntity.builder()
                  .shopId(shopId)
                  .operationDay(OperationDay.MONDAY)
                  .operationStartTime(operationStartTimeOnMonday)
                  .build();

              // when
              store.putRemoteOperationTimeSettings(List.of(entity));

              // then
              Map<String, ShopRemoteOperationTimeSettings> map = store.getRemoteOperationTimeSettingsMap();
              assertThat(map).hasSize(1);

              ShopRemoteOperationTimeSettings settings = store.getRemoteOperationTimeSettings(
                  shopId);
              assertThat(settings).isNotSameAs(ShopRemoteOperationTimeSettings.EMPTY);

              LocalDate operationDate = LocalDate.of(2023, 4, 17); // 월요일
              assertThat(settings.getOperationStartTime(operationDate))
                  .isEqualTo(operationStartTimeOnMonday);
            }
        ),
        DynamicTest.dynamicTest(
            "기존에 저장했던 매장 ID로 운영시간 정보 저장 시 기존 객체에 값을 추가한다.",
            () -> {
              // given
              RemoteOperationTimeSettingsEntity entity = RemoteOperationTimeSettingsEntity.builder()
                  .shopId(shopId)
                  .operationDay(OperationDay.TUESDAY)
                  .operationStartTime(operationStartTimeOnTuesday)
                  .build();

              // when
              store.putRemoteOperationTimeSettings(List.of(entity));

              // then
              Map<String, ShopRemoteOperationTimeSettings> map = store.getRemoteOperationTimeSettingsMap();
              assertThat(map).hasSize(1);

              ShopRemoteOperationTimeSettings settings = store.getRemoteOperationTimeSettings(
                  shopId);
              assertThat(settings).isNotSameAs(ShopRemoteOperationTimeSettings.EMPTY);

              LocalDate operationDate1 = LocalDate.of(2023, 4, 17); // 월요일
              assertThat(settings.getOperationStartTime(operationDate1))
                  .isEqualTo(operationStartTimeOnMonday);
              LocalDate operationDate2 = LocalDate.of(2023, 4, 18); // 화요일
              assertThat(settings.getOperationStartTime(operationDate2))
                  .isEqualTo(operationStartTimeOnTuesday);
            }
        )
    );

  }

}