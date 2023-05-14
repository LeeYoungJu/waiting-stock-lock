package co.wadcorp.waiting.api.controller.login;

import co.wadcorp.waiting.api.model.login.LoginResponse;
import co.wadcorp.waiting.data.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LogoutController {

  /**
   * 로그아웃 API - interface
   * TODO 포스에서 로그아웃 API가 준비되면 토큰을 만료할 수 있게 연동을 진행한다.
   *
   * @return
   */
  @PostMapping("/api/logout")
  public ApiResponse<LoginResponse> logout() {
    return ApiResponse.ok();

  }

}
