package co.wadcorp.waiting.api.controller.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdatePasswordRequest {

  private String oldPassword;
  private String newPassword;


  public UpdatePasswordRequest() {
  }

  @Builder
  public UpdatePasswordRequest(String oldPassword, String newPassword) {
    this.oldPassword = oldPassword;
    this.newPassword = newPassword;
  }
}
