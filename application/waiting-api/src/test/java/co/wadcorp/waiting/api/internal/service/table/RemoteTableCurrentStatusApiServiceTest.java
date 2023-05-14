package co.wadcorp.waiting.api.internal.service.table;

import static co.wadcorp.waiting.data.domain.waiting.WaitingStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.internal.service.table.dto.response.RemoteTableStatusResponse;
import co.wadcorp.waiting.api.internal.service.table.dto.response.RemoteTableStatusResponse.TableCurrentStatusVO;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.data.config.WaitingNumberConstructor;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import co.wadcorp.waiting.data.domain.waiting.PersonOptionsData;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RemoteTableCurrentStatusApiServiceTest extends IntegrationTest {

  @Autowired
  private HomeSettingsRepository homeSettingsRepository;

  @Autowired
  private WaitingRepository waitingRepository;

  @Autowired
  private RemoteTableCurrentStatusApiService remoteTableCurrentStatusApiService;

  @DisplayName("현재 웨이팅이 걸려있는 기본모드 테이블 현황 정보를 조회한다.")
  @Test
  void findDefaultTableCurrentStatus() {
    LocalDate operationDate = LocalDate.of(2023, 3, 9);
    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";

    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping(
        Map.of(shopId1, "1", shopId2, "2"));

    createHomeSettings(shopId1,
        createSeatOptions("홀", 10, true, true),
        List.of(
            createSeatOptions("홀", 10, true, true),
            createSeatOptions("바", 20, true, true)
        ),
        "DEFAULT"
    );
    createHomeSettings(shopId2,
        createSeatOptions("홀2", 20, true, true),
        List.of(
            createSeatOptions("홀2", 30, true, true),
            createSeatOptions("바2", 40, true, true)
        ),
        "DEFAULT"
    );

    createWaiting(shopId1, operationDate, WAITING, WaitingDetailStatus.WAITING, 1, "홀");
    createWaiting(shopId2, operationDate, WAITING, WaitingDetailStatus.WAITING, 2, "홀2");

    List<RemoteTableStatusResponse> results = remoteTableCurrentStatusApiService.findTableCurrentStatus(
        channelShopIdMapping, operationDate
    );

    assertThat(results).hasSize(2)
        .extracting("shopId", "totalTeamCount")
        .containsExactlyInAnyOrder(
            tuple(1L, 1),
            tuple(2L, 1)
        );

    List<TableCurrentStatusVO> statuses = results.stream()
        .flatMap(i -> i.getTableCurrentStatus().stream())
        .toList();
    assertThat(statuses).hasSize(2)
        .extracting("tableName", "teamCount", "expectedWaitingTime", "isUsedExpectedWaitingPeriod",
            "isTakeOut"
        )
        .containsExactlyInAnyOrder(
            tuple("홀", 1, 20, true, true),
            tuple("홀2", 1, 40, true, true)
        );
  }

  @DisplayName("현재 웨이팅이 걸려있는 테이블모드 테이블 현황 정보를 조회한다.")
  @Test
  void findTableCurrentStatus() {
    LocalDate operationDate = LocalDate.of(2023, 3, 9);
    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";

    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping(
        Map.of(shopId1, "1", shopId2, "2"));

    createHomeSettings(shopId1, null, List.of(
        createSeatOptions("홀", 10, true, true),
        createSeatOptions("바", 20, true, true)
    ), "TABLE");
    createHomeSettings(shopId2, null, List.of(
        createSeatOptions("홀2", 30, true, true),
        createSeatOptions("바2", 40, true, true)
    ), "TABLE");

    createWaiting(shopId1, operationDate, WAITING, WaitingDetailStatus.WAITING, 1, "홀");
    createWaiting(shopId1, operationDate, WAITING, WaitingDetailStatus.WAITING, 2, "바");
    createWaiting(shopId1, operationDate, WAITING, WaitingDetailStatus.WAITING, 3, "바");
    createWaiting(shopId2, operationDate, WAITING, WaitingDetailStatus.WAITING, 4, "홀2");
    createWaiting(shopId2, operationDate, WAITING, WaitingDetailStatus.WAITING, 5, "바2");

    List<RemoteTableStatusResponse> results = remoteTableCurrentStatusApiService.findTableCurrentStatus(
        channelShopIdMapping, operationDate
    );

    assertThat(results).hasSize(2)
        .extracting("shopId", "totalTeamCount")
        .containsExactlyInAnyOrder(
            tuple(1L, 3),
            tuple(2L, 2)
        );

    List<TableCurrentStatusVO> statuses = results.stream()
        .flatMap(i -> i.getTableCurrentStatus().stream())
        .toList();
    assertThat(statuses).hasSize(4)
        .extracting("tableName", "teamCount", "expectedWaitingTime", "isUsedExpectedWaitingPeriod",
            "isTakeOut"
        )
        .containsExactlyInAnyOrder(
            tuple("홀", 1, 20, true, true),
            tuple("바", 2, 60, true, true),
            tuple("홀2", 1, 60, true, true),
            tuple("바2", 1, 80, true, true)
        );
  }

  private HomeSettingsEntity createHomeSettings(String shopId, SeatOptions defaultModeSettings,
      List<SeatOptions> tableModeSettings, String waitingModeType) {
    HomeSettingsEntity homeSettings = HomeSettingsEntity.builder()
        .shopId(shopId)
        .homeSettingsData(HomeSettingsData.builder()
            .defaultModeSettings(defaultModeSettings)
            .tableModeSettings(tableModeSettings)
            .waitingModeType(waitingModeType)
            .build()
        )
        .build();
    return homeSettingsRepository.save(homeSettings);
  }

  private SeatOptions createSeatOptions(String name, int expectedWaitingPeriod,
      boolean isUsedExpectedWaitingPeriod, boolean isTakeOut) {
    return SeatOptions.builder()
        .name(name)
        .expectedWaitingPeriod(expectedWaitingPeriod)
        .isUsedExpectedWaitingPeriod(isUsedExpectedWaitingPeriod)
        .isTakeOut(isTakeOut)
        .build();
  }

  private WaitingEntity createWaiting(
      String shopId, LocalDate operationDate, WaitingStatus waitingStatus,
      WaitingDetailStatus waitingDetailStatus, int waitingOrder, String seatOptionName
  ) {
    WaitingEntity waiting = WaitingEntity.builder()
        .shopId(shopId)
        .waitingId(UUIDUtil.shortUUID().toUpperCase())
        .operationDate(operationDate)
        .waitingStatus(waitingStatus)
        .waitingDetailStatus(waitingDetailStatus)
        .seatOptionName(seatOptionName)
        .customerSeq(1L)
        .waitingCompleteDateTime(null)
        .waitingNumbers(
            WaitingNumber.builder()
                .waitingNumber(WaitingNumberConstructor.initWaitingNumber())
                .waitingOrder(waitingOrder)
                .build()
        )
        .personOptionsData(PersonOptionsData.builder().build())
        .build();
    return waitingRepository.save(waiting);
  }

}