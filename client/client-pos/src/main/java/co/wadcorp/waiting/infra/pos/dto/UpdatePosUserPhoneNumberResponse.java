package co.wadcorp.waiting.infra.pos.dto;

import lombok.Getter;

@Getter
public class UpdatePosUserPhoneNumberResponse {

  private String phone;
  private String certNo;

  public UpdatePosUserPhoneNumberResponse() {
  }

  public UpdatePosUserPhoneNumberResponse(String phone, String certNo) {
    this.phone = phone;
    this.certNo = certNo;
  }
}
