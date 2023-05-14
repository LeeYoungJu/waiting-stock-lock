package co.wadcorp.waiting.worker.controller.dto;

import lombok.Getter;

@Getter
public class StopSchedulerRequest {

  private final String bean;
  private final String beanName;

  public StopSchedulerRequest(String bean, String beanName) {
    this.bean = bean;
    this.beanName = beanName;
  }
}
