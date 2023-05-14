package co.wadcorp.waiting.api.service.waiting;

import static co.wadcorp.libs.stream.StreamUtils.convert;

import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.waiting.api.model.waiting.response.OtherWaitingListResponse;
import co.wadcorp.waiting.data.domain.settings.DefaultHomeSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.query.settings.HomeSettingsQueryRepository;
import co.wadcorp.waiting.data.query.waiting.WaitingCountQueryRepository;
import co.wadcorp.waiting.data.query.waiting.WaitingRegisterQueryRepository;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingOfOtherShopQueryDto;
import co.wadcorp.waiting.shared.util.PhoneNumberUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Slf4j
@Service
@RequiredArgsConstructor
public class WaitingRegisterOtherWaitingService {

  private final WaitingRegisterQueryRepository waitingRegisterQueryRepository;
  private final WaitingCountQueryRepository waitingCountQueryRepository;
  private final HomeSettingsQueryRepository homeSettingsQueryRepository;

  public List<OtherWaitingListResponse> getAllWaitingOfOtherShopByCustomerPhone(
      String shopId,
      String customerPhone,
      LocalDate operationDate
  ) {
    PhoneNumber encCustomerPhone = PhoneNumberUtils.ofKr(customerPhone);
    List<WaitingOfOtherShopQueryDto> otherWaitings = waitingRegisterQueryRepository.getAllWaitingOfOtherShopByCustomerPhone(
        encCustomerPhone,
        operationDate
    );

    Map<String, HomeSettingsEntity> homeSettingsMap = createHomeSettingsMap(otherWaitings);

    return otherWaitings.stream()
        .filter(o -> !shopId.equals(o.getShopId()))
        .map(waiting -> {
          int waitingOrder = waitingCountQueryRepository.countAllWaitingTeamLessThanOrEqualOrder(
              waiting.getShopId(), operationDate, waiting.getWaitingOrder(), waiting.getSeatOptionName());

          HomeSettingsEntity homeSettings = homeSettingsMap.getOrDefault(
              waiting.getShopId(), createDefaultHomeSettings(waiting.getShopId())
          );

          HomeSettingsData homeSettingsData = homeSettings.getHomeSettingsData();
          Integer expectedWaitingPeriodSetting = getExpectedPeriodBySeatOption(waiting.getSeatOptionName(),
              homeSettingsData);

          return new OtherWaitingListResponse(waiting, waitingOrder, expectedWaitingPeriodSetting);
        })
        .toList();
  }


  private Map<String, HomeSettingsEntity> createHomeSettingsMap(List<WaitingOfOtherShopQueryDto> waitings) {
    List<String> shopIds = extractShopIdsFrom(waitings);
    List<HomeSettingsEntity> homeSettings = homeSettingsQueryRepository.findByShopIds(shopIds);

    return homeSettings.stream()
        .collect(Collectors.toMap(HomeSettingsEntity::getShopId, item -> item, (item1, item2) -> item1));
  }

  private List<String> extractShopIdsFrom(List<WaitingOfOtherShopQueryDto> waitings) {
    return convert(waitings, WaitingOfOtherShopQueryDto::getShopId);
  }

  private Integer getExpectedPeriodBySeatOption(String seatOptionName,
      HomeSettingsData homeSettingsData) {
    return homeSettingsData
        .getExpectedPeriodBySeatOption(seatOptionName);
  }

  private HomeSettingsEntity createDefaultHomeSettings(String shopId) {
    return new HomeSettingsEntity(shopId, DefaultHomeSettingDataFactory.create());
  }

}
