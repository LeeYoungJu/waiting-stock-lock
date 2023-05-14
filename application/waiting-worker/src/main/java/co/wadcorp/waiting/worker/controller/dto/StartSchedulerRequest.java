package co.wadcorp.waiting.worker.controller.dto;

import lombok.Getter;

@Getter
public class StartSchedulerRequest {

  private final String bean;
  private final String beanName;

  public StartSchedulerRequest(String bean, String beanName) {
    this.bean = bean;
    this.beanName = beanName;
  }
}
