package co.wadcorp.waiting.data.service.waiting;

import static co.wadcorp.waiting.data.domain.settings.fixture.HomeSettingsFixture.createDefaultHomeSettingsWithRegDateTime;
import static co.wadcorp.waiting.data.domain.settings.fixture.OptionSettingsFixture.createDefaultOptionSettingsWithRegDateTime;
import static co.wadcorp.waiting.data.domain.waiting.fixture.WaitingFixture.createSittingWaitingWithRegDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.wadcorp.waiting.data.domain.settings.FakeHomeSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.FakeOptionSettingsRepository;
import co.wadcorp.waiting.data.domain.waiting.FakeWaitingHistoryRepository;
import co.wadcorp.waiting.data.domain.waiting.FakeWaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WaitingChangeStatusServiceTest {

  private FakeWaitingRepository waitingRepository;
  private FakeWaitingHistoryRepository waitingHistoryRepository;
  private FakeHomeSettingsRepository homeSettingsRepository;
  private FakeOptionSettingsRepository optionSettingsRepository;

  private WaitingChangeStatusService waitingChangeStatusService;

  @BeforeEach
  void setup() {
    waitingRepository = new FakeWaitingRepository();
    waitingHistoryRepository = new FakeWaitingHistoryRepository();
    homeSettingsRepository = new FakeHomeSettingsRepository();
    optionSettingsRepository = new FakeOptionSettingsRepository();

    waitingChangeStatusService = new WaitingChangeStatusService(
        waitingRepository, waitingHistoryRepository, homeSettingsRepository,
        optionSettingsRepository
    );
  }

  @DisplayName("웨이팅 등록 이후에 홈 설정이 변경되었다면 복귀할 수 없다.")
  @Test
  void checkHomeSettingsModified() {
    // given
    String shopId = "TEST_SHOP";
    LocalDate operationDate = LocalDate.of(2023, 2, 25);

    ZonedDateTime nowDateTime = ZonedDateTimeUtils.nowOfSeoul();
    ZonedDateTime waitingRegDateTime = nowDateTime.minusMinutes(15);
    ZonedDateTime waitingCompleteDateTime = nowDateTime.minusMinutes(10);

    ZonedDateTime homeSettingsRegDateTime = nowDateTime.minusMinutes(5);

    WaitingEntity savedEntity = waitingRepository.save(
        createSittingWaitingWithRegDateTime(shopId, operationDate, waitingCompleteDateTime,
            waitingRegDateTime));

    homeSettingsRepository.save(
        createDefaultHomeSettingsWithRegDateTime(shopId, homeSettingsRegDateTime));
    optionSettingsRepository.save(
        createDefaultOptionSettingsWithRegDateTime(shopId, homeSettingsRegDateTime));

    // when // then
    AppException appException = assertThrows(AppException.class,
        () -> waitingChangeStatusService.undoByCustomer(savedEntity.getWaitingId(), operationDate));
    assertThat(appException.getDisplayMessage()).isEqualTo(
        ErrorCode.CANNOT_UNDO_CAUSE_SEAT_OPTIONS_MODIFIED.getMessage());
  }

}