package co.wadcorp.waiting.infra.pos;

import co.wadcorp.waiting.infra.pos.dto.PosApiResponse;
import co.wadcorp.waiting.infra.pos.dto.PosSearchShopsResponse;
import co.wadcorp.waiting.infra.pos.dto.PosShopResponse;
import co.wadcorp.waiting.infra.pos.dto.PosShopSearchRequest;
import co.wadcorp.waiting.infra.pos.dto.PosShopsResponse;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(contentType = MediaType.APPLICATION_JSON_VALUE)
interface CatchtablePosShopHttpClient {

  @GetExchange(url = "/catchpos/api/v1/shops")
  PosApiResponse<PosShopsResponse> getShops(@RequestHeader Map<String, Object> header);

  @GetExchange(url = "/catchpos/api/v1/shops/{shopId}")
  PosApiResponse<PosShopResponse> getShop(@PathVariable("shopId") String shopId,
      @RequestHeader Map<String, Object> header);

  /**
   * 내부 시스템간 호출을 사용하여 매장 정보를 얻는다.
   *
   * @param shopId POS 매장 아이디
   */
  @GetExchange(url = "/internal/catchpos/api/v1/shops/{shopId}")
  PosApiResponse<PosShopResponse> getShopForInternal(@PathVariable("shopId") String shopId);


  @PostExchange(url = "/internal/catchpos/api/v1/shops/search")
  PosApiResponse<PosSearchShopsResponse> searchShops(@RequestBody PosShopSearchRequest posShopSearchRequest);
}

