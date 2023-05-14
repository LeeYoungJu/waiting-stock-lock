package co.wadcorp.waiting.data.service.waiting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.wadcorp.waiting.data.domain.settings.FakeOperationTimeSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.fixture.OperationTimeSettingDataFixture;
import co.wadcorp.waiting.data.domain.shop.operation.pause.AutoPauseInfo;
import co.wadcorp.waiting.data.domain.waiting.FakeShopOperationInfoHistoryRepository;
import co.wadcorp.waiting.data.domain.waiting.FakeShopOperationInfoRepository;
import co.wadcorp.waiting.data.domain.shop.operation.status.RegistrableStatus;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoHistoryEntity;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.jdbc.core.JdbcTemplate;

class ShopOperationInfoServiceTest {

  private static final ZonedDateTime NOW_LOCAL_DATE_TIME = ZonedDateTimeUtils.ofSeoul(
      LocalDate.of(2023, 2, 21), LocalTime.of(12, 0));
  private static final LocalTime OPERATION_START_TIME = LocalTime.of(10, 0);
  private static final LocalTime OPERATION_END_TIME = LocalTime.of(20, 0);
  private static final LocalTime AUTO_PAUSE_START_TIME = LocalTime.of(14, 0);
  private static final LocalTime AUTO_PAUSE_END_TIME = LocalTime.of(15, 0);


  private final FakeShopOperationInfoRepository fakeShopOperationInfoRepository = new FakeShopOperationInfoRepository();
  private final FakeShopOperationInfoHistoryRepository fakeShopOperationInfoHistoryRepository = new FakeShopOperationInfoHistoryRepository();

  private final OperationTimeSettingsRepository fakeOperationTimeSettingsRepository = new FakeOperationTimeSettingsRepository();

  private final ShopOperationInfoService shopOperationInfoService = new ShopOperationInfoService(
      fakeShopOperationInfoRepository,
      fakeShopOperationInfoHistoryRepository,
      fakeOperationTimeSettingsRepository,
      new JdbcTemplate()
  );

  @Test
  @DisplayName("매장 운영 정보를 조회할 수 있다.")
  void find() {
    LocalDate operationDate = LocalDate.now();
    ShopOperationInfoEntity givenEntity = shopOperationInfoService.save(
        createShopOperationEntity("shopId",
            operationDate, RegistrableStatus.CLOSED));

    ShopOperationInfoEntity targetEntity = shopOperationInfoService.findByShopIdAndOperationDate(
        givenEntity.getShopId(), givenEntity.getOperationDate());

    assertEquals(givenEntity, targetEntity);
  }

  @Test
  @DisplayName("저장하지 않은 매장 운영 정보는 조회할 수 없다.")
  void find_not_found() {
    assertThrows(AppException.class,
        () -> shopOperationInfoService.findByShopIdAndOperationDate(
            "shopId", LocalDate.now()));
  }

  @Test
  @DisplayName("매장 운영 정보를 저장할 수 있다.")
  void save() {
    LocalDate operationDate = LocalDate.now();
    ShopOperationInfoEntity givenEntity = createShopOperationEntity("shopId", operationDate,
        RegistrableStatus.CLOSED);
    ShopOperationInfoEntity savedEntity = shopOperationInfoService.save(givenEntity);

    List<ShopOperationInfoHistoryEntity> historyEntities = fakeShopOperationInfoHistoryRepository.findByShopIdAndOperationDate(
        givenEntity.getShopId(), givenEntity.getOperationDate());

    ShopOperationInfoHistoryEntity historyEntity = ShopOperationInfoHistoryEntity.of(savedEntity);

    assertEquals(givenEntity, savedEntity);
    assertEquals(1, historyEntities.size());
    assertEquals(historyEntity, historyEntities.get(0));
  }

  @Test
  @DisplayName("같은 영업일에 매장 운영 정보가 이미 저장 되어있다면 저장할 수 없다.")
  void save_already() {
    LocalDate operationDate = LocalDate.now();
    shopOperationInfoService.save(
        createShopOperationEntity("shopId", operationDate, RegistrableStatus.CLOSED));

    assertThrows(AppException.class, () -> {
      shopOperationInfoService.save(
          createShopOperationEntity("shopId", operationDate, RegistrableStatus.CLOSED));
    });
  }


  @Test
  @DisplayName("매장 운영 상태를 운영 중으로 바꿀 수 있다.")
  void open() {
    LocalDate operationDate = LocalDate.now();
    String shopId = "shopId";
    fakeShopOperationInfoRepository.save(createShopOperationEntity(shopId, operationDate,
        RegistrableStatus.CLOSED));

    ShopOperationInfoEntity changeStatus = shopOperationInfoService.open(shopId, operationDate,
        NOW_LOCAL_DATE_TIME);

    assertEquals(RegistrableStatus.OPEN, changeStatus.getRegistrableStatus());
  }

