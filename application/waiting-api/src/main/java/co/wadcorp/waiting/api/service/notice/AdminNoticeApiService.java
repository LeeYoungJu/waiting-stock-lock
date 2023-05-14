package co.wadcorp.waiting.api.service.notice;

import co.wadcorp.waiting.api.model.notice.request.CreateNoticeRequest;
import co.wadcorp.waiting.api.model.notice.response.AdminNoticeResponse;
import co.wadcorp.waiting.data.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminNoticeApiService {

  private final NoticeService noticeService;


  @Transactional
  public AdminNoticeResponse getNoticeList() {
    return AdminNoticeResponse.toDto(noticeService.getNoticeList());
  }

  @Transactional
  public AdminNoticeResponse getNotice(Long noticeSeq) {
    return AdminNoticeResponse.toDto(noticeService.getNotice(noticeSeq));
  }

  @Transactional
  public AdminNoticeResponse insertNotice(CreateNoticeRequest noticeRequest) {
    return AdminNoticeResponse.toDto(noticeService.saveNotice(noticeRequest.toEntity()));
  }

  @Transactional
  public AdminNoticeResponse updateNotice(Long noticeSeq, CreateNoticeRequest noticeRequest) {
    return AdminNoticeResponse.toDto(noticeService.saveNotice(noticeRequest.toEntity(noticeSeq)));
  }

  @Transactional
  public void deleteNotice(Long noticeSeq) {
    noticeService.deleteNotice(noticeSeq);
  }

}
