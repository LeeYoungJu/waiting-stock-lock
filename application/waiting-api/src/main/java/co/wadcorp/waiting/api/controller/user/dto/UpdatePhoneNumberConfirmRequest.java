package co.wadcorp.waiting.api.controller.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdatePhoneNumberConfirmRequest {

  private String phoneNumber;
  private String certNo;

  public UpdatePhoneNumberConfirmRequest() {
  }

  @Builder
  public UpdatePhoneNumberConfirmRequest(String phoneNumber, String certNo) {
    this.phoneNumber = phoneNumber;
    this.certNo = certNo;
  }
}
