package co.wadcorp.waiting.data.domain.memo;

import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.data.support.BooleanYnConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "seq", callSuper = false)
@Table(name = "cw_waiting_memo")
@Entity
public class WaitingMemoEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "shop_id")
  private String shopId;

  @Column(name = "waiting_id", unique = true)
  private String waitingId;

  @Column(name = "memo")
  private String memo;

  @Builder
  private WaitingMemoEntity(String shopId, String waitingId, String memo) {
    this.shopId = shopId;
    this.waitingId = waitingId;
    this.memo = memo;
  }

  public void updateMemo(String memo) {
    this.memo = memo;
  }

}
