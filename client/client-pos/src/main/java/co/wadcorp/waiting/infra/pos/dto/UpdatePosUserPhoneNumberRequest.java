package co.wadcorp.waiting.infra.pos.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdatePosUserPhoneNumberRequest {

  private String phone;
  private String certNo;

  public UpdatePosUserPhoneNumberRequest() {
  }

  @Builder
  public UpdatePosUserPhoneNumberRequest(String phone, String certNo) {
    this.phone = phone;
    this.certNo = certNo;
  }
}
