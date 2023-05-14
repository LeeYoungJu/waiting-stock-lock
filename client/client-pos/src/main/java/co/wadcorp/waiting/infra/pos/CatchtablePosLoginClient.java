package co.wadcorp.waiting.infra.pos;

import co.wadcorp.waiting.infra.pos.dto.PosApiResponse;
import co.wadcorp.waiting.infra.pos.dto.PosApiResponse.Reason;
import co.wadcorp.waiting.infra.pos.dto.PosLoginRefreshRequest;
import co.wadcorp.waiting.infra.pos.dto.PosLoginRequest;
import co.wadcorp.waiting.infra.pos.dto.PosLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
public class CatchtablePosLoginClient {

  public static final ParameterizedTypeReference<PosApiResponse<Reason>> POS_API_RESPONSE_PARAMETERIZED_TYPE_REFERENCE = new ParameterizedTypeReference<>() {
  };
  private final CatchtablePosLoginHttpClient posLoginHttpClient;

  /**
   * Catchtable Pos 로그인 API
   * @param request
   * @return
   */
  public PosApiResponse<PosLoginResponse> login(PosLoginRequest request) {

    try {
      return posLoginHttpClient.login(request);
    } catch (WebClientResponseException e) {
      PosApiResponse<Reason> responseBodyAs = e.getResponseBodyAs(POS_API_RESPONSE_PARAMETERIZED_TYPE_REFERENCE);

      return PosApiResponse.failed(responseBodyAs);
    } catch (Exception e) {

      return PosApiResponse.failed(e);
    }
  }

  /**
   * Catchtable Pos 토큰 연장 API
   * @param request
   * @return
   */
  public PosApiResponse<PosLoginResponse> refresh(PosLoginRefreshRequest request) {

    try {
      return posLoginHttpClient.refresh(request);
    } catch (WebClientResponseException e) {
      PosApiResponse<Reason> responseBodyAs = e.getResponseBodyAs(POS_API_RESPONSE_PARAMETERIZED_TYPE_REFERENCE);

      return PosApiResponse.failed(responseBodyAs);
    } catch (Exception e) {

      return PosApiResponse.failed(e);
    }
  }
}

