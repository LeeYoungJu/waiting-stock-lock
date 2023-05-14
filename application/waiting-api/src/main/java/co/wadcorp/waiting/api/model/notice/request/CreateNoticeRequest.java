package co.wadcorp.waiting.api.model.notice.request;

import co.wadcorp.waiting.data.domain.notice.NoticeEntity;
import co.wadcorp.waiting.data.domain.notice.NoticeType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
public class CreateNoticeRequest {

  private String noticeType;
  private boolean isNoticeAlways;
  private boolean isEnable;
  private String noticeTitle;
  private String noticePreview;
  private String noticeContent;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private ZonedDateTime openDateTime;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private ZonedDateTime closeDateTime;


  private NoticeEntity.NoticeEntityBuilder noticeEntityBuilder() {
    return NoticeEntity.builder()
        .noticeType(NoticeType.valueOf(noticeType))
        .isNoticeAlways(isNoticeAlways)
        .isEnable(isEnable)
        .openDateTime(openDateTime)
        .closeDateTime(closeDateTime)
        .noticeTitle(noticeTitle)
        .noticePreview(noticePreview)
        .noticeContent(noticeContent);
  }

  public NoticeEntity toEntity() {
    return noticeEntityBuilder().build();
  }

  public NoticeEntity toEntity(Long noticeSeq) {
    return noticeEntityBuilder()
        .seq(noticeSeq)
        .build();
  }

}
