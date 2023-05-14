package co.wadcorp.waiting.data.domain.notice;

import com.querydsl.core.annotations.QueryProjection;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

@Getter
@Setter
public class NoticeVO implements Serializable {

  private Long seq;

  private String noticeType;
  private String noticeTitle;
  private String noticePreview;
  private String noticeContent;
  private String regDateTime;
  private Boolean isRead;


  private final static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

  @QueryProjection
  public NoticeVO(Long seq, NoticeType noticeType, String noticeTitle, String noticePreview, String noticeContent,
      ZonedDateTime regDateTime, NoticeReadId isRead) {
    this.seq = seq;
    this.noticeType = noticeType.getValue();
    this.noticeTitle = noticeTitle;
    this.noticePreview = noticePreview;
    this.noticeContent = noticeContent;
    this.regDateTime = regDateTime.format(dateFormatter);
    this.isRead = !ObjectUtils.isEmpty(isRead);
  }

}
