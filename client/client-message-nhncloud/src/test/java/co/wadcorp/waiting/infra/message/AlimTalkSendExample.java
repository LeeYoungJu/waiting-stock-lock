package co.wadcorp.waiting.infra.message;

import static org.junit.jupiter.api.Assertions.*;

import co.wadcorp.libs.nhn_cloud.alimtalk.AlimtalkClient;
import co.wadcorp.libs.nhn_cloud.alimtalk.AlimtalkRecipient;
import co.wadcorp.libs.nhn_cloud.alimtalk.AlimtalkSendRequest;
import co.wadcorp.libs.nhn_cloud.alimtalk.AlimtalkSendResponseMessage;
import co.wadcorp.libs.nhn_cloud.alimtalk.AlimtalkSendResult;
import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.waiting.shared.util.PhoneNumberUtils;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * 알림톡 발송 예제.
 */
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AlimTalkSendExample {

  private AlimtalkClient client;
  private ExecutorService executorService;

  // 발송자 키
  private static final String SENDER_KEY = "d14fc59b94e964395bc3b8511fe08055e804895c";

  /**
   * 테스트 전에 알림톡 클라이언트를 설정한다.
   */
  @BeforeAll
  public void initClient() {
    executorService = Executors.newCachedThreadPool();

    var appKey = "xiDXOZqZ6aTiFuPW";
    var secretKey = "wELwvyUj";
    client = new AlimtalkClient(appKey, secretKey);
  }

  @AfterAll
  public void onFinish() {
    executorService.shutdown();
  }

  @Test
  @Disabled("실제 알림톡 발송은 전체 테스트에서 제외")
  public void send() {
    String templateCode = "CW_CUSTOMER_REG_000";
    AlimtalkSendRequest request = new AlimtalkSendRequest(SENDER_KEY, templateCode);

    // 수신자1 설정
    PhoneNumber phoneNumber1 = PhoneNumberUtils.ofKr("010-2499-1180");
    AlimtalkRecipient recipient1 = request.addRecipient(phoneNumber1);
    recipient1.setTemplateParameter(Map.of("가게명", "우리집",
        "인원수", "4",
        "세부인원", "어른없음",
        "웨이팅순번", "13",
        "잔여팀수", "1",
        "전화번호", "없어요",
        "매장안내사항", "여기에는 조금 긴 안내사항도 등록 가능합니다."
    ));

    // 발송 처리
    var sendFuture = executorService.submit(() -> client.send(request));
    try {
      AlimtalkSendResponseMessage response = sendFuture.get();
      List<AlimtalkSendResult> sendResults = response.getSendResults();

      assertEquals(1, sendResults.size());

    } catch (Exception e) {
      log.error("발송 처리 실패. ", e);
    }
  }
}