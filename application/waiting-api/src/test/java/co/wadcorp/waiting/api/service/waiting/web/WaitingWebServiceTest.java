package co.wadcorp.waiting.api.service.waiting.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.controller.waiting.web.dto.response.WaitingWebResponse;
import co.wadcorp.waiting.api.model.waiting.vo.WebPersonOptionVO;
import co.wadcorp.waiting.data.config.WaitingNumberConstructor;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import co.wadcorp.waiting.data.domain.shop.ShopEntity;
import co.wadcorp.waiting.data.domain.shop.ShopRepository;
import co.wadcorp.waiting.data.domain.waiting.PersonOption;
import co.wadcorp.waiting.data.domain.waiting.PersonOption.AdditionalOption;
import co.wadcorp.waiting.data.domain.waiting.PersonOptionsData;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.domain.waiting.WebProgressMessage;
import co.wadcorp.waiting.data.domain.settings.putoff.DisablePutOffEntity;
import co.wadcorp.waiting.data.domain.settings.putoff.DisablePutOffRepository;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class WaitingWebServiceTest extends IntegrationTest {

  @Autowired
  private WaitingRepository waitingRepository;

  @Autowired
  private ShopRepository shopRepository;

  @Autowired
  private HomeSettingsRepository homeSettingsRepository;

  @Autowired
  private DisablePutOffRepository disablePutOffRepository;

  @Autowired
  private WaitingWebService waitingWebService;

  @DisplayName("웨이팅 id로 웹에서 실시간 현황 정보를 조회한다.")
  @Test
  void getWaitingInfo() {
    // given
    String shopId = "123";
    long putOffCount = 2L;

    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
    int expectedWaitingPeriod = 5;

    PersonOptionsData personOptionsData = PersonOptionsData.builder()
        .personOptions(
            List.of(PersonOption.builder()
                .name("유아")
                .count(1)
                .additionalOptions(
                    List.of(AdditionalOption.builder()
                        .name("유아용의자")
                        .count(0)
                        .build()
                    )
                )
                .build()
            )
        )
        .build();

    String shopTelNumber = "010-2499-1180";
    String shopAddress = "서울특별시";
    String shopName = "매장명";

    shopRepository.save(ShopEntity.builder()
        .shopId(shopId)
        .shopName(shopName)
        .shopAddress(shopAddress)
        .shopTelNumber(shopTelNumber)
        .isUsedRemoteWaiting(true)
        .build());

    WaitingEntity waiting = createWaiting(
        shopId, operationDate, WaitingStatus.WAITING, WaitingDetailStatus.WAITING,
        1, personOptionsData
    );
    waitingRepository.save(waiting);

    HomeSettingsEntity homeSettings = createHomeSettings(shopId, expectedWaitingPeriod);
    homeSettingsRepository.save(homeSettings);

    // when
    WaitingWebResponse result = waitingWebService.getWaitingInfo(
        waiting.getWaitingId(), operationDate
    );

    // then
    assertThat(result)
        .extracting(
            "shopName", "shopAddress", "shopTelNumber",
            "waitingNumber", "waitingOrder", "expectedWaitingPeriod",
            "seatOptionName", "canPutOffCount", "message", "disablePutOff"
        )
        .containsExactly(
            shopName, shopAddress, shopTelNumber,
            waiting.getWaitingNumber(), waiting.getWaitingOrder(), expectedWaitingPeriod,
            waiting.getSeatOptionName(), putOffCount,
            WebProgressMessage.getMessage(waiting.getWaitingDetailStatus()), false
        );

    List<WebPersonOptionVO> personOptions = result.getPersonOptions();
    assertThat(personOptions)
        .extracting("name", "count")
        .containsExactly(
            tuple("유아", 1)
        );
    assertThat(personOptions.get(0).getAdditionalOptions())
        .extracting("name", "count")
        .containsExactly(
            tuple("유아용의자", 0)
        );
  }

  @DisplayName("웨이팅 id로 웹에서 실시간 현황 정보를 조회할 때, 미루기 off한 매장 여부 정보도 같이 응답한다.")
  @Test
  void getWaitingInfoWithDisablePutOff() {
    // given
    String shopId = "123";
    long putOffCount = 2L;

    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
    int expectedWaitingPeriod = 5;

    PersonOptionsData personOptionsData = PersonOptionsData.builder()
        .personOptions(
            List.of(PersonOption.builder()
                .name("유아")
                .count(1)
                .additionalOptions(
                    List.of(AdditionalOption.builder()
                        .name("유아용의자")
                        .count(0)
                        .build()
                    )
                )
                .build()
            )
        )
        .build();

    String shopTelNumber = "010-2499-1180";
    String shopAddress = "서울특별시";
    String shopName = "매장명";

    shopRepository.save(ShopEntity.builder()
        .shopId(shopId)
        .shopName(shopName)
        .shopAddress(shopAddress)
        .shopTelNumber(shopTelNumber)
        .isUsedRemoteWaiting(true)
        .build());

    WaitingEntity waiting = createWaiting(
        shopId, operationDate, WaitingStatus.WAITING, WaitingDetailStatus.WAITING,
        1, personOptionsData
    );
    waitingRepository.save(waiting);

    HomeSettingsEntity homeSettings = createHomeSettings(shopId, expectedWaitingPeriod);
    homeSettingsRepository.save(homeSettings);

    createDisablePutOff(shopId);

    // when
    WaitingWebResponse result = waitingWebService.getWaitingInfo(
        waiting.getWaitingId(), operationDate
    );

    // then
    assertThat(result)
        .extracting("shopName", "shopAddress", "shopTelNumber", "disablePutOff")
        .containsExactly(shopName, shopAddress, shopTelNumber, true);
  }

  private WaitingEntity createWaiting(
      String shopId, LocalDate operationDate, WaitingStatus waitingStatus,
      WaitingDetailStatus waitingDetailStatus, int waitingOrder, PersonOptionsData personOptionsData
  ) {
    return WaitingEntity.builder()
        .shopId(shopId)
        .waitingId(UUIDUtil.shortUUID().toUpperCase())
        .operationDate(operationDate)
        .waitingStatus(waitingStatus)
        .waitingDetailStatus(waitingDetailStatus)
        .seatOptionName("홀")
        .customerSeq(1L)
        .waitingCompleteDateTime(null)
        .waitingNumbers(
            WaitingNumber.builder()
                .waitingNumber(WaitingNumberConstructor.initWaitingNumber())
                .waitingOrder(waitingOrder)
                .build()
        )
        .personOptionsData(personOptionsData)
        .build();
  }

  private HomeSettingsEntity createHomeSettings(String shopId, int defaultExpectedWaitingPeriod) {
    return HomeSettingsEntity.builder()
        .shopId(shopId)
        .homeSettingsData(
            HomeSettingsData.builder()
                .waitingModeType("DEFAULT")
                .defaultModeSettings(
                    SeatOptions.builder()
                        .expectedWaitingPeriod(defaultExpectedWaitingPeriod)
                        .isUsedExpectedWaitingPeriod(true)
                        .build()
                )
                .build()
        )
        .build();
  }

  private DisablePutOffEntity createDisablePutOff(String shopId) {
    DisablePutOffEntity disablePutOff = DisablePutOffEntity.builder()
        .shopId(shopId)
        .build();
    return disablePutOffRepository.save(disablePutOff);
  }

}