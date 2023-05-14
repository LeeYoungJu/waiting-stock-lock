package co.wadcorp.waiting.api.controller.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdatePhoneNumberRequest {

  private String phoneNumber;

  public UpdatePhoneNumberRequest() {
  }

  @Builder
  public UpdatePhoneNumberRequest(String phoneNumber, String certNo) {
    this.phoneNumber = phoneNumber;
  }
}
