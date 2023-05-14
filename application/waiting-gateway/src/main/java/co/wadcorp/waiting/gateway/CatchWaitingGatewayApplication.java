package co.wadcorp.waiting.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * gateway 메인 클래스.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"co.wadcorp.waiting"})
public class CatchWaitingGatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(CatchWaitingGatewayApplication.class, args);
  }
}
