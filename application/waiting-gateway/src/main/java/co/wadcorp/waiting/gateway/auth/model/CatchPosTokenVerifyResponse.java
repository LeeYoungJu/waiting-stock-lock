package co.wadcorp.waiting.gateway.auth.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 캐치테이블 포스용 인증 유효성 검사 결과 클래스.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CatchPosTokenVerifyResponse implements Serializable {

  private boolean isValid;
  private String invalidMessage;
  private Long userSeq;
  private boolean isAdmin;

  public String getUserType() {
    return isAdmin ? "ADMIN" : "USER";
  }
}
