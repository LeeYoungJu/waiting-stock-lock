package co.wadcorp.waiting.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * websocket 메인 클래스.
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"co.wadcorp.waiting"})
public class CatchWaitingWebSocketApplication {

  public static void main(String[] args) {
    SpringApplication.run(CatchWaitingWebSocketApplication.class, args);
  }
}
