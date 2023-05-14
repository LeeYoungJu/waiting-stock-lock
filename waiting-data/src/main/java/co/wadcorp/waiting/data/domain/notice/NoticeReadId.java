package co.wadcorp.waiting.data.domain.notice;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class NoticeReadId implements Serializable {

  @Column(name = "shop_id")
  private String shopId;

  @Column(name = "notice_seq")
  private Long noticeSeq;


  NoticeReadId(String shopId, Long noticeSeq) {
    this.shopId = shopId;
    this.noticeSeq = noticeSeq;
  }

}
