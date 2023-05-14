package co.wadcorp.waiting.data.domain.waiting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.data.domain.settings.DefaultOperationTimeSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.AutoPauseSettings;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.OperationTimeForDay;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.fixture.OperationTimeSettingDataFixture;
import co.wadcorp.waiting.data.domain.settings.remote.RemoteOperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.remote.ShopRemoteOperationTimeSettings;
import co.wadcorp.waiting.data.domain.shop.operation.ClosedReason;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInitializeFactory;
import co.wadcorp.waiting.data.domain.shop.operation.status.RegistrableStatus;
import co.wadcorp.waiting.data.enums.OperationDay;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ShopOperationInitializeFactoryTest {

  @Test
  void open_initialize() {
    String shopId = "SHOP_ID";
    OperationTimeSettingsEntity operationTimeSettingsEntity = new OperationTimeSettingsEntity(
        shopId, DefaultOperationTimeSettingDataFactory.create());
    LocalDate operationDate = LocalDate.of(2023, 2, 15);
    LocalTime nowTime = LocalTime.of(11, 0);

    ShopOperationInfoEntity initialize = ShopOperationInitializeFactory.initialize(
        operationTimeSettingsEntity, operationDate);

    assertEquals(RegistrableStatus.OPEN, initialize.getRegistrableStatus());
    assertEquals(shopId, initialize.getShopId());
    assertEquals(operationDate, initialize.getOperationDate());
    assertEquals(ZonedDateTime.of(LocalDateTime.of(operationDate, LocalTime.of(10, 0)),
        ZoneId.of("Asia/Seoul")), initialize.getOperationStartDateTime());
    assertEquals(ZonedDateTime.of(LocalDateTime.of(operationDate, LocalTime.of(20, 0)),
        ZoneId.of("Asia/Seoul")), initialize.getOperationEndDateTime());
  }

  @Test
  void closed_day_initialize() {
    String shopId = "SHOP_ID";
    OperationTimeSettingsEntity operationTimeSettingsEntity = new OperationTimeSettingsEntity(
        shopId, OperationTimeSettingDataFixture.closedDayFixture(UUIDUtil.shortUUID()));

    LocalDate operationDate = LocalDate.of(2023, 2, 15);
    LocalTime nowTime = LocalTime.of(14, 30);

    ShopOperationInfoEntity initialize = ShopOperationInitializeFactory.initialize(
        operationTimeSettingsEntity, operationDate);

    assertEquals(shopId, initialize.getShopId());
    assertEquals(operationDate, initialize.getOperationDate());
    assertEquals(RegistrableStatus.CLOSED, initialize.getRegistrableStatus());
    assertEquals(ClosedReason.CLOSED_DAY, initialize.getClosedReason());

    assertEquals(ZonedDateTime.of(LocalDateTime.of(operationDate, LocalTime.of(10, 0)),
        ZoneId.of("Asia/Seoul")), initialize.getOperationStartDateTime());
    assertEquals(ZonedDateTime.of(LocalDateTime.of(operationDate, LocalTime.of(20, 0)),
        ZoneId.of("Asia/Seoul")), initialize.getOperationEndDateTime());
  }


  @Test
  void pause_initialize() {
    String shopId = "SHOP_ID";
    OperationTimeSettingsEntity operationTimeSettingsEntity = new OperationTimeSettingsEntity(
        shopId, OperationTimeSettingDataFixture.pauseFixture(UUIDUtil.shortUUID()));

    LocalDate operationDate = LocalDate.of(2023, 2, 15);
    LocalTime nowTime = LocalTime.of(14, 30);

    ShopOperationInfoEntity initialize = ShopOperationInitializeFactory.initialize(
        operationTimeSettingsEntity, operationDate);

    assertEquals(shopId, initialize.getShopId());
    assertEquals(operationDate, initialize.getOperationDate());
    assertEquals(RegistrableStatus.OPEN, initialize.getRegistrableStatus());
    assertEquals(ZonedDateTime.of(LocalDateTime.of(operationDate, LocalTime.of(14, 0)),
        ZoneId.of("Asia/Seoul")), initialize.getAutoPauseStartDateTime());
    assertEquals(ZonedDateTime.of(LocalDateTime.of(operationDate, LocalTime.of(15, 0)),
        ZoneId.of("Asia/Seoul")), initialize.getAutoPauseEndDateTime());

    assertEquals(ZonedDateTime.of(LocalDateTime.of(operationDate, LocalTime.of(10, 0)),
        ZoneId.of("Asia/Seoul")), initialize.getOperationStartDateTime());
    assertEquals(ZonedDateTime.of(LocalDateTime.of(operationDate, LocalTime.of(20, 0)),
        ZoneId.of("Asia/Seoul")), initialize.getOperationEndDateTime());
  }

  @DisplayName("원격 운영시간 정보가 존재한다면 해당 정보를 세팅한다.")
  @Test
  void initializeWithRemoteOperationTimeSettings() {
    // given
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 4, 17); // 월요일

    OperationTimeSettingsEntity operationTimeSettings = OperationTimeSettingsEntity.builder()
        .shopId(shopId)
        .operationTimeSettingsData(OperationTimeSettingsData.builder()
            .operationTimeForDays(List.of(OperationTimeForDay.builder()
                .day("MONDAY")
                .isClosedDay(false)
                .operationStartTime(LocalTime.of(9, 0))
                .operationEndTime(LocalTime.of(23, 0))
                .build()
            ))
            .isUsedAutoPause(false)
            .autoPauseSettings(AutoPauseSettings.builder()
                .pauseReasons(List.of())
                .build()
            )
            .build()
        )
        .build();

    ShopRemoteOperationTimeSettings shopRemoteSettings = ShopRemoteOperationTimeSettings.of(
        RemoteOperationTimeSettingsEntity.builder()
            .shopId(shopId)
            .operationDay(OperationDay.MONDAY)
            .operationStartTime(LocalTime.of(10, 0))
            .operationEndTime(LocalTime.of(22, 0))
            .isUsedAutoPause(true)
            .autoPauseStartTime(LocalTime.of(14, 0))
            .autoPauseEndTime(LocalTime.of(15, 0))
            .build()
    );

    // when
    ShopOperationInfoEntity entity = ShopOperationInitializeFactory.initialize(
        operationTimeSettings, shopRemoteSettings, operationDate);

    // then
    assertThat(entity)
        .extracting(
            "remoteOperationStartDateTime", "remoteOperationEndDateTime",
            "remoteAutoPauseInfo.remoteAutoPauseStartDateTime",
            "remoteAutoPauseInfo.remoteAutoPauseEndDateTime"
        )
        .contains(
            ZonedDateTimeUtils.ofSeoul(operationDate, LocalTime.of(10, 0)),
            ZonedDateTimeUtils.ofSeoul(operationDate, LocalTime.of(22, 0)),
            ZonedDateTimeUtils.ofSeoul(operationDate, LocalTime.of(14, 0)),
            ZonedDateTimeUtils.ofSeoul(operationDate, LocalTime.of(15, 0))
        );
  }

}