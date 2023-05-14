package co.wadcorp.waiting.api.internal.service.shop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.internal.service.shop.dto.request.RemoteShopOperationServiceRequest;
import co.wadcorp.waiting.api.internal.service.shop.dto.response.RemoteShopOperationResponse;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.data.domain.settings.AlarmSettingsData;
import co.wadcorp.waiting.data.domain.settings.AlarmSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.AlarmSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsData;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsData;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsRepository;
import co.wadcorp.waiting.data.domain.shop.ShopEntity;
import co.wadcorp.waiting.data.domain.shop.ShopRepository;
import co.wadcorp.waiting.data.domain.shop.operation.status.RegistrableStatus;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoRepository;
import co.wadcorp.waiting.data.domain.settings.putoff.DisablePutOffEntity;
import co.wadcorp.waiting.data.domain.settings.putoff.DisablePutOffRepository;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RemoteShopApiServiceTest extends IntegrationTest {

  @Autowired
  private RemoteShopApiService remoteShopApiService;

  @Autowired
  private ShopOperationInfoRepository shopOperationInfoRepository;

  @Autowired
  private ShopRepository shopRepository;

  @Autowired
  private AlarmSettingsRepository alarmSettingsRepository;

  @Autowired
  private PrecautionSettingsRepository precautionSettingsRepository;

  @Autowired
  private DisablePutOffRepository disablePutOffRepository;

  @Autowired
  private OrderSettingsRepository orderSettingsRepository;

  @DisplayName("미루기 off 정보를 매장 운영 정보 전달 시 같이 전달한다.")
  @Test
  void disablePutOff() {
    // given
    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";
    String shopId3 = "shopId-3";
    LocalDate operationDate = LocalDate.of(2023, 4, 21);

    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put(shopId1, "1");
    channelShopIdMapping.put(shopId2, "2");
    channelShopIdMapping.put(shopId3, "3");

    createShopOperationInfo(operationDate, shopId1);
    createShopOperationInfo(operationDate, shopId2);
    createShopOperationInfo(operationDate, shopId3);
    createShop(shopId1);
    createShop(shopId2);
    createShop(shopId3);
    createAlarmSettings(shopId1);
    createAlarmSettings(shopId2);
    createAlarmSettings(shopId3);
    createPrecautionSettings(shopId1);
    createPrecautionSettings(shopId2);
    createPrecautionSettings(shopId3);

    // shopId1은 ON, shopId2는 OFF, shopId3은 설정 없는 경우
    createDisablePutOff(shopId1);
    DisablePutOffEntity disablePutOff2 = createDisablePutOff(shopId2);
    disablePutOff2.unPublish();
    disablePutOffRepository.save(disablePutOff2);

    RemoteShopOperationServiceRequest request = RemoteShopOperationServiceRequest.builder()
        .operationDate(operationDate)
        .build();

    // when
    List<RemoteShopOperationResponse> results = remoteShopApiService.findShopOperations(
        channelShopIdMapping, request,
        ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 4, 21, 10, 0))
    );

    // then
    assertThat(results).hasSize(3)
        .extracting("shopId", "disablePutOff")
        .containsExactlyInAnyOrder(
            tuple(1L, true),
            tuple(2L, false),
            tuple(3L, false)
        );
  }

  @DisplayName("원격 매장정보 조회 시 주문설정값도 포함된다.")
  @Test
  void checkIsPossibleOrderInShopInfo() {
    // given
    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";
    LocalDate operationDate = LocalDate.of(2023, 5, 12);

    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put(shopId1, "1");
    channelShopIdMapping.put(shopId2, "2");

    createShopOperationInfo(operationDate, shopId1);
    createShopOperationInfo(operationDate, shopId2);
    createShop(shopId1);
    createShop(shopId2);
    createAlarmSettings(shopId1);
    createAlarmSettings(shopId2);
    createPrecautionSettings(shopId1);
    createPrecautionSettings(shopId2);
    createOrderSettings(shopId1, true);   // shop1은 선주문 On
    createOrderSettings(shopId2, false);  // shop2은 선주문 Off

    createDisablePutOff(shopId1);
    DisablePutOffEntity disablePutOff2 = createDisablePutOff(shopId2);
    disablePutOff2.unPublish();
    disablePutOffRepository.save(disablePutOff2);

    RemoteShopOperationServiceRequest request = RemoteShopOperationServiceRequest.builder()
        .operationDate(operationDate)
        .build();

    // when
    List<RemoteShopOperationResponse> results = remoteShopApiService.findShopOperations(
        channelShopIdMapping, request,
        ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 5, 12, 10, 0))
    );

    // then
    assertThat(results).hasSize(2)
        .extracting("isPossibleOrder")
        .containsExactlyInAnyOrder(true, false);
  }

  private ShopOperationInfoEntity createShopOperationInfo(LocalDate operationDate, String shopId) {
    ShopOperationInfoEntity shopOperationInfo = ShopOperationInfoEntity.builder()
        .shopId(shopId)
        .operationDate(operationDate)
        .registrableStatus(RegistrableStatus.OPEN)
        .operationStartDateTime(ZonedDateTimeUtils.ofSeoul(operationDate, LocalTime.of(10, 0)))
        .operationEndDateTime(ZonedDateTimeUtils.ofSeoul(operationDate, LocalTime.of(22, 0)))
        .build();
    return shopOperationInfoRepository.save(shopOperationInfo);
  }

  private ShopEntity createShop(String shopId) {
    ShopEntity shop = ShopEntity.builder()
        .shopId(shopId)
        .shopName("shopName")
        .shopAddress("shopAddress")
        .shopTelNumber("shopTelNumber")
        .build();
    return shopRepository.save(shop);
  }

  private AlarmSettingsEntity createAlarmSettings(String shopId) {
    AlarmSettingsEntity alarmSettings = AlarmSettingsEntity.builder()
        .shopId(shopId)
        .alarmSettingsData(AlarmSettingsData.of(3, false, 1))
        .build();
    return alarmSettingsRepository.save(alarmSettings);
  }

  private PrecautionSettingsEntity createPrecautionSettings(String shopId) {
    PrecautionSettingsEntity precautionSettings = PrecautionSettingsEntity.builder()
        .shopId(shopId)
        .precautionSettingsData(PrecautionSettingsData.builder()
            .precautions(List.of())
            .isUsedPrecautions(false)
            .messagePrecaution("")
            .build()
        )
        .build();
    return precautionSettingsRepository.save(precautionSettings);
  }

  private DisablePutOffEntity createDisablePutOff(String shopId) {
    DisablePutOffEntity disablePutOff = DisablePutOffEntity.builder()
        .shopId(shopId)
        .build();
    return disablePutOffRepository.save(disablePutOff);
  }

  private OrderSettingsEntity createOrderSettings(String shopId, boolean isPossibleOrder) {
    OrderSettingsEntity orderSettingsEntity = OrderSettingsEntity.builder()
        .shopId(shopId)
        .orderSettingsData(OrderSettingsData.builder()
            .isPossibleOrder(isPossibleOrder)
            .build()
        )
        .build();
    return orderSettingsRepository.save(orderSettingsEntity);
  }

}