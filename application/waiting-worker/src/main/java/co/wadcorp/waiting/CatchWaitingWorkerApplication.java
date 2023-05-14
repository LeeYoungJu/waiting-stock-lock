package co.wadcorp.waiting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CatchWaitingWorkerApplication {

  public static void main(String[] args) {
    SpringApplication.run(CatchWaitingWorkerApplication.class);
  }
}