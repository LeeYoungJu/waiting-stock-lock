package co.wadcorp.waiting.api.controller.notice;

import co.wadcorp.waiting.api.model.notice.request.ReadNoticeRequest;
import co.wadcorp.waiting.api.model.notice.response.NoticeResponse;
import co.wadcorp.waiting.api.service.notice.NoticeApiService;
import co.wadcorp.waiting.data.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NoticeController {

  private final NoticeApiService noticeApiService;


  @GetMapping("/api/v1/notice/{shopId}")
  public ApiResponse<NoticeResponse> getNoticeList(@PathVariable String shopId) {
    return ApiResponse.ok(noticeApiService.getNoticeList(shopId));
  }

  @PostMapping("/api/v1/notice/read/{shopId}")
  public ApiResponse<?> saveReadNotice(@PathVariable String shopId, @RequestBody ReadNoticeRequest readNoticeRequest) {
    noticeApiService.saveNoticeRead(shopId, readNoticeRequest);
    return ApiResponse.ok();
  }

}
