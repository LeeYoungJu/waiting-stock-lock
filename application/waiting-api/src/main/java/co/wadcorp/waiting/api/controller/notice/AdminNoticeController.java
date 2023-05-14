package co.wadcorp.waiting.api.controller.notice;

import co.wadcorp.waiting.api.model.notice.request.CreateNoticeRequest;
import co.wadcorp.waiting.api.model.notice.response.AdminNoticeResponse;
import co.wadcorp.waiting.api.service.notice.AdminNoticeApiService;
import co.wadcorp.waiting.data.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminNoticeController {

  private final AdminNoticeApiService noticeAdminApiService;


  @GetMapping("/admin/v1/notice")
  public ApiResponse<AdminNoticeResponse> getNoticeList() {
    return ApiResponse.ok(noticeAdminApiService.getNoticeList());
  }

  @GetMapping("/admin/v1/notice/{noticeSeq}")
  public ApiResponse<AdminNoticeResponse> getNotice(@PathVariable Long noticeSeq) {
    return ApiResponse.ok(noticeAdminApiService.getNotice(noticeSeq));
  }


  @PostMapping("/admin/v1/notice")
  public ApiResponse<AdminNoticeResponse> insertNotice(@RequestBody CreateNoticeRequest noticeRequest) {
    return ApiResponse.ok(noticeAdminApiService.insertNotice(noticeRequest));
  }

  @PutMapping("/admin/v1/notice/{noticeSeq}")
  public ApiResponse<AdminNoticeResponse> updateNotice(@PathVariable Long noticeSeq,
      @RequestBody CreateNoticeRequest noticeRequest) {
    return ApiResponse.ok(noticeAdminApiService.updateNotice(noticeSeq, noticeRequest));
  }

  @DeleteMapping("/admin/v1/notice/{noticeSeq}")
  public ApiResponse<?> deleteNotice(@PathVariable Long noticeSeq) {
    noticeAdminApiService.deleteNotice(noticeSeq);
    return ApiResponse.ok();
  }

}
