package co.wadcorp.waiting.infra.message;

import co.wadcorp.libs.nhn_cloud.alimtalk.AlimtalkClient;
import co.wadcorp.libs.nhn_cloud.alimtalk.AlimtalkRecipient;
import co.wadcorp.libs.nhn_cloud.alimtalk.AlimtalkResendParameter;
import co.wadcorp.libs.nhn_cloud.alimtalk.AlimtalkSendRequest;
import co.wadcorp.libs.nhn_cloud.alimtalk.AlimtalkSendResponseMessage;
import co.wadcorp.libs.nhn_cloud.alimtalk.AlimtalkSendResult;
import co.wadcorp.waiting.infra.message.dto.SendMessageRequest;
import co.wadcorp.waiting.infra.message.dto.SendMessageResponse;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class NhnCloudMessageSendClient implements MessageSendClient {

  private final AlimtalkClient client;

  // 발송자 키
  @Value("${external.nhn-cloud.biz-message.sender-key}")
  private String SENDER_KEY;

  @Override
  public SendMessageResponse send(SendMessageRequest sendMessageRequest) {
    ExecutorService executorService = Executors.newCachedThreadPool();

    AlimtalkSendRequest request = new AlimtalkSendRequest(SENDER_KEY,
        sendMessageRequest.getTemplateCode());

    AlimtalkRecipient alimtalkRecipient = new AlimtalkRecipient(
        sendMessageRequest.getPhoneNumber());
    alimtalkRecipient.setTemplateParameter(sendMessageRequest.getTemplateParameter());

    // 알림톡이 실패했을 때 대체발송 하는 파라미터
    AlimtalkResendParameter resendParameter = new AlimtalkResendParameter();
    resendParameter.setResend(true);
    resendParameter.setResendContent(sendMessageRequest.getResendContent());
    alimtalkRecipient.setResendParameter(resendParameter);

    request.setRecipientList(List.of(alimtalkRecipient));

    // 발송 처리
    var sendFuture = executorService.submit(() -> client.send(request));
    try {

      AlimtalkSendResponseMessage response = sendFuture.get();
      List<AlimtalkSendResult> sendResults = response.getSendResults();

      AlimtalkSendResult alimtalkSendResult = sendResults.get(0);

      log.info(alimtalkSendResult.getResultMessage());
      return SendMessageResponse.builder()
          .requestId(String.format("%s-%s", response.getRequestId(),
              alimtalkSendResult.getRecipientSeq()))
          .resultCode(alimtalkSendResult.getResultCode())
          .resultMessage(alimtalkSendResult.getResultMessage())
          .build();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
