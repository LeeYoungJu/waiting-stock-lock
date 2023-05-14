package co.wadcorp.waiting.infra.pos.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdatePosUserPasswordRequest {

  private String oldPassword;
  private String newPassword;

  public UpdatePosUserPasswordRequest() {
  }

  @Builder
  public UpdatePosUserPasswordRequest(String oldPassword, String newPassword) {
    this.oldPassword = oldPassword;
    this.newPassword = newPassword;
  }
}
