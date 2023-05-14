package co.wadcorp.waiting.api.controller.settings;

import co.wadcorp.waiting.api.controller.settings.dto.request.MemoKeywordCreateRequest;
import co.wadcorp.waiting.api.controller.settings.dto.request.MemoKeywordOrderingRequest;
import co.wadcorp.waiting.api.controller.settings.dto.request.MemoKeywordUpdateRequest;
import co.wadcorp.waiting.api.service.settings.MemoSettingsApiService;
import co.wadcorp.waiting.api.service.settings.dto.response.MemoKeywordResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.MemoKeywordListResponse;
import co.wadcorp.waiting.data.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemoSettingsController {

  private final MemoSettingsApiService memoSettingsApiService;

  /**
   * 매장별 메모 키워드 리스트 조회(odering 순으로)
   */
  @GetMapping("/api/v1/shops/{shopId}/settings/memo/keywords")
  public ApiResponse<MemoKeywordListResponse> getMemoKeywords(@PathVariable String shopId) {
    return ApiResponse.ok(memoSettingsApiService.getMemoKeywords(shopId));
  }

  /**
   * 메모 키워드 단건 조회
   */
  @GetMapping("/api/v1/shops/{shopId}/settings/memo/keywords/{keywordId}")
  public ApiResponse<MemoKeywordResponse> getMemoKeyword(@PathVariable String shopId,
      @PathVariable String keywordId) {
    return ApiResponse.ok(memoSettingsApiService.getMemoKeyword(keywordId));
  }

  /**
   * 메모 키워드 단건 생성
   */
  @PostMapping("/api/v1/shops/{shopId}/settings/memo/keywords")
  public ApiResponse<MemoKeywordResponse> createMemoKeyword(@PathVariable String shopId,
      @Valid @RequestBody MemoKeywordCreateRequest request) {
    return ApiResponse.ok(
        memoSettingsApiService.create(shopId, request.toServiceRequest())
    );
  }

  /**
   * 메모 키워드 단건 수정
   */
  @PostMapping("/api/v1/shops/{shopId}/settings/memo/keywords/{keywordId}/update")
  public ApiResponse<MemoKeywordResponse> updateMemoKeyword(@PathVariable String shopId,
      @PathVariable String keywordId, @Valid @RequestBody MemoKeywordUpdateRequest request) {
    return ApiResponse.ok(
        memoSettingsApiService.update(keywordId, request.toServiceRequest())
    );
  }

  /**
   * 메모 키워드 단건 삭제
   */
  @PostMapping("/api/v1/shops/{shopId}/settings/memo/keywords/{keywordId}/delete")
  public ApiResponse<?> deleteMemoKeyword(@PathVariable String shopId,
      @PathVariable String keywordId) {
    memoSettingsApiService.delete(keywordId);
    return ApiResponse.ok();
  }

  /**
   * 메모 키워드 순서 변경
   */
  @PostMapping("/api/v1/shops/{shopId}/settings/memo/keywords/ordering")
  public ApiResponse<MemoKeywordListResponse> orderingMemoKeywords(
      @PathVariable String shopId, @Valid @RequestBody MemoKeywordOrderingRequest request) {
    return ApiResponse.ok(
        memoSettingsApiService.updateMemoKeywordsOrdering(shopId, request.toServiceRequest())
    );
  }

}
