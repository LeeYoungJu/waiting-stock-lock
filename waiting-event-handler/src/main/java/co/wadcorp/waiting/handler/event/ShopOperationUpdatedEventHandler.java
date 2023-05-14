package co.wadcorp.waiting.handler.event;

import co.wadcorp.waiting.data.domain.channel.ChannelMappingEntity;
import co.wadcorp.waiting.data.event.ShopOperationUpdatedEvent;
import co.wadcorp.waiting.data.service.channel.SelectChannelService;
import co.wadcorp.waiting.infra.kafka.publisher.WaitingShopOperationPublisher;
import co.wadcorp.waiting.shared.enums.ServiceChannelId;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class ShopOperationUpdatedEventHandler {

  private final SelectChannelService selectChannelService;
  private final WaitingShopOperationPublisher waitingShopOperationPublisher;

  @Async
  @TransactionalEventListener(ShopOperationUpdatedEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void event(ShopOperationUpdatedEvent event) {

    publish(event.shopId());
  }

  private void publish(String shopId) {
    Optional<ChannelMappingEntity> channelMappingByWaitingShopIds = selectChannelService.getChannelMappingByWaitingShopIds(
        ServiceChannelId.CATCHTABLE_B2C.getValue(), shopId);

    channelMappingByWaitingShopIds.ifPresent(channelMapping -> {
      LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();

      Long shopSeq = Long.valueOf(channelMapping.getChannelShopId());
      log.info("매장 운영정보 변경 이벤트 shopSeq={}, operationDate={}", shopSeq, operationDate);
      waitingShopOperationPublisher.publish(shopSeq, operationDate);
    });
  }

}
