package co.wadcorp.waiting.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "co.wadcorp.waiting")
public class CatchWaitingBatchApplication {

  public static void main(String[] args) {
    System.exit(SpringApplication.exit(
        SpringApplication.run(CatchWaitingBatchApplication.class, args)
    ));
  }

}