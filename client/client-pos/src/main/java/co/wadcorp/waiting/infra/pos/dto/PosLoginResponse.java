package co.wadcorp.waiting.infra.pos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PosLoginResponse {

  private String accessToken;
  private String refreshToken;

  public PosLoginResponse() {
  }

}
