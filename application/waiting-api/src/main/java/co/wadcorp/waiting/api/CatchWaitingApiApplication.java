package co.wadcorp.waiting.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "co.wadcorp.waiting")
public class CatchWaitingApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(CatchWaitingApiApplication.class, args);
  }
}
