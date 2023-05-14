package co.wadcorp.waiting.api.service.notice;

import co.wadcorp.waiting.api.model.notice.request.ReadNoticeRequest;
import co.wadcorp.waiting.api.model.notice.response.NoticeResponse;
import co.wadcorp.waiting.data.query.notice.NoticeEntityQueryRepository;
import co.wadcorp.waiting.data.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeApiService {

  private final NoticeService noticeService;

  private final NoticeEntityQueryRepository noticeEntityQueryRepository;


  @Transactional(readOnly = true)
  public NoticeResponse getNoticeList(String shopId) {
    var list = noticeEntityQueryRepository.findAllExposedNotice(shopId);
    return NoticeResponse.toDto(list);
  }

  @Transactional
  public void saveNoticeRead(String shopId, ReadNoticeRequest readNoticeRequest) {
    noticeService.saveNoticeRead(shopId, readNoticeRequest.getNoticeSeqList());
  }

}
