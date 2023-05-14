package co.wadcorp.waiting.api.service.waiting.management;

import static org.assertj.core.api.Assertions.assertThat;

import co.wadcorp.libs.datetime.ISO8601;
import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementWaitingCurrentStatusResponse;
import co.wadcorp.waiting.data.domain.settings.DefaultHomeSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsRepository;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoRepository;
import co.wadcorp.waiting.data.domain.shop.operation.status.OperationStatus;
import co.wadcorp.waiting.data.domain.shop.operation.status.RegistrableStatus;
import co.wadcorp.waiting.data.domain.waiting.PersonOptionsData;
import co.wadcorp.waiting.data.domain.waiting.RegisterChannel;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ManagementWaitingCurrentStatusApiServiceTest extends IntegrationTest {

  @Autowired
  private HomeSettingsRepository homeSettingsRepository;

  @Autowired
  private ShopOperationInfoRepository shopOperationInfoRepository;

  @Autowired
  private WaitingRepository waitingRepository;

  @Autowired
  private ManagementWaitingCurrentStatusApiService managementWaitingCurrentStatusApiService;

  @Test
  void getCurrentStatus() {
    // given
    String SHOP_ID = "SHOP_ID";
    LocalDate operationDate = LocalDate.of(2023, 4, 17);
    ZonedDateTime nowDateTime = ZonedDateTime.of(operationDate, LocalTime.of(13, 0, 0),
        ZoneId.of("Asia/Seoul"));

    ZonedDateTime operationStartDateTime = ZonedDateTime.of(operationDate, LocalTime.of(12, 0, 0),
        ZoneId.of("Asia/Seoul"));
    ZonedDateTime operationEndDateTime = ZonedDateTime.of(operationDate, LocalTime.of(14, 0, 0),
        ZoneId.of("Asia/Seoul"));

    homeSettingsRepository.save(
        new HomeSettingsEntity(SHOP_ID, DefaultHomeSettingDataFactory.create())
    );
    shopOperationInfoRepository.save(
        createShopOpeartionInfo(SHOP_ID, operationDate, operationStartDateTime, operationEndDateTime)
    );

    waitingRepository.save(
        createWaiting(SHOP_ID, operationDate));

    // when
    ManagementWaitingCurrentStatusResponse currentStatus = managementWaitingCurrentStatusApiService.getCurrentStatus(
        SHOP_ID, operationDate, nowDateTime);

    // then
    assertThat(currentStatus.getCurrentStatus())
        .extracting("teamCount", "peopleCount")
        .containsExactly(1, 2);

    assertThat(currentStatus.getCurrentStatus().getSeatsCurrentStatuses())
        .hasSize(1)
        .extracting("teamCount", "peopleCount")
        .containsExactly(Tuple.tuple(1, 2));

    assertThat(currentStatus.getOperationInfo())
        .extracting("operationDate", "operationStatus", "operationStartDateTime",
            "operationEndDateTime")
        .containsExactly(operationDate.toString(), OperationStatus.OPEN,
            ISO8601.format(operationStartDateTime), ISO8601.format(operationEndDateTime));

  }

  private static WaitingEntity createWaiting(String SHOP_ID, LocalDate operationDate) {
    return WaitingEntity.builder()
        .shopId(SHOP_ID)
        .waitingId(UUIDUtil.shortUUID())
        .operationDate(operationDate)
        .registerChannel(RegisterChannel.WAITING_APP)
        .waitingNumbers(WaitingNumber.builder()
            .waitingNumber(101)
            .waitingOrder(1)
            .build())
        .waitingStatus(WaitingStatus.WAITING)
        .waitingDetailStatus(WaitingDetailStatus.CALL)
        .seatOptionName("착석")
        .customerSeq(1L)
        .totalPersonCount(2)
        .personOptionsData(PersonOptionsData.builder()
            .personOptions(List.of())
            .build())
        .build();
  }

  private static ShopOperationInfoEntity createShopOpeartionInfo(
      String SHOP_ID, LocalDate operationDate,
      ZonedDateTime operationStartDateTime, ZonedDateTime operationEndDateTime
  ) {
    return ShopOperationInfoEntity.builder()
        .shopId(SHOP_ID)
        .operationDate(operationDate)
        .operationStartDateTime(operationStartDateTime)
        .operationEndDateTime(operationEndDateTime)
        .registrableStatus(RegistrableStatus.OPEN)
        .build();
  }
}