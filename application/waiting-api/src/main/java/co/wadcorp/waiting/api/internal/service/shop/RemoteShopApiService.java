package co.wadcorp.waiting.api.internal.service.shop;

import static co.wadcorp.libs.stream.StreamUtils.convertToMap;

import co.wadcorp.waiting.api.internal.service.shop.dto.request.RemoteShopOperationServiceRequest;
import co.wadcorp.waiting.api.internal.service.shop.dto.response.RemoteShopOperationResponse;
import co.wadcorp.waiting.api.internal.service.shop.dto.response.RemoteShopOperationResponse.RemoteShopOperationResponseBuilder;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.data.domain.settings.AlarmSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.putoff.DisablePutOffEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.status.OperationStatus;
import co.wadcorp.waiting.data.query.settings.AlarmSettingsQueryRepository;
import co.wadcorp.waiting.data.query.settings.DisablePutOffQueryRepository;
import co.wadcorp.waiting.data.query.settings.OrderSettingsQueryRepository;
import co.wadcorp.waiting.data.query.settings.PrecautionSettingsQueryRepository;
import co.wadcorp.waiting.data.query.shop.ShopQueryRepository;
import co.wadcorp.waiting.data.query.shop.dto.ShopUsedFieldDto;
import co.wadcorp.waiting.data.query.waiting.ShopOperationInfoQueryRepository;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RemoteShopApiService {

  private final ShopOperationInfoQueryRepository shopOperationInfoQueryRepository;
  private final ShopQueryRepository shopQueryRepository;
  private final AlarmSettingsQueryRepository alarmSettingsQueryRepository;
  private final PrecautionSettingsQueryRepository precautionSettingsQueryRepository;
  private final DisablePutOffQueryRepository disablePutOffQueryRepository;
  private final OrderSettingsQueryRepository orderSettingsQueryRepository;

  public List<RemoteShopOperationResponse> findShopOperations(ChannelShopIdMapping shopIdMapping,
      RemoteShopOperationServiceRequest request, ZonedDateTime now) {
    List<String> shopIds = shopIdMapping.getAllWaitingShopIds();
    LocalDate operationDate = request.getOperationDate();

    Map<String, ShopOperationInfoEntity> shopOperationInfoMap = createShopOperationInfoMap(shopIds,
        operationDate);
    Map<String, ShopUsedFieldDto> shopMap = createShopMap(shopIds);
    Map<String, AlarmSettingsEntity> alarmSettingsMap = createAlarmSettingsMap(shopIds);
    Map<String, PrecautionSettingsEntity> precautionSettingsMap = createPrecautionSettingsMap(
        shopIds);
    Map<String, Boolean> disablePutOffMap = createDisablePutOffMap(shopIds);
    Map<String, Boolean> isPossibleOrderMap = createIsPossibleOrderMap(shopIds);

    return shopIds.stream()
        .map(shopId -> {
          ShopOperationInfoEntity shopOperationInfoEntity = shopOperationInfoMap.get(shopId);
          ShopUsedFieldDto shopEntity = shopMap.get(shopId);
          AlarmSettingsEntity alarmSettingsEntity = alarmSettingsMap.get(shopId);
          PrecautionSettingsEntity precautionSettingsEntity = precautionSettingsMap.get(shopId);

          RemoteShopOperationResponseBuilder remoteShopOperationResponseBuilder = RemoteShopOperationResponse.builder()
              .shopId(Long.valueOf(shopIdMapping.getChannelShopId(shopId)))
              .operationDate(operationDate)
              .isUsedWaiting(shopEntity.getIsMembership())
              .isUsedRemoteWaiting(shopEntity.getIsUsedRemoteWaiting())
              .operationStatus(OperationStatus.find(shopOperationInfoEntity, now))
              .operationStartDateTime(shopOperationInfoEntity.getOperationStartDateTime())
              .operationEndDateTime(shopOperationInfoEntity.getOperationEndDateTime())
              .remoteOperationStartDateTime(
                  shopOperationInfoEntity.getRemoteOperationStartDateTime()
              )
              .remoteOperationEndDateTime(shopOperationInfoEntity.getRemoteOperationEndDateTime())
              .closedReason(shopOperationInfoEntity.findClosedReason(now))
              .autoAlarmOrdering(alarmSettingsEntity.getAutoAlarmOrdering())
              .isUsedPrecautions(precautionSettingsEntity.isUsedPrecautions())
              .precautions(precautionSettingsEntity.getPrecautions())
              .messagePrecaution(precautionSettingsEntity.getMessagePrecaution())
              .disablePutOff(disablePutOffMap.getOrDefault(shopId, false))
              .isPossibleOrder(isPossibleOrderMap.getOrDefault(shopId, false));

          setPauseInfos(remoteShopOperationResponseBuilder, shopOperationInfoEntity);

          return remoteShopOperationResponseBuilder
              .build();
        })
        .toList();
  }

  private Map<String, ShopOperationInfoEntity> createShopOperationInfoMap(
      List<String> shopIds, LocalDate operationDate) {
    return convertToMap(
        shopOperationInfoQueryRepository.findByShopIdsAndOperationDate(shopIds, operationDate),
        ShopOperationInfoEntity::getShopId
    );
  }

  private Map<String, ShopUsedFieldDto> createShopMap(List<String> shopIds) {
    return convertToMap(
        shopQueryRepository.findByUsedWaitings(shopIds),
        ShopUsedFieldDto::getShopId
    );
  }

  private Map<String, AlarmSettingsEntity> createAlarmSettingsMap(List<String> shopIds) {
    return alarmSettingsQueryRepository.findAllPublishedByShopIds(shopIds)
        .stream()
        .collect(Collectors.toMap(AlarmSettingsEntity::getShopId, e -> e, (e1, e2) -> e1));
  }

  private Map<String, PrecautionSettingsEntity> createPrecautionSettingsMap(
      List<String> shopIds) {
    return precautionSettingsQueryRepository.findAllPublishedByShopIds(shopIds)
        .stream()
        .collect(Collectors.toMap(PrecautionSettingsEntity::getShopId, e -> e, (e1, e2) -> e1));
  }

  private Map<String, Boolean> createDisablePutOffMap(List<String> shopIds) {
    return disablePutOffQueryRepository.findDisabledPutOffBy(shopIds)
        .stream()
        .collect(Collectors.toMap(
            DisablePutOffEntity::getShopId,
            DisablePutOffEntity::getIsPublished,
            (e1, e2) -> e1
        ));
  }

  private Map<String, Boolean> createIsPossibleOrderMap(List<String> shopIds) {
    return orderSettingsQueryRepository.findByShopIds(shopIds)
        .stream()
        .collect(Collectors.toMap(
            OrderSettingsEntity::getShopId,
            OrderSettingsEntity::isPossibleOrder,
            (e1, e2) -> e1
        ));
  }

  private void setPauseInfos(RemoteShopOperationResponseBuilder remoteShopOperationResponseBuilder,
      ShopOperationInfoEntity shopOperationInfoEntity) {
    String manualPauseReason = shopOperationInfoEntity.getManualPauseReason();
    String autoPauseReason = shopOperationInfoEntity.getAutoPauseReason();

    if (StringUtils.hasText(autoPauseReason)) {
      remoteShopOperationResponseBuilder
          .pauseStartDateTime(shopOperationInfoEntity.getAutoPauseStartDateTime())
          .pauseEndDateTime(shopOperationInfoEntity.getAutoPauseEndDateTime())
          .pauseReasonId(shopOperationInfoEntity.getAutoPauseReasonId())
          .pauseReason(autoPauseReason)
          .autoPauseStartDateTime(shopOperationInfoEntity.getAutoPauseStartDateTime())
          .autoPauseEndDateTime(shopOperationInfoEntity.getAutoPauseEndDateTime())
          .autoPauseReasonId(shopOperationInfoEntity.getAutoPauseReasonId())
          .autoPauseReason(autoPauseReason);
    }

    if (StringUtils.hasText(manualPauseReason)) {
      remoteShopOperationResponseBuilder
          .pauseStartDateTime(shopOperationInfoEntity.getManualPauseStartDateTime())
          .pauseEndDateTime(shopOperationInfoEntity.getManualPauseEndDateTime())
          .pauseReasonId(shopOperationInfoEntity.getManualPauseReasonId())
          .pauseReason(manualPauseReason)
          .manualPauseStartDateTime(shopOperationInfoEntity.getManualPauseStartDateTime())
          .manualPauseEndDateTime(shopOperationInfoEntity.getManualPauseEndDateTime())
          .manualPauseReasonId(shopOperationInfoEntity.getManualPauseReasonId())
          .manualPauseReason(manualPauseReason);
    }

    remoteShopOperationResponseBuilder
        .remoteAutoPauseStartDateTime(shopOperationInfoEntity.getRemoteAutoPauseStartDateTime())
        .remoteAutoPauseEndDateTime(shopOperationInfoEntity.getRemoteAutoPauseEndDateTime());
  }

}
