package co.wadcorp.waiting.infra.pos;

import co.wadcorp.waiting.infra.pos.dto.PosApiResponse;
import co.wadcorp.waiting.infra.pos.dto.PosLoginRefreshRequest;
import co.wadcorp.waiting.infra.pos.dto.PosLoginRequest;
import co.wadcorp.waiting.infra.pos.dto.PosLoginResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(contentType = MediaType.APPLICATION_JSON_VALUE)
interface CatchtablePosLoginHttpClient {

  @PostExchange(url = "/catchpos/api/login-user")
  PosApiResponse<PosLoginResponse> login(@RequestBody PosLoginRequest request);

  @PostExchange(url = "/catchpos/api/login-user/refresh")
  PosApiResponse<PosLoginResponse> refresh(@RequestBody PosLoginRefreshRequest request);
}

