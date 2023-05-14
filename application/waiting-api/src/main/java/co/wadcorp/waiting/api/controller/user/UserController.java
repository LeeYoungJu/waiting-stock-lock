package co.wadcorp.waiting.api.controller.user;

import co.wadcorp.waiting.api.controller.user.dto.UpdateEmailRequest;
import co.wadcorp.waiting.api.controller.user.dto.UpdatePasswordRequest;
import co.wadcorp.waiting.api.controller.user.dto.UpdatePhoneNumberConfirmRequest;
import co.wadcorp.waiting.api.controller.user.dto.UpdatePhoneNumberRequest;
import co.wadcorp.waiting.api.controller.user.dto.UserResponse;
import co.wadcorp.waiting.api.service.user.UserApiService;
import co.wadcorp.waiting.data.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {

  private final UserApiService userApiService;


  /**
   * 계정 정보 조회
   *
   * @return
   */
  @GetMapping("/api/v1/shops/{shopId}/user")
  public ApiResponse<UserResponse> userInfo(@RequestHeader("X-CTM-AUTH") String ctmAuth,
      @PathVariable String shopId) {
    return ApiResponse.ok(userApiService.getUserInfo(shopId, ctmAuth));
  }


  /**
   * 비밀번호 변경
   *
   * @return
   */
  @PostMapping("/api/v1/user/password/update")
  public ApiResponse<?> updatePassword(@RequestHeader("X-CTM-AUTH") String ctmAuth,
      @RequestBody UpdatePasswordRequest passwordRequest) {

    userApiService.updatePassword(passwordRequest.getOldPassword(),
        passwordRequest.getNewPassword(), ctmAuth);

    return ApiResponse.ok();
  }


  /**
   * 이메일 변경 신청
   *
   * @return
   */
  @PostMapping("/api/v1/user/update-email")
  public ApiResponse<?> updateEmail(@RequestHeader("X-CTM-AUTH") String ctmAuth,
      @RequestBody UpdateEmailRequest request) {
    userApiService.updateEmail(request.getNewEmail(), ctmAuth);
    return ApiResponse.ok();
  }


  /**
   * 연락처 변경 신청
   *
   * @return
   */
  @PostMapping("/api/v1/user/update-phone/request")
  public ApiResponse<?> updatePhoneRequest(@RequestHeader("X-CTM-AUTH") String ctmAuth,
      @RequestBody UpdatePhoneNumberRequest request) {
    userApiService.updatePhoneRequest(request.getPhoneNumber(), ctmAuth);
    return ApiResponse.ok();
  }

  /**
   * 연락처 변경 인증
   *
   * @return
   */
  @PostMapping("/api/v1/user/update-phone/confirm")
  public ApiResponse<?> updatePhoneConfirm(@RequestHeader("X-CTM-AUTH") String ctmAuth,
      @RequestBody UpdatePhoneNumberConfirmRequest request) {
    userApiService.updatePhoneConfirm(request.getPhoneNumber(), request.getCertNo(), ctmAuth);
    return ApiResponse.ok();
  }

}
