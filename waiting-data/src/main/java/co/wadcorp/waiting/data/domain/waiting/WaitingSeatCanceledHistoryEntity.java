package co.wadcorp.waiting.data.domain.waiting;

import co.wadcorp.waiting.data.support.BaseHistoryEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name = "cw_waiting_seat_canceled_history")
public class WaitingSeatCanceledHistoryEntity extends BaseHistoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "seat_waiting_seq")
  private Long seatWaitingSeq;

  @Column(name = "canceled_waiting_seq")
  private Long canceledWaitingSeq;

  @Column(name = "canceled_waiting_team_count")
  private Integer canceledWaitingTeamCount;

  @Column(name = "canceled_waiting_expected_waiting_period")
  private Integer canceledWaitingExpectedWaitingPeriod;

  @Builder
  private WaitingSeatCanceledHistoryEntity(Long seatWaitingSeq, Long canceledWaitingSeq,
      Integer canceledWaitingTeamCount, Integer canceledWaitingExpectedWaitingPeriod) {
    this.seatWaitingSeq = seatWaitingSeq;
    this.canceledWaitingSeq = canceledWaitingSeq;
    this.canceledWaitingTeamCount = canceledWaitingTeamCount;
    this.canceledWaitingExpectedWaitingPeriod = canceledWaitingExpectedWaitingPeriod;
  }
}
