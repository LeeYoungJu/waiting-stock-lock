package co.wadcorp.waiting.infra.pos.dto;

import lombok.Getter;

@Getter
public class UpdatePosUserEmailRequest {

  private String newEmail;

  public UpdatePosUserEmailRequest() {
  }

  public UpdatePosUserEmailRequest(String newEmail) {
    this.newEmail = newEmail;
  }
}
