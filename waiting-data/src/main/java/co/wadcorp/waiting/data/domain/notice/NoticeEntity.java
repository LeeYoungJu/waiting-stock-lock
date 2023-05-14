package co.wadcorp.waiting.data.domain.notice;

import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.data.support.BooleanYnConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "cw_notice")
public class NoticeEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long seq;

  @Column(name = "notice_type")
  @Enumerated(EnumType.STRING)
  private NoticeType noticeType;

  @Column(name = "notice_always_yn", columnDefinition = "CHAR")
  @Convert(converter = BooleanYnConverter.class)
  private Boolean isNoticeAlways;

  @Column(name = "enable_yn", columnDefinition = "CHAR")
  @Convert(converter = BooleanYnConverter.class)
  private Boolean isEnable;

  @Column(name = "open_date_time")
  private ZonedDateTime openDateTime;

  @Column(name = "close_date_time")
  private ZonedDateTime closeDateTime;

  @Column(name = "notice_title")
  private String noticeTitle;

  @Column(name = "notice_preview")
  private String noticePreview;

  @Column(name = "notice_content")
  private String noticeContent;

}
