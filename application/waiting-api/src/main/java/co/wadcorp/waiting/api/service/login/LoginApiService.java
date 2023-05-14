package co.wadcorp.waiting.api.service.login;

import co.wadcorp.waiting.api.model.login.LoginRefreshRequest;
import co.wadcorp.waiting.api.model.login.LoginRequest;
import co.wadcorp.waiting.api.model.login.LoginResponse;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.infra.pos.CatchtablePosLoginClient;
import co.wadcorp.waiting.infra.pos.dto.PosApiResponse;
import co.wadcorp.waiting.infra.pos.dto.PosLoginRefreshRequest;
import co.wadcorp.waiting.infra.pos.dto.PosLoginRequest;
import co.wadcorp.waiting.infra.pos.dto.PosLoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginApiService {

  public static final String SERVICE_TYPE_WAITING = "WAITING";

  private final CatchtablePosLoginClient posClient;

  public LoginResponse login(LoginRequest request) {
    PosApiResponse<PosLoginResponse> response = posClient.login(
        new PosLoginRequest(SERVICE_TYPE_WAITING, request.userId(), request.userPw()));

    if (response.httpStatus() == HttpStatus.CONFLICT) {
      throw new AppException(HttpStatus.CONFLICT, response.getMessage(),
          response.getDisplayMessage(), response.getReason());
    }

    PosLoginResponse login = response.getData();
    return new LoginResponse(login.getAccessToken(), login.getRefreshToken());
  }

  public LoginResponse refresh(LoginRefreshRequest request) {
    PosApiResponse<PosLoginResponse> response = posClient.refresh(
        new PosLoginRefreshRequest(request.refreshToken()));

    if (response.httpStatus() == HttpStatus.CONFLICT) {
      throw new AppException(HttpStatus.CONFLICT, response.getMessage(),
          response.getDisplayMessage(), response.getReason());
    }

    PosLoginResponse login = response.getData();
    return new LoginResponse(login.getAccessToken(), login.getRefreshToken());
  }


}
