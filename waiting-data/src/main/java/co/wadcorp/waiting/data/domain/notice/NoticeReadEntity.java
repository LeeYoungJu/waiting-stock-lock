package co.wadcorp.waiting.data.domain.notice;

import co.wadcorp.waiting.data.support.BaseHistoryEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "cw_notice_read")
public class NoticeReadEntity extends BaseHistoryEntity {

  @EmbeddedId
  private NoticeReadId noticeReadId;

  public NoticeReadEntity(String shopId, Long noticeSeq) {
    this.noticeReadId = new NoticeReadId(shopId, noticeSeq);
  }

}
