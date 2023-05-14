package co.wadcorp.waiting.data.service.waiting;

import static co.wadcorp.waiting.data.domain.waiting.fixture.WaitingFixture.createWaiting;
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
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WaitingManagementServiceCallTest {

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
  @DisplayName("웨이팅을 호출할 수 있다.")
  void call() {
    String shopId = "TEST_SHOP";
    LocalDate operationDate = LocalDate.of(2023, 2, 15);

    WaitingEntity savedEntity = waitingRepository.save(createWaiting(shopId, operationDate));

    WaitingHistoryEntity call = waitingManagementService.call(shopId, savedEntity.getWaitingId(), operationDate);

    WaitingStatus waitingStatus = call.getWaitingStatus();
    WaitingDetailStatus waitingDetailStatus = call.getWaitingDetailStatus();

    assertEquals(WaitingStatus.WAITING, waitingStatus);
    assertEquals(WaitingDetailStatus.CALL, waitingDetailStatus);

  }

  @Test
  @DisplayName("같은 매장의 웨이팅이 아니라면 호출할 수 없다.")
  void call_not_same_shopId() {
    String shopId = "TEST_SHOP";
    String anotherShopId = "ANOTHER_SHOP";
    LocalDate operationDate = LocalDate.of(2023, 2, 15);

    WaitingEntity savedEntity = waitingRepository.save(createWaiting(shopId, operationDate));

    assertThrows(AppException.class, () -> waitingManagementService.call(anotherShopId, savedEntity.getWaitingId(), operationDate));
  }

  @Test
  @DisplayName("웨이팅이 웨이팅 중 상태가 아니라면 호출할 수 없다.")
  void call_not_status_waiting() {
    String shopId = "TEST_SHOP";
    LocalDate operationDate = LocalDate.of(2023, 2, 15);

    WaitingEntity savedEntity = waitingRepository.save(createWaiting(shopId, operationDate, WaitingStatus.SITTING, WaitingDetailStatus.SITTING));

    assertThrows(AppException.class, () -> waitingManagementService.call(shopId, savedEntity.getWaitingId(), operationDate));

  }

  @Test
  @DisplayName("영업일이 다른 웨이팅은 호출할 수 없다.")
  void call_not_same_operationDate() {
    String shopId = "TEST_SHOP";
    LocalDate operationDate = LocalDate.of(2023, 2, 15);

    WaitingEntity savedEntity = waitingRepository.save(createWaiting(shopId, operationDate.minusDays(1)));

    assertThrows(AppException.class, () -> waitingManagementService.call(shopId, savedEntity.getWaitingId(), operationDate));
  }

  @Test
  @DisplayName("하나의 웨이팅에서 호출은 2회를 초과할 수 없다.")
  void call_call_count_over() {
    String shopId = "TEST_SHOP";
    LocalDate operationDate = LocalDate.of(2023, 2, 15);

    WaitingEntity savedEntity = waitingRepository.save(createWaiting(shopId, operationDate, WaitingStatus.WAITING, WaitingDetailStatus.CALL));
    waitingHistoryRepository.save(new WaitingHistoryEntity(savedEntity));
    waitingHistoryRepository.save(new WaitingHistoryEntity(savedEntity));


    assertThrows(AppException.class, () -> waitingManagementService.call(shopId, savedEntity.getWaitingId(), operationDate));
  }
}