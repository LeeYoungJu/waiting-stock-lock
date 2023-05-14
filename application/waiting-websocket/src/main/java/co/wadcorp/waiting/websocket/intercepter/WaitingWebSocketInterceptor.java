package co.wadcorp.waiting.websocket.intercepter;

import co.wadcorp.waiting.data.service.device.DeviceWebsocketService;
import co.wadcorp.waiting.websocket.auth.authenticator.CatchPosTokenAuthenticator;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class WaitingWebSocketInterceptor implements ChannelInterceptor {

  private static final String X_CTM_AUTH = "X-CTM-AUTH";
  private static final String DEVICE_ID = "X-REQUEST-ID";

  private final CatchPosTokenAuthenticator catchPosTokenAuthenticator;
  private final DeviceWebsocketService deviceWebsocketService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
        StompHeaderAccessor.class);
    if (accessor == null) {
      return null;
    }

    final StompCommand command = accessor.getCommand();
    String deviceUuid = accessor.getFirstNativeHeader(DEVICE_ID);

    // 임시 코드
    if (!StringUtils.hasText(deviceUuid)) {
      deviceUuid = "temp-device-uuid-" + UUID.randomUUID();
    }

    if (StompCommand.CONNECT.equals(command)) {
      String token = accessor.getFirstNativeHeader(X_CTM_AUTH);


      try {
        InetAddress inetAddress = InetAddress.getLocalHost(); // 로컬 기준 5초 소요

        catchPosTokenAuthenticator.authenticate(token);

        Authentication auth = new UsernamePasswordAuthenticationToken(deviceUuid, deviceUuid);
        accessor.setUser(auth);

        deviceWebsocketService.connect(deviceUuid, inetAddress.getHostAddress());
        log.info("웹소켓 접속 성공. 패드 UUID: {}", deviceUuid);
      } catch (UnknownHostException e) {
        log.error("웹소켓 CONNECT 시 서버 IP 조회 오류", e);
      }
    } else if (StompCommand.DISCONNECT.equals(command)) {

      try {
        InetAddress inetAddress = InetAddress.getLocalHost(); // 로컬 기준 5초 소요
        String accessorUserName = Objects.requireNonNull(accessor.getUser()).getName();


        deviceWebsocketService.disconnect(accessorUserName, inetAddress.getHostAddress());

        log.info("웹소켓 연결 해제. 패드 UUID: {}", accessorUserName);
      } catch (UnknownHostException e) {
        log.error("웹소켓 CONNECT 시 서버 IP 조회 오류", e);
      }


    } else if (StompCommand.SEND.equals(command)) {
      log.info("웹소켓 전송. 패드 UUID: {}", deviceUuid);

    } else if (StompCommand.SUBSCRIBE.equals(command)) {
      String destination = accessor.getDestination();
      String[] split = destination.split("/");
      String shopId = split[3];


      try {
        InetAddress inetAddress = InetAddress.getLocalHost(); // 로컬 기준 5초 소요
        deviceWebsocketService.subscribe(deviceUuid, inetAddress.getHostAddress(), shopId);

        log.info("웹소켓 구독. 패드 UUID: {}", deviceUuid);
      } catch (UnknownHostException e) {
        log.error("웹소켓 CONNECT 시 서버 IP 조회 오류", e);
      }
    }

    return message;
  }
}
