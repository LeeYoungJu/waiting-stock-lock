package co.wadcorp.waiting.infra.pos.dto;

import lombok.Getter;

@Getter
public class PosUserResponse {

  private String phone;
  private String email;
  private String updateEmail;

  public PosUserResponse() {
  }

  public PosUserResponse(String phone, String email, String updateEmail) {
    this.phone = phone;
    this.email = email;
    this.updateEmail = updateEmail;
  }
}
