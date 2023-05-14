package co.wadcorp.waiting.data.domain.memo;

import co.wadcorp.waiting.data.support.BaseHistoryEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cw_waiting_memo_history",
    indexes = {
        @Index(name = "cw_waiting_memo_history_waiting_memo_seq_index", columnList = "waiting_memo_seq"),
        @Index(name = "cw_waiting_memo_history_waiting_id_index", columnList = "waiting_id")
    }
)
@Entity
public class WaitingMemoHistoryEntity extends BaseHistoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "waiting_memo_seq")
  private Long waitingMemoSeq;

  @Column(name = "shop_id")
  private String shopId;

  @Column(name = "waiting_id")
  private String waitingId;

  @Column(name = "memo")
  private String memo;

  @Builder
  private WaitingMemoHistoryEntity(Long waitingMemoSeq, String shopId, String waitingId,
      String memo) {
    this.waitingMemoSeq = waitingMemoSeq;
    this.shopId = shopId;
    this.waitingId = waitingId;
    this.memo = memo;
  }

  public static WaitingMemoHistoryEntity of(WaitingMemoEntity entity) {
    return WaitingMemoHistoryEntity.builder()
        .waitingMemoSeq(entity.getSeq())
        .shopId(entity.getShopId())
        .waitingId(entity.getWaitingId())
        .memo(entity.getMemo())
        .build();
  }

}