  @Test
  @DisplayName("매장 운영 정보가 없다면 운영 중으로 바꿀 수 없다.")
  void open_fail() {
    LocalDate operationDate = LocalDate.now();
    String shopId = "shopId";

    assertThrows(AppException.class, () -> {
      shopOperationInfoService.open(shopId, operationDate, NOW_LOCAL_DATE_TIME);
    });
  }


  @Test
  @DisplayName("매장 운영 시작 시간 이전에 오픈하는 경우 운영 시작 시각을 현재 시간으로 강제 설정")
  void open_before_operation_start_date_time() {
    LocalDate operationDate = LocalDate.now();
    String shopId = "shopId";
    fakeShopOperationInfoRepository.save(createShopOperationEntity(shopId, operationDate,
        RegistrableStatus.CLOSED));

    ZonedDateTime nowDateTime = ZonedDateTimeUtils.ofSeoul(operationDate, LocalTime.of(5, 0));

    ShopOperationInfoEntity changeStatus = shopOperationInfoService.open(shopId, operationDate,
        nowDateTime);

    assertEquals(RegistrableStatus.OPEN, changeStatus.getRegistrableStatus());
    assertEquals(nowDateTime, changeStatus.getOperationStartDateTime());

  }


  @Test
  @DisplayName("매장 운영 종료 시간 이후에 오픈하는 경우 운영 종료 시간 5:30으로 강제 설정")
  void open_after_operation_end_date_time() {
    LocalDate operationDate = LocalDate.now();
    String shopId = "shopId";
    ZonedDateTime nowDateTime = ZonedDateTimeUtils.ofSeoul(operationDate, LocalTime.of(23, 0));
    ZonedDateTime endDateTime = ZonedDateTimeUtils.ofSeoul(operationDate.plusDays(1),
        LocalTime.of(5, 30));

    fakeShopOperationInfoRepository.save(
        createShopOperationEntity(shopId, operationDate, RegistrableStatus.CLOSED));

    ShopOperationInfoEntity changeStatus = shopOperationInfoService.open(shopId, operationDate,
        nowDateTime);

    assertEquals(RegistrableStatus.OPEN, changeStatus.getRegistrableStatus());
    assertEquals(endDateTime, changeStatus.getOperationEndDateTime());
  }

  @DisplayName("자동 일시중지 시간 도중 오픈하는 경우 자동 일시중지 정보를 제거하고 오픈한다.")
  @CsvSource({
      "14:00:00",
      "14:00:01",
      "15:00:00"
  })
  @ParameterizedTest
  void open_between_auto_pause_date_time(LocalTime nowTime) {
    LocalDate operationDate = LocalDate.now();
    String shopId = "shopId";
    fakeShopOperationInfoRepository.save(createShopOperationEntity(shopId, operationDate,
        RegistrableStatus.CLOSED));

    ZonedDateTime nowDateTime = ZonedDateTimeUtils.ofSeoul(operationDate, nowTime);

    ShopOperationInfoEntity changeStatus = shopOperationInfoService.open(shopId, operationDate,
        nowDateTime);

    assertEquals(RegistrableStatus.OPEN, changeStatus.getRegistrableStatus());
    assertThat(changeStatus.getAutoPauseInfo()).isNull();
  }

  @Test
  @DisplayName("매장 운영 상태를 운영 종료로 바꿀 수 있다.")
  void close() {
    LocalDate operationDate = LocalDate.now();
    String shopId = "shopId";
    fakeShopOperationInfoRepository.save(createShopOperationEntity(shopId, operationDate,
        RegistrableStatus.OPEN));

    ShopOperationInfoEntity changeStatus = shopOperationInfoService.close(shopId, operationDate);

    assertEquals(RegistrableStatus.CLOSED, changeStatus.getRegistrableStatus());
  }

  @Test
  @DisplayName("매장 운영 정보가 없다면 운영 종료로 바꿀 수 없다.")
  void close_fail() {
    LocalDate operationDate = LocalDate.now();
    String shopId = "shopId";

    assertThrows(AppException.class, () -> {
      shopOperationInfoService.close(shopId, operationDate);
    });
  }

