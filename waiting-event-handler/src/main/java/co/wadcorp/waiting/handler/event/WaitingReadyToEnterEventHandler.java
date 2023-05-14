package co.wadcorp.waiting.handler.event;

import static co.wadcorp.waiting.handler.support.ExcludeSendMessageShopIdConstant.EXCLUDE_SHOP_IDS;

import co.wadcorp.waiting.data.domain.customer.CustomerEntity;
import co.wadcorp.waiting.data.domain.message.MessageSendHistoryEntity;
import co.wadcorp.waiting.data.domain.message.MessageTemplate;
import co.wadcorp.waiting.data.domain.message.SendChannel;
import co.wadcorp.waiting.data.domain.message.SendStatus;
import co.wadcorp.waiting.data.domain.message.SendType;
import co.wadcorp.waiting.data.domain.shop.ShopEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryService;
import co.wadcorp.waiting.data.event.ReadyToEnterEvent;
import co.wadcorp.waiting.data.query.waiting.WaitingCountQueryRepository;
import co.wadcorp.waiting.data.service.customer.CustomerService;
import co.wadcorp.waiting.data.service.message.MessageSendHistoryService;
import co.wadcorp.waiting.data.service.message.MessageTemplateService;
import co.wadcorp.waiting.data.service.shop.ShopService;
import co.wadcorp.waiting.handler.support.TemplateContentsReplaceHelper;
import co.wadcorp.waiting.handler.support.WaitingWebProperties;
import co.wadcorp.waiting.infra.message.MessageSendClient;
import co.wadcorp.waiting.infra.message.dto.SendMessageRequest;
import co.wadcorp.waiting.infra.message.dto.SendMessageResponse;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.util.Map;
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
public class WaitingReadyToEnterEventHandler {

  private final WaitingHistoryService waitingHistoryService;
  private final ShopService shopService;
  private final CustomerService customerService;
  private final MessageTemplateService messageTemplateService;
  private final MessageSendHistoryService messageSendHistoryService;

  private final WaitingCountQueryRepository waitingCountQueryRepository;

  private final MessageSendClient messageSendClient;
  private final WaitingWebProperties webProperties;

  @Async
  @TransactionalEventListener(ReadyToEnterEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void sendMessage(ReadyToEnterEvent event) {
    sendMessage(event.shopId(), event.waitingHistorySeq(), event.operationDate());
  }

  private void sendMessage(String shopId, Long waitingHistorySeq, LocalDate operationDate) {
    // 임시 알림톡 발송 못하게...
    if (EXCLUDE_SHOP_IDS.contains(shopId)) {
      return;
    }

    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(waitingHistorySeq);
    Long customerSeq = waitingHistory.getCustomerSeq();
    if (customerSeq == null) {
      return;
    }

    SendType waitingReadyToEnter = SendType.WAITING_READY_TO_ENTER;
    MessageTemplate messageTemplate = messageTemplateService.getMessageTemplate(
        waitingReadyToEnter, true);

    CustomerEntity customerEntity = customerService.findById(waitingHistory.getCustomerSeq());
    ShopEntity shopEntity = shopService.findByShopId(shopId);

    // Query 체크..
    int waitingExpectedOrder = waitingCountQueryRepository.countAllWaitingTeamLessThanOrEqualOrder(
        waitingHistory.getShopId(),
        operationDate, waitingHistory.getWaitingOrder(),
        waitingHistory.getSeatOptionName());

    Map<String, String> templateParameter = Map.of(
        "가게명", shopEntity.getShopName(),
        "웨이팅번호", String.valueOf(waitingHistory.getWaitingNumber()),
        "웨이팅순서", String.valueOf(waitingExpectedOrder),
        "link", String.format(webProperties.getWaitingUrl(), waitingHistory.getWaitingId())
    );

    log.info("[알림톡] 입장준비 waitingId={}", waitingHistory.getWaitingId());
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
        .waitingHistorySeq(waitingHistorySeq)
        .sendChannel(SendChannel.ALIMTALK)
        .sendType(waitingReadyToEnter)
        .encCustomerPhone(customerEntity.getEncCustomerPhone())
        .templateCode(messageTemplate.getTemplateCode())
        .templateName(messageTemplate.getTemplateCode())
        .content(content)
        .status(SendStatus.SUCCESS)
        .sendDateTime(ZonedDateTimeUtils.nowOfSeoul())
        .build());
  }
}
