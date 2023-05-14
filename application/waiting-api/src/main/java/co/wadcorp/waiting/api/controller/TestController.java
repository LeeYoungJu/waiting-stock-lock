package co.wadcorp.waiting.api.controller;

import co.wadcorp.waiting.data.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 테스트용 컨트롤러. 로컬 및 개발 환경에서만 접근하게 해야 함
 */
@RestController
public class TestController {

  @GetMapping("/test/current")
  public ApiResponse<String> getCurrent() {
    return ApiResponse.ok(String.valueOf(System.currentTimeMillis()));
  }
}
