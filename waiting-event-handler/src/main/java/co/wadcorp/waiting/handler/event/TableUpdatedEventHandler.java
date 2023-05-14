package co.wadcorp.waiting.handler.event;

import co.wadcorp.waiting.data.domain.channel.ChannelMappingEntity;
import co.wadcorp.waiting.data.event.TableUpdatedEvent;
import co.wadcorp.waiting.data.service.channel.SelectChannelService;
import co.wadcorp.waiting.infra.kafka.publisher.WaitingTablePublisher;
import co.wadcorp.waiting.shared.enums.ServiceChannelId;
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
public class TableUpdatedEventHandler {

  private final SelectChannelService selectChannelService;
  private final WaitingTablePublisher waitingTablePublisher;

  @Async
  @TransactionalEventListener(TableUpdatedEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void sendEventMessage(TableUpdatedEvent event) {
    publish(event.shopId());
  }


  private void publish(String event) {
    Optional<ChannelMappingEntity> channelMappingByWaitingShopIds = selectChannelService.getChannelMappingByWaitingShopIds(
        ServiceChannelId.CATCHTABLE_B2C.getValue(),
        event);

    channelMappingByWaitingShopIds.ifPresent((item) -> {
      Long shopSeq = Long.valueOf(item.getChannelShopId());
      log.info("테이블 변경 이벤트 shopSeq={}", shopSeq);
      waitingTablePublisher.publish(shopSeq);
    });
  }

}