  @Test
  @DisplayName("매장 운영 상태를 바로 입장으로 바꿀 수 있다.")
  void byPass() {
    LocalDate operationDate = LocalDate.now();
    String shopId = "shopId";
    fakeShopOperationInfoRepository.save(createShopOperationEntity(shopId, operationDate,
        RegistrableStatus.OPEN));

    ShopOperationInfoEntity changeStatus = shopOperationInfoService.byPass(shopId, operationDate);

    assertEquals(RegistrableStatus.BY_PASS, changeStatus.getRegistrableStatus());
  }

  @Test
  @DisplayName("매장 운영 정보가 없다면 바로 입장으로 바꿀 수 없다.")
  void byPass_fail() {
    LocalDate operationDate = LocalDate.now();
    String shopId = "shopId";

    assertThrows(AppException.class, () -> {
      shopOperationInfoService.byPass(shopId, operationDate);
    });
  }


  @Test
  @DisplayName("매장 운영 상태를 일시 중지로 바꿀 수 있다.")
  void pause() {
    LocalDate operationDate = LocalDate.now();
    String shopId = "shopId";
    fakeShopOperationInfoRepository.save(createShopOperationEntity(shopId, operationDate,
        RegistrableStatus.OPEN));

    String pauseReasonUuid = "PAUSE_REASON_UUID";
    OperationTimeSettingsData operationTimeSettingsData = OperationTimeSettingDataFixture.pauseFixture(
        pauseReasonUuid);

    fakeOperationTimeSettingsRepository.save(
        new OperationTimeSettingsEntity(shopId, operationTimeSettingsData));

    ShopOperationInfoEntity changeStatus =
        shopOperationInfoService.pause(shopId, operationDate, pauseReasonUuid, 110);

    assertEquals(RegistrableStatus.OPEN, changeStatus.getRegistrableStatus());
    assertNotNull(changeStatus.getManualPauseInfo().getManualPauseStartDateTime());
    assertNotNull(changeStatus.getManualPauseInfo().getManualPauseEndDateTime());
  }

  @Test
  @DisplayName("매장 운영 상태를 일시 중지 미정으로 바꿀 수 있다.")
  void pause_undefined() {
    LocalDate operationDate = LocalDate.now();
    String shopId = "shopId";
    fakeShopOperationInfoRepository.save(createShopOperationEntity(shopId, operationDate,
        RegistrableStatus.OPEN));

    String pauseReasonUuid = "PAUSE_REASON_UUID";
    OperationTimeSettingsData operationTimeSettingsData = OperationTimeSettingDataFixture.pauseFixture(
        pauseReasonUuid);
    fakeOperationTimeSettingsRepository.save(
        new OperationTimeSettingsEntity(shopId, operationTimeSettingsData));

    ShopOperationInfoEntity changeStatus = shopOperationInfoService.pause(shopId, operationDate,
        pauseReasonUuid, -1);

    assertEquals(RegistrableStatus.OPEN, changeStatus.getRegistrableStatus());
    assertNotNull(changeStatus.getManualPauseStartDateTime());
    assertNull(changeStatus.getManualPauseEndDateTime());
  }

  @Test
  @DisplayName("매장 운영 정보가 없다면 일시 중지로 바꿀 수 없다.")
  void pause_fail() {
    LocalDate operationDate = LocalDate.now();
    String shopId = "shopId";

    assertThrows(AppException.class, () ->
        shopOperationInfoService.pause(shopId, operationDate, "메모", 110));
  }


  @Test
  @DisplayName("매장 일시 중지 시간은 180분 이상 넘어갈 수 없다.")
  void pause_out_of_period() {
    LocalDate operationDate = LocalDate.now();
    String shopId = "shopId";
    fakeShopOperationInfoRepository.save(createShopOperationEntity(shopId, operationDate,
        RegistrableStatus.OPEN));

    assertThrows(AppException.class, () ->
        shopOperationInfoService.pause(shopId, operationDate, "메모", 200));
  }


  private static ShopOperationInfoEntity createShopOperationEntity(String shopId,
      LocalDate operationDate, RegistrableStatus registrableStatus) {
    return ShopOperationInfoEntity.builder()
        .shopId(shopId)
        .operationDate(operationDate)
        .operationStartDateTime(
            ZonedDateTimeUtils.ofSeoul(operationDate, OPERATION_START_TIME))
        .operationEndDateTime(
            ZonedDateTimeUtils.ofSeoul(operationDate, OPERATION_END_TIME))
        .registrableStatus(registrableStatus)
        .autoPauseInfo(AutoPauseInfo.builder()
            .autoPauseStartDateTime(
                ZonedDateTimeUtils.ofSeoul(operationDate, AUTO_PAUSE_START_TIME))
            .autoPauseEndDateTime(ZonedDateTimeUtils.ofSeoul(operationDate, AUTO_PAUSE_END_TIME))
            .build())
        .build();
  }


}