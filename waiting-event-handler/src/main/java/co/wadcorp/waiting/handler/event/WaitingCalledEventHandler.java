package co.wadcorp.waiting.handler.event;

import static co.wadcorp.waiting.handler.support.ExcludeSendMessageShopIdConstant.EXCLUDE_SHOP_IDS;

import co.wadcorp.waiting.data.domain.customer.CustomerEntity;
import co.wadcorp.waiting.data.domain.message.MessageSendHistoryEntity;
import co.wadcorp.waiting.data.domain.message.MessageTemplate;
import co.wadcorp.waiting.data.domain.message.SendChannel;
import co.wadcorp.waiting.data.domain.message.SendStatus;
import co.wadcorp.waiting.data.domain.message.SendType;
import co.wadcorp.waiting.data.domain.settings.AlarmSettingsEntity;
import co.wadcorp.waiting.data.domain.shop.ShopEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryService;
import co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelTargetEntity;
import co.wadcorp.waiting.data.event.CalledEvent;
import co.wadcorp.waiting.data.service.customer.CustomerService;
import co.wadcorp.waiting.data.service.message.MessageSendHistoryService;
import co.wadcorp.waiting.data.service.message.MessageTemplateService;
import co.wadcorp.waiting.data.service.settings.AlarmSettingsService;
import co.wadcorp.waiting.data.service.shop.ShopService;
import co.wadcorp.waiting.data.service.waiting.AutoCancelTargetService;
import co.wadcorp.waiting.handler.support.TemplateContentsReplaceHelper;
import co.wadcorp.waiting.handler.support.WaitingWebProperties;
import co.wadcorp.waiting.infra.message.MessageSendClient;
import co.wadcorp.waiting.infra.message.dto.SendMessageRequest;
import co.wadcorp.waiting.infra.message.dto.SendMessageResponse;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.Duration;
import java.time.ZonedDateTime;
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
public class WaitingCalledEventHandler {

  private static final String RIGHT_NOW = "지금 바로";
  private static final String PERIOD = "%s분 동안";

  private final WaitingHistoryService waitingHistoryService;
  private final ShopService shopService;
  private final CustomerService customerService;
  private final MessageTemplateService messageTemplateService;
  private final MessageSendHistoryService messageSendHistoryService;
  private final AlarmSettingsService alarmSettingsService;

  private final MessageSendClient messageSendClient;
  private final WaitingWebProperties webProperties;

  private final AutoCancelTargetService autoCancelTargetService;

  @Async
  @TransactionalEventListener(CalledEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void sendMessage(CalledEvent event) {
    // 임시 알림톡 발송 못하게...
    if (EXCLUDE_SHOP_IDS.contains(event.shopId())) {
      return;
    }

    Long waitingHistorySeq = event.waitingHistorySeq();
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(waitingHistorySeq);

    Long customerSeq = waitingHistory.getCustomerSeq();
    if (customerSeq == null) {
      return;
    }

    SendType waitingCall = SendType.WAITING_CALL;
    MessageTemplate messageTemplate = messageTemplateService.getMessageTemplate(
        waitingCall, true);

    CustomerEntity customerEntity = customerService.findById(customerSeq);
    ShopEntity shopEntity = shopService.findByShopId(event.shopId());

    AutoCancelTargetEntity autoCancelTarget = autoCancelTargetService.findByWaitingId(
        waitingHistory.getWaitingId());

    AlarmSettingsEntity alarmSettings = alarmSettingsService.getAlarmSettings(event.shopId());

    String autoCancelPeriod = getCancelPeriodString(autoCancelTarget, event.currentDateTime(),
        alarmSettings.getAutoCancelPeriod());

    Map<String, String> templateParameter = Map.of(
        "가게명", shopEntity.getShopName(),
        "웨이팅번호", String.valueOf(waitingHistory.getWaitingNumber()),
        "호출멘트", autoCancelPeriod,
        "link", String.format(webProperties.getWaitingUrl(), waitingHistory.getWaitingId())
    );

    log.info("[알림톡] 취소 waitingId={}", waitingHistory.getWaitingId());
    SendMessageResponse sendMessageResponse = messageSendClient.send(SendMessageRequest.builder()
        .phoneNumber(customerEntity.getEncCustomerPhone())
        .templateCode(messageTemplate.getTemplateCode())
        .templateContent(messageTemplate.getTemplateContent())
        .resendContent(messageTemplate.getResendContent())
        .templateParameter(templateParameter)
        .build());

    String content = TemplateContentsReplaceHelper.replace(messageTemplate.getTemplateContent(),
        templateParameter);
    messageSendHistoryService.save(MessageSendHistoryEntity.builder()
        .requestId(sendMessageResponse.getRequestId())
        .waitingId(waitingHistory.getWaitingId())
        .waitingHistorySeq(waitingHistory.getSeq())
        .sendChannel(SendChannel.ALIMTALK)
        .sendType(waitingCall)
        .encCustomerPhone(customerEntity.getEncCustomerPhone())
        .templateCode(messageTemplate.getTemplateCode())
        .templateName(messageTemplate.getTemplateCode())
        .content(content)
        .status(SendStatus.SUCCESS)
        .sendDateTime(ZonedDateTimeUtils.nowOfSeoul())
        .build());
  }

  private String getCancelPeriodString(
      AutoCancelTargetEntity autoCancelTargetEntity,
      ZonedDateTime currentDateTime,
      Integer autoCancelPeriod
  ) {
    if (autoCancelTargetEntity == AutoCancelTargetEntity.EMPTY) {
      return String.format(PERIOD, autoCancelPeriod);
    }

    Duration diff = Duration.between(currentDateTime.withNano(0),
        autoCancelTargetEntity.getExpectedCancelDateTime().withNano(0));
    long minutes = diff.toMinutes();

    return minutes < 1 ? RIGHT_NOW : String.format(PERIOD, minutes);
  }
}
