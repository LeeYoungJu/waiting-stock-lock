package co.wadcorp.waiting.data.service.waiting;

import static co.wadcorp.waiting.data.domain.waiting.fixture.WaitingFixture.createWaiting;
import static org.junit.jupiter.api.Assertions.*;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.data.domain.waiting.FakeWaitingHistoryRepository;
import co.wadcorp.waiting.data.domain.waiting.FakeWaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.exception.AppException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WaitingServiceTest {

  private FakeWaitingRepository fakeWaitingRepository;
  private FakeWaitingHistoryRepository fakeWaitingHistoryRepository;
  private WaitingService waitingService;

  @BeforeEach
  void setup() {
    fakeWaitingRepository = new FakeWaitingRepository();
    fakeWaitingHistoryRepository = new FakeWaitingHistoryRepository();
    waitingService = new WaitingService(fakeWaitingRepository, fakeWaitingHistoryRepository);
  }


  @DisplayName("존재하지 않는 웨이팅 시퀀스로 조회할 수 없다.")
  @Test
  void getWaitingBySeq() {
    String shopId = "SHOP_ID";
    LocalDate operationDate = LocalDate.of(2023, 2, 25);

    WaitingEntity saved = fakeWaitingRepository.save(createWaiting(shopId, operationDate));

    WaitingEntity target = waitingService.findByWaitingId(saved.getWaitingId());

    assertEquals(saved, target);
    assertThrows(AppException.class, () -> waitingService.findByWaitingId(UUIDUtil.shortUUID()));
  }

  @DisplayName("매장에 웨이팅 중인 팀이 있으면 설정을 업데이트 할 수 없다.")
  @Test
  void validWaitingTeamExists() {
    String shopId = "SHOP_ID";
    LocalDate operationDate = LocalDate.of(2023, 2, 25);

    fakeWaitingRepository.save(createWaiting(shopId, operationDate));

    assertThrows(AppException.class, () -> waitingService.validWaitingTeamExists(shopId, operationDate));
  }

  @DisplayName("웨이팅 상태를 고객취소 상태로 변경할 수 있다.")
  @Test
  void cancelByCustomer() {
    String shopId = "SHOP_ID";
    LocalDate operationDate = LocalDate.of(2023, 2, 25);

    WaitingEntity saved = fakeWaitingRepository.save(createWaiting(shopId, operationDate));
    WaitingEntity canceledWaiting = waitingService.cancelByCustomer(saved.getWaitingId());

    assertEquals(WaitingStatus.CANCEL, canceledWaiting.getWaitingStatus());
    assertEquals(WaitingDetailStatus.CANCEL_BY_CUSTOMER, canceledWaiting.getWaitingDetailStatus());
  }

  @DisplayName("이미 취소 상태인 웨이팅을 취소할 수 없다.")
  @Test
  void cancelAlreadyCanceledWaiting() {
    String shopId = "SHOP_ID";
    LocalDate operationDate = LocalDate.of(2023, 2, 25);

    WaitingEntity saved = fakeWaitingRepository.save(createWaiting(shopId, operationDate,
        WaitingStatus.CANCEL, WaitingDetailStatus.CANCEL_BY_CUSTOMER));

    assertThrows(AppException.class, () -> waitingService.cancelByCustomer(saved.getWaitingId()));
  }
}