package co.wadcorp.waiting.handler.event;

import static co.wadcorp.waiting.handler.support.ExcludeSendMessageShopIdConstant.EXCLUDE_SHOP_IDS;

import co.wadcorp.waiting.data.domain.customer.CustomerEntity;
import co.wadcorp.waiting.data.domain.message.MessageSendHistoryEntity;
import co.wadcorp.waiting.data.domain.message.MessageTemplate;
import co.wadcorp.waiting.data.domain.message.SendChannel;
import co.wadcorp.waiting.data.domain.message.SendStatus;
import co.wadcorp.waiting.data.domain.message.SendType;
import co.wadcorp.waiting.data.domain.shop.ShopEntity;
import co.wadcorp.waiting.data.service.shop.ShopService;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryService;
import co.wadcorp.waiting.data.event.SeatCanceledEvent;
import co.wadcorp.waiting.data.service.customer.CustomerService;
import co.wadcorp.waiting.data.service.message.MessageSendHistoryService;
import co.wadcorp.waiting.data.service.message.MessageTemplateService;
import co.wadcorp.waiting.handler.support.TemplateContentsReplaceHelper;
import co.wadcorp.waiting.handler.support.WaitingWebProperties;
import co.wadcorp.waiting.infra.message.MessageSendClient;
import co.wadcorp.waiting.infra.message.dto.SendMessageRequest;
import co.wadcorp.waiting.infra.message.dto.SendMessageResponse;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
public class WaitingSeatCanceledEventHandler {

  private final WaitingHistoryService waitingHistoryService;
  private final ShopService shopService;
  private final CustomerService customerService;
  private final MessageTemplateService messageTemplateService;
  private final MessageSendHistoryService messageSendHistoryService;

  private final MessageSendClient messageSendClient;
  private final WaitingWebProperties webProperties;

  @Async
  @TransactionalEventListener(SeatCanceledEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void seatedCancel(SeatCanceledEvent event) {

    // 임시 알림톡 발송 못하게...
    if (EXCLUDE_SHOP_IDS.contains(event.shopId())) {
      return;
    }

    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(event.waitingHistorySeq());

    // 고객 정보가 없다면 메시지를 보낼 수 없다.
    if (waitingHistory.getCustomerSeq() == null) {
      return;
    }

    List<WaitingHistoryEntity> waitingHistoryEntities = waitingHistoryService.findAllBySeqIn(
        event.canceledWaitingHistorySeq());
    if (waitingHistoryEntities.isEmpty()) {
      return;
    }

    CustomerEntity customerEntity = customerService.findById(waitingHistory.getCustomerSeq());
    ShopEntity shopEntity = shopService.findByShopId(event.shopId());

    SendType waitingSeatedCancel = SendType.WAITING_SEATED_CANCEL;
    MessageTemplate messageTemplate = messageTemplateService.getMessageTemplate(
        waitingSeatedCancel, true);

    String shopNames = waitingHistoryEntities.stream()
        .map(item -> shopService.findByShopId(item.getShopId()))
        .map(ShopEntity::getShopName)
        .collect(Collectors.joining(", "));

    Map<String, String> templateParameter = Map.of(
        "가게명", shopEntity.getShopName(),
        "동시웨이팅매장", shopNames,
        "link", String.format(webProperties.getRestoreUrl(), waitingHistory.getWaitingId())

    );

    log.info("[알림톡] 착석 취소 waitingId={}", waitingHistory.getWaitingId());
    SendMessageResponse sendMessageResponse = messageSendClient.send(SendMessageRequest.builder()
        .phoneNumber(customerEntity.getEncCustomerPhone())
        .templateCode(messageTemplate.getTemplateCode())
        .templateContent(messageTemplate.getTemplateContent())
        .resendContent(messageTemplate.getResendContent())
        .templateParameter(templateParameter)
        .build());

    String content = TemplateContentsReplaceHelper.replace(messageTemplate.getTemplateContent(), templateParameter);
    messageSendHistoryService.save(MessageSendHistoryEntity.builder()
        .requestId(sendMessageResponse.getRequestId())
        .waitingId(waitingHistory.getWaitingId())
        .waitingHistorySeq(waitingHistory.getSeq())
        .sendChannel(SendChannel.ALIMTALK)
        .sendType(waitingSeatedCancel)
        .encCustomerPhone(customerEntity.getEncCustomerPhone())
        .templateCode(messageTemplate.getTemplateCode())
        .templateName(messageTemplate.getTemplateCode())
        .content(content)
        .status(SendStatus.SUCCESS)
        .sendDateTime(ZonedDateTimeUtils.nowOfSeoul())
        .build());
  }
}
