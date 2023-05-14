package co.wadcorp.waiting.data.service.notice;

import co.wadcorp.waiting.data.domain.notice.NoticeEntity;
import co.wadcorp.waiting.data.domain.notice.NoticeReadEntity;
import co.wadcorp.waiting.data.domain.notice.NoticeReadRepository;
import co.wadcorp.waiting.data.domain.notice.NoticeRepository;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {

  private final NoticeRepository noticeRepository;
  private final NoticeReadRepository noticeReadRepository;


  public List<NoticeEntity> getNoticeList() {
    return noticeRepository.findAll();
  }

  public NoticeEntity getNotice(Long noticeSeq) {
    return noticeRepository.findById(noticeSeq)
        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_NOTICE));
  }

  public void saveNoticeRead(String shopId, List<Long> noticeSeqList) {
    noticeReadRepository.saveAll(
        noticeSeqList.stream().map(noticeSeq -> new NoticeReadEntity(shopId, noticeSeq)).toList());
  }

  public NoticeEntity saveNotice(NoticeEntity noticeEntity) {
    return noticeRepository.save(noticeEntity);
  }

  public void deleteNotice(Long noticeSeq) {
    noticeRepository.deleteById(noticeSeq);
  }

}
