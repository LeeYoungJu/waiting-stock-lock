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

class WaitingManagementServiceCancelByShopTest {

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
  @DisplayName("웨이팅을 매장 사정으로 취소할 수 있다.")
  void cancel_shop() {
    String shopId = "TEST_SHOP";
    LocalDate operationDate = LocalDate.of(2023, 2, 15);

    WaitingEntity savedEntity = waitingRepository.save(createWaiting(shopId, operationDate));

    WaitingHistoryEntity cancel = waitingManagementService.cancelByShop(shopId,
        savedEntity.getWaitingId());

    assertEquals(WaitingStatus.CANCEL, cancel.getWaitingStatus());
    assertEquals(WaitingDetailStatus.CANCEL_BY_SHOP, cancel.getWaitingDetailStatus());

  }

  @Test
  @DisplayName("같은 매장의 웨이팅이 아니라면 취소할 수 없다.")
  void cancel_not_same_shopId() {
    String shopId = "TEST_SHOP";
    String anotherShopId = "ANOTHER_SHOP";
    LocalDate operationDate = LocalDate.of(2023, 2, 15);

    WaitingEntity savedEntity = waitingRepository.save(createWaiting(shopId, operationDate));

    assertThrows(AppException.class, () -> waitingManagementService.cancelByShop(anotherShopId, savedEntity.getWaitingId()));
  }

  @Test
  @DisplayName("이미 취소된 웨이팅은 취소할 수 없다.")
  void cancel_already_cancel() {
    String shopId = "TEST_SHOP";
    LocalDate operationDate = LocalDate.of(2023, 2, 15);

    WaitingEntity savedEntity = waitingRepository.save(createWaiting(shopId, operationDate, WaitingStatus.CANCEL, WaitingDetailStatus.CANCEL_BY_SHOP));

    assertThrows(AppException.class, () -> waitingManagementService.cancelByShop(shopId, savedEntity.getWaitingId()));
  }

  @Test
  @DisplayName("웨이팅이 웨이팅 중 상태가 아니라면 취소할 수 없다.")
  void cancel_not_status_waiting() {
    String shopId = "TEST_SHOP";
    LocalDate operationDate = LocalDate.of(2023, 2, 15);

    WaitingEntity savedEntity = waitingRepository.save(createWaiting(shopId, operationDate, WaitingStatus.SITTING, WaitingDetailStatus.SITTING));

    assertThrows(AppException.class, () -> waitingManagementService.cancelByShop(shopId, savedEntity.getWaitingId()));
  }

}