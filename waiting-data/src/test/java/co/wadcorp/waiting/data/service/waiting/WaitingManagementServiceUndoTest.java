package co.wadcorp.waiting.data.service.waiting;

import static co.wadcorp.waiting.data.domain.settings.fixture.HomeSettingsFixture.createDefaultHomeSettingsWithRegDateTime;
import static co.wadcorp.waiting.data.domain.settings.fixture.OptionSettingsFixture.createDefaultOptionSettingsWithRegDateTime;
import static co.wadcorp.waiting.data.domain.waiting.fixture.WaitingFixture.createSittingWaiting;
import static co.wadcorp.waiting.data.domain.waiting.fixture.WaitingFixture.createSittingWaitingWithRegDateTime;
import static co.wadcorp.waiting.data.domain.waiting.fixture.WaitingFixture.createWaiting;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.wadcorp.waiting.data.domain.settings.FakeHomeSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.FakeOptionSettingsRepository;
import co.wadcorp.waiting.data.domain.waiting.FakeWaitingHistoryRepository;
import co.wadcorp.waiting.data.domain.waiting.FakeWaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WaitingManagementServiceUndoTest {

  private FakeWaitingRepository waitingRepository;
  private FakeWaitingHistoryRepository waitingHistoryRepository;
  private FakeHomeSettingsRepository homeSettingsRepository;
  private FakeOptionSettingsRepository optionSettingsRepository;

  private WaitingManagementService waitingManagementService;

  @BeforeEach
  void setup() {
    waitingRepository = new FakeWaitingRepository();
    waitingHistoryRepository = new FakeWaitingHistoryRepository();
    homeSettingsRepository = new FakeHomeSettingsRepository();
    optionSettingsRepository = new FakeOptionSettingsRepository();

    waitingManagementService = new WaitingManagementService(
        waitingRepository, waitingHistoryRepository, homeSettingsRepository,
        optionSettingsRepository
    );
  }

  @Test
  @DisplayName("웨이팅을 복귀할 수 있다.")
  void undo() {
    String shopId = "TEST_SHOP";
    LocalDate operationDate = LocalDate.of(2023, 2, 25);

    ZonedDateTime nowDateTime = ZonedDateTimeUtils.nowOfSeoul();
    ZonedDateTime homeSettingsRegDateTime = nowDateTime.minusMinutes(20);
    ZonedDateTime waitingRegDateTime = nowDateTime.minusMinutes(15);
    ZonedDateTime waitingCompleteDateTime = nowDateTime.minusMinutes(10);

    WaitingEntity savedEntity = waitingRepository.save(
        createSittingWaitingWithRegDateTime(shopId, operationDate, waitingCompleteDateTime, waitingRegDateTime));

    homeSettingsRepository.save(
        createDefaultHomeSettingsWithRegDateTime(shopId, homeSettingsRegDateTime));
    optionSettingsRepository.save(
        createDefaultOptionSettingsWithRegDateTime(shopId, homeSettingsRegDateTime));

    WaitingHistoryEntity undo = waitingManagementService.undo(shopId,
        savedEntity.getWaitingId(), operationDate);

    WaitingStatus waitingStatus = undo.getWaitingStatus();
    WaitingDetailStatus waitingDetailStatus = undo.getWaitingDetailStatus();

    assertEquals(WaitingStatus.WAITING, waitingStatus);
    assertEquals(WaitingDetailStatus.UNDO, waitingDetailStatus);
    assertEquals("되돌리기", undo.getRemark());

  }

  @Test
  @DisplayName("같은 매장의 웨이팅이 아니라면 복귀할 수 없다.")
  void undo_not_same_shopId() {
    String shopId = "TEST_SHOP";
    String anotherShopId = "ANOTHER_SHOP";
    LocalDate operationDate = LocalDate.of(2023, 2, 25);

    WaitingEntity savedEntity = waitingRepository.save(createWaiting(shopId, operationDate));

    assertThrows(AppException.class,
        () -> waitingManagementService.undo(anotherShopId, savedEntity.getWaitingId(),
            operationDate));
  }

  @Test
  @DisplayName("웨이팅이 웨이팅 중 상태라면 아니라면 복귀할 수 없다.")
  void undo_status_waiting() {
    String shopId = "TEST_SHOP";
    LocalDate operationDate = LocalDate.of(2023, 2, 25);

    WaitingEntity savedEntity = waitingRepository.save(
        createWaiting(shopId, operationDate, WaitingStatus.WAITING, WaitingDetailStatus.WAITING));

    assertThrows(AppException.class,
        () -> waitingManagementService.undo(shopId, savedEntity.getWaitingId(), operationDate));

  }

  @Test
  @DisplayName("웨이팅 복귀 유효시간이 지나면 복귀할 수 없다.")
  void undo_duration_time() {
    String shopId = "TEST_SHOP";
    LocalDate operationDate = LocalDate.of(2023, 2, 25);

    ZonedDateTime waitingCompleteDateTime = ZonedDateTimeUtils.nowOfSeoul().minusMinutes(40);
    WaitingEntity savedEntity = waitingRepository.save(
        createSittingWaiting(shopId, operationDate, waitingCompleteDateTime));

    assertThrows(AppException.class,
        () -> waitingManagementService.undo(shopId, savedEntity.getWaitingId(), operationDate));

  }


  @Test
  @DisplayName("같은 매장에 이미 웨이팅중인 웨이팅이 있다면 복귀할 수 없다.")
  void undo_fail_same_shop_waiting() {
    String shopId = "TEST_SHOP";
    LocalDate operationDate = LocalDate.of(2023, 2, 25);

    ZonedDateTime waitingCompleteDateTime = ZonedDateTimeUtils.nowOfSeoul().minusMinutes(20);
    WaitingEntity savedEntity = waitingRepository.save(
        createSittingWaiting(shopId, operationDate, waitingCompleteDateTime));
    waitingRepository.save(createWaiting(shopId, operationDate));

    assertThrows(AppException.class,
        () -> waitingManagementService.undo(shopId, savedEntity.getWaitingId(), operationDate));

  }


  @Test
  @DisplayName("이미 같은 번호로 웨이팅 중인 웨이팅이 3개를 등록했다면 복귀할 수 없다.")
  void undo_fail_waiting_registered_3_time() {
    String shopId = "TEST_SHOP";
    LocalDate operationDate = LocalDate.of(2023, 2, 25);

    ZonedDateTime waitingCompleteDateTime = ZonedDateTimeUtils.nowOfSeoul().minusMinutes(20);
    WaitingEntity savedEntity = waitingRepository.save(
        createSittingWaiting(shopId, operationDate, waitingCompleteDateTime));
    waitingRepository.save(createWaiting("1", operationDate));
    waitingRepository.save(createWaiting("2", operationDate));
    waitingRepository.save(createWaiting("3", operationDate));

    assertThrows(AppException.class,
        () -> waitingManagementService.undo(shopId, savedEntity.getWaitingId(), operationDate));

  }

  @Test
  @DisplayName("영업일이 다른 웨이팅은 복귀할 수 없다.")
  void undo_not_same_operationDate() {
    String shopId = "TEST_SHOP";
    LocalDate operationDate = LocalDate.of(2023, 2, 25);

    WaitingEntity savedEntity = waitingRepository.save(
        createWaiting(shopId, LocalDate.now().minusDays(1)));

    assertThrows(AppException.class,
        () -> waitingManagementService.undo(shopId, savedEntity.getWaitingId(), operationDate));

  }

  @Test
  @DisplayName("웨이팅 등록 이후에 홈 설정이 변경되었다면 복귀할 수 없다.")
  void checkHomeSettingsModified() {
    // given
    String shopId = "TEST_SHOP";
    LocalDate operationDate = LocalDate.of(2023, 2, 25);

    ZonedDateTime nowDateTime = ZonedDateTimeUtils.nowOfSeoul();
    ZonedDateTime waitingRegDateTime = nowDateTime.minusMinutes(15);
    ZonedDateTime waitingCompleteDateTime = nowDateTime.minusMinutes(10);

    ZonedDateTime homeSettingsRegDateTime = nowDateTime.minusMinutes(5);

    WaitingEntity savedEntity = waitingRepository.save(
        createSittingWaitingWithRegDateTime(shopId, operationDate, waitingCompleteDateTime, waitingRegDateTime));

    homeSettingsRepository.save(
        createDefaultHomeSettingsWithRegDateTime(shopId, homeSettingsRegDateTime));
    optionSettingsRepository.save(
        createDefaultOptionSettingsWithRegDateTime(shopId, homeSettingsRegDateTime));

    // when // then
    AppException appException = assertThrows(AppException.class,
        () -> waitingManagementService.undo(shopId, savedEntity.getWaitingId(), operationDate));
    assertThat(appException.getDisplayMessage()).isEqualTo(
        ErrorCode.CANNOT_UNDO_CAUSE_SEAT_OPTIONS_MODIFIED.getMessage());
  }

}