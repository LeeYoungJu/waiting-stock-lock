package co.wadcorp.waiting.api.controller.login;

import co.wadcorp.waiting.api.model.login.LoginRefreshRequest;
import co.wadcorp.waiting.api.model.login.LoginRequest;
import co.wadcorp.waiting.api.model.login.LoginResponse;
import co.wadcorp.waiting.api.service.login.LoginApiService;
import co.wadcorp.waiting.data.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

  private final LoginApiService loginApiService;

  /**
   * 로그인 API - with pos
   * @param request
   * @return
   */
  @PostMapping("/api/login")
  public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
    return ApiResponse.ok(loginApiService.login(request));

  }

  /**
   * 토큰 연장 API - with pos
   * @param request
   * @return
   */
  @PostMapping("/api/login/refresh")
  public ApiResponse<LoginResponse> refresh(@RequestBody LoginRefreshRequest request) {
    return ApiResponse.ok(loginApiService.refresh(request));
  }
}
