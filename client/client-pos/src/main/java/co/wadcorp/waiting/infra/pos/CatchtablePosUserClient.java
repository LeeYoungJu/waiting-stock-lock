package co.wadcorp.waiting.infra.pos;

import co.wadcorp.waiting.infra.pos.dto.PosApiResponse;
import co.wadcorp.waiting.infra.pos.dto.PosApiResponse.Reason;
import co.wadcorp.waiting.infra.pos.dto.PosResultResponse;
import co.wadcorp.waiting.infra.pos.dto.PosUserResponse;
import co.wadcorp.waiting.infra.pos.dto.UpdatePosUserEmailRequest;
import co.wadcorp.waiting.infra.pos.dto.UpdatePosUserPasswordRequest;
import co.wadcorp.waiting.infra.pos.dto.UpdatePosUserPhoneNumberRequest;
import co.wadcorp.waiting.infra.pos.dto.UpdatePosUserPhoneNumberResponse;
import co.wadcorp.waiting.infra.pos.util.AuthHeaderUtils;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
public class CatchtablePosUserClient {

  public static final ParameterizedTypeReference<PosApiResponse<Reason>> POS_API_RESPONSE_PARAMETERIZED_TYPE_REFERENCE = new ParameterizedTypeReference<>() {
  };
  private final CatchtablePosUserHttpClient posUserHttpClient;

  /**
   * Catchtable Pos - 유저 정보 조회
   */
  public PosApiResponse<PosUserResponse> getUserInfo(String shopId, String ctmAuth) {
    try {
      Map<String, Object> header = AuthHeaderUtils.createAuthHeader(ctmAuth);

      return posUserHttpClient.getUserInfo(header, shopId);
    } catch (WebClientResponseException e) {

      if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
        return PosApiResponse.failed(e.getStatusCode());
      }

      PosApiResponse<Reason> responseBodyAs = e.getResponseBodyAs(
          POS_API_RESPONSE_PARAMETERIZED_TYPE_REFERENCE);

      return PosApiResponse.failed(responseBodyAs);
    } catch (Exception e) {
      return PosApiResponse.failed(e);
    }
  }

  /**
   * Catchtable Pos - 이메일 변경 요청
   */
  public PosApiResponse<PosResultResponse> updateEmailRequest(String newEmail, String ctmAuth) {
    try {
      Map<String, Object> header = AuthHeaderUtils.createAuthHeader(ctmAuth);

      return posUserHttpClient.updateEmailRequest(header, new UpdatePosUserEmailRequest(newEmail));
    } catch (WebClientResponseException e) {

      if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
        return PosApiResponse.failed(e.getStatusCode());
      }

      PosApiResponse<Reason> responseBodyAs = e.getResponseBodyAs(
          POS_API_RESPONSE_PARAMETERIZED_TYPE_REFERENCE);

      return PosApiResponse.failed(responseBodyAs);
    } catch (Exception e) {
      return PosApiResponse.failed(e);
    }
  }


  /**
   * Catchtable Pos - 비밀번호 변경 요청
   */
  public PosApiResponse<PosResultResponse> updatePassword(String oldPassword, String newPassword, String ctmAuth) {
    try {
      Map<String, Object> header = AuthHeaderUtils.createAuthHeader(ctmAuth);

      return posUserHttpClient.updatePassword(header, UpdatePosUserPasswordRequest.builder()
          .oldPassword(oldPassword)
          .newPassword(newPassword)
          .build());
    } catch (WebClientResponseException e) {

      if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
        return PosApiResponse.failed(e.getStatusCode());
      }

      PosApiResponse<Reason> responseBodyAs = e.getResponseBodyAs(
          POS_API_RESPONSE_PARAMETERIZED_TYPE_REFERENCE);

      return PosApiResponse.failed(responseBodyAs);
    } catch (Exception e) {
      return PosApiResponse.failed(e);
    }
  }


  /**
   * Catchtable Pos - 연락처 변경 요청
   */
  public PosApiResponse<PosResultResponse> updatePhoneNumberRequest(String phoneNumber, String ctmAuth) {
    try {
      Map<String, Object> header = AuthHeaderUtils.createAuthHeader(ctmAuth);

      return posUserHttpClient.updatePhoneNumberRequest(header, UpdatePosUserPhoneNumberRequest.builder()
          .phone(phoneNumber)
          .build());
    } catch (WebClientResponseException e) {

      if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
        return PosApiResponse.failed(e.getStatusCode());
      }

      PosApiResponse<Reason> responseBodyAs = e.getResponseBodyAs(
          POS_API_RESPONSE_PARAMETERIZED_TYPE_REFERENCE);

      return PosApiResponse.failed(responseBodyAs);
    } catch (Exception e) {
      return PosApiResponse.failed(e);
    }
  }


  /**
   * Catchtable Pos - 연락처 변경 인증
   *
   * @return
   */
  public PosApiResponse<UpdatePosUserPhoneNumberResponse> updatePhoneNumberConfirm(String phoneNumber,
      String certNo, String ctmAuth) {
    try {
      Map<String, Object> header = AuthHeaderUtils.createAuthHeader(ctmAuth);

      return posUserHttpClient.updatePhoneNumberConfirm(header, UpdatePosUserPhoneNumberRequest.builder()
          .phone(phoneNumber)
          .certNo(certNo)
          .build());
    } catch (WebClientResponseException e) {

      if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
        return PosApiResponse.failed(e.getStatusCode());
      }

      PosApiResponse<Reason> responseBodyAs = e.getResponseBodyAs(
          POS_API_RESPONSE_PARAMETERIZED_TYPE_REFERENCE);

      return PosApiResponse.failed(responseBodyAs);
    } catch (Exception e) {
      return PosApiResponse.failed(e);
    }
  }
}

