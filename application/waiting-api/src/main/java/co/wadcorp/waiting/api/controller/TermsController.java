package co.wadcorp.waiting.api.controller;

import co.wadcorp.waiting.api.model.waiting.response.TermsResponse;
import co.wadcorp.waiting.api.service.waiting.WaitingTermsApiService;
import co.wadcorp.waiting.data.api.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TermsController {

  private final WaitingTermsApiService waitingTermsApiService;

  /**
   * 웨이팅 이용약관 목록 조회
   *
   * @return 웨이팅 이용약관 리스트
   */
  @GetMapping(value = "/api/v1/waiting/terms")
  public ApiResponse<List<TermsResponse>> getAllWaitingTerms() {
    return ApiResponse.ok(waitingTermsApiService.getAllWaitingTerms());
  }
}
