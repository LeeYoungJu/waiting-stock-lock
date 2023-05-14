package co.wadcorp.waiting.api.model.notice.response;

import static co.wadcorp.libs.stream.StreamUtils.convert;

import co.wadcorp.libs.stream.StreamUtils;
import co.wadcorp.waiting.data.domain.notice.NoticeEntity;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminNoticeResponse {

  private List<Notice> noticeList;


  public static AdminNoticeResponse toDto(List<NoticeEntity> noticeEntityList) {
    return new AdminNoticeResponse(convert(noticeEntityList, AdminNoticeResponse::convertNotice));
  }

  public static AdminNoticeResponse toDto(NoticeEntity notice) {
    return toDto(List.of(notice));
  }


  private final static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

  private static Notice convertNotice(NoticeEntity notice) {
    return Notice.builder()
        .seq(notice.getSeq())
        .noticeType(notice.getNoticeType().getValue())
        .noticeTitle(notice.getNoticeTitle())
        .noticePreview(notice.getNoticePreview())
        .noticeContent(notice.getNoticeContent())
        .regDateTime(notice.getRegDateTime())
        .modDateTime(notice.getModDateTime())
        .isNoticeAlways(notice.getIsNoticeAlways())
        .isEnable(notice.getIsEnable())
        .openDateTime(notice.getOpenDateTime())
        .build();
  }

  public static class Notice {

    private Long seq;
    private String noticeType;
    private String noticeTitle;
    private String noticePreview;
    private String noticeContent;
    private ZonedDateTime regDateTime;
    private ZonedDateTime modDateTime;

    private boolean isNoticeAlways;
    private boolean isEnable;
    private ZonedDateTime openDateTime;
    private ZonedDateTime closeDateTime;

    @Builder
    public Notice(Long seq, String noticeType, String noticeTitle, String noticePreview, String noticeContent,
        ZonedDateTime regDateTime, ZonedDateTime modDateTime, boolean isNoticeAlways, boolean isEnable,
        ZonedDateTime openDateTime, ZonedDateTime closeDateTime) {
      this.seq = seq;
      this.noticeType = noticeType;
      this.noticeTitle = noticeTitle;
      this.noticePreview = noticePreview;
      this.noticeContent = noticeContent;
      this.regDateTime = regDateTime;
      this.modDateTime = modDateTime;
      this.isNoticeAlways = isNoticeAlways;
      this.isEnable = isEnable;
      this.openDateTime = openDateTime;
      this.closeDateTime = closeDateTime;
    }
  }

}
