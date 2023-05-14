package co.wadcorp.waiting.infra.pos;

import co.wadcorp.waiting.infra.pos.dto.PosApiResponse;
import co.wadcorp.waiting.infra.pos.dto.PosApiResponse.Reason;
import co.wadcorp.waiting.infra.pos.dto.PosSearchShopsResponse;
import co.wadcorp.waiting.infra.pos.dto.PosShopResponse;
import co.wadcorp.waiting.infra.pos.dto.PosShopSearchRequest;
import co.wadcorp.waiting.infra.pos.dto.PosShopsResponse;
import co.wadcorp.waiting.infra.pos.util.AuthHeaderUtils;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
public class CatchtablePosShopClient {

  public static final ParameterizedTypeReference<PosApiResponse<Reason>> POS_API_RESPONSE_PARAMETERIZED_TYPE_REFERENCE = new ParameterizedTypeReference<>() {
  };
  private final CatchtablePosShopHttpClient posShopHttpClient;

  /**
   * Catchtable Pos - 매장 목록 조회 API
   * @return
   */
  public PosApiResponse<PosShopsResponse> getShops(String ctmAuth) {
    try {
      Map<String, Object> header = AuthHeaderUtils.createAuthHeader(ctmAuth);

      return posShopHttpClient.getShops(header);
    } catch (WebClientResponseException e) {
      return PosApiResponse.failed(e.getStatusCode());
    } catch (Exception e) {
      return PosApiResponse.failed(e);
    }
  }

  /**
   * Catchtable Pos - 단일 매장 조회 API
   *
   * @param shopId
   * @param ctmAuth
   * @return
   */
  public PosApiResponse<PosShopResponse> getShop(String shopId, String ctmAuth) {
    try {
      Map<String, Object> header = AuthHeaderUtils.createAuthHeader(ctmAuth);

      return posShopHttpClient.getShop(shopId, header);
    } catch (WebClientResponseException e) {
      return PosApiResponse.failed(e.getStatusCode());
    } catch (Exception e) {
      return PosApiResponse.failed(e);
    }
  }

  /**
   * Catchtable Pos - 매장 검색
   *
   * @return
   */
  public PosApiResponse<PosSearchShopsResponse> searchShops(PosShopSearchRequest request) {
    try {

      return posShopHttpClient.searchShops(request);
    } catch (WebClientResponseException e) {
      return PosApiResponse.failed(e.getStatusCode());
    } catch (Exception e) {
      return PosApiResponse.failed(e);
    }
  }
  /**
   * Catchtable Pos - 단일 매장 조회 API INTERNAL 호출
   */
  public PosApiResponse<PosShopResponse> getShopForInternal(String shopId) {
    try {
      return posShopHttpClient.getShopForInternal(shopId);
    } catch (WebClientResponseException e) {
      return PosApiResponse.failed(e.getStatusCode());
    } catch (Exception e) {
      return PosApiResponse.failed(e);
    }
  }
}

