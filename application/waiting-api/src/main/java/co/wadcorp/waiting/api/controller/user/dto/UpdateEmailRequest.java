package co.wadcorp.waiting.api.controller.user.dto;

import lombok.Getter;

@Getter
public class UpdateEmailRequest {

  private String newEmail;

  public UpdateEmailRequest() {
  }

  public UpdateEmailRequest(String newEmail) {
    this.newEmail = newEmail;
  }
}
