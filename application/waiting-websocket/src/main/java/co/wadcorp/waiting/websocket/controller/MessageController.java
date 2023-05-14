package co.wadcorp.waiting.websocket.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

  private final SimpMessagingTemplate template;

  // /pub/message 로 클라이언트가 요청한 경우 처리
  @MessageMapping(value = "/shops/{shopId}/ack")
  @SendTo("/sub/shops/{shopId}")
  public String message(
      @Header("simpSessionId") String sessionId,
      @DestinationVariable("shopId") String shopId,
      @Payload String message,
      SimpMessageHeaderAccessor headerAccessor
  ) {
    final String DEFAULT_SUBSCRIBE_CHANNEL_NAME = "/sub/shops/";
    final String channelName = DEFAULT_SUBSCRIBE_CHANNEL_NAME + shopId;

//    template.convertAndSend(channelName, message);

    // 헤더, path variable, 메시지 데이터
    log.info("sessionId:{}, shopId:{}, message:{}", sessionId, shopId, message);
    log.info("header:{}", headerAccessor.toString());

    return message;
  }
}
