package co.wadcorp.waiting.api.internal.service.meta;

import co.wadcorp.waiting.api.internal.service.meta.dto.RemoteMetaResponse;
import co.wadcorp.waiting.api.internal.service.person.RemotePersonOptionApiService;
import co.wadcorp.waiting.api.internal.service.person.dto.response.RemotePersonOptionResponse;
import co.wadcorp.waiting.api.internal.service.shop.RemoteShopApiService;
import co.wadcorp.waiting.api.internal.service.shop.dto.request.RemoteShopOperationServiceRequest;
import co.wadcorp.waiting.api.internal.service.shop.dto.response.RemoteShopOperationResponse;
import co.wadcorp.waiting.api.internal.service.table.RemoteTableApiService;
import co.wadcorp.waiting.api.internal.service.table.dto.response.RemoteTableSettingResponse;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RemoteMetaApiService {

  private final RemotePersonOptionApiService remotePersonOptionApiService;
  private final RemoteTableApiService remoteTableApiService;
  private final RemoteShopApiService remoteShopApiService;

  public List<RemoteMetaResponse> getMeta(ChannelShopIdMapping channelShopIdMapping,
      LocalDate operationDate,
      ZonedDateTime nowDateTime) {

    Map<Long, RemoteShopOperationResponse> groupByShopOperations = groupByShopOperations(
        channelShopIdMapping, operationDate, nowDateTime);

    Map<Long, RemoteTableSettingResponse> groupByTableSettings = groupByTableSettings(
        channelShopIdMapping);

    Map<Long, RemotePersonOptionResponse> groupByPersonOptions = groupByPersonOptions(
        channelShopIdMapping);

    List<String> waitingIds = channelShopIdMapping.getAllWaitingShopIds();

    return waitingIds.stream()
        .map(waitingShopId -> {

          long channelShopId = Long.parseLong(channelShopIdMapping.getChannelShopId(waitingShopId));
          RemoteShopOperationResponse remoteShopOperationResponse = groupByShopOperations.get(
              channelShopId);
          RemoteTableSettingResponse remoteTableSettingResponse = groupByTableSettings.get(
              channelShopId);
          RemotePersonOptionResponse remotePersonOptionResponse = groupByPersonOptions.get(
              channelShopId);

          return RemoteMetaResponse
              .builder()
              .shopId(channelShopId)
              .shopOperation(remoteShopOperationResponse)
              .tableSetting(remoteTableSettingResponse)
              .personOption(remotePersonOptionResponse)
              .build();
        })
        .toList();
  }

  private Map<Long, RemoteShopOperationResponse> groupByShopOperations(
      ChannelShopIdMapping channelShopIdMapping, LocalDate operationDate,
      ZonedDateTime nowDateTime) {
    List<RemoteShopOperationResponse> shopOperations = remoteShopApiService.findShopOperations(
        channelShopIdMapping, RemoteShopOperationServiceRequest.builder().operationDate(
            operationDate).build(), nowDateTime);

    return shopOperations.stream()
        .collect(Collectors.toMap(RemoteShopOperationResponse::getShopId, item -> item));
  }

  private Map<Long, RemoteTableSettingResponse> groupByTableSettings(
      ChannelShopIdMapping channelShopIdMapping) {

    List<RemoteTableSettingResponse> tableSettings = remoteTableApiService.findTableSettings(
        channelShopIdMapping);

    return tableSettings.stream()
        .collect(Collectors.toMap(RemoteTableSettingResponse::getShopId, item -> item, (item1, item2) -> item1));
  }

  private Map<Long, RemotePersonOptionResponse> groupByPersonOptions(
      ChannelShopIdMapping channelShopIdMapping) {

    List<RemotePersonOptionResponse> personOptions = remotePersonOptionApiService.findPersonOptions(
        channelShopIdMapping);

    return personOptions.stream()
        .collect(Collectors.toMap(RemotePersonOptionResponse::getShopId, item -> item, (item1, item2) -> item1));
  }
}
