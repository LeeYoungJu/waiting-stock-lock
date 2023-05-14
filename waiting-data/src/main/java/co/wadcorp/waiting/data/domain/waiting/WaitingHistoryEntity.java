package co.wadcorp.waiting.data.domain.waiting;

import co.wadcorp.waiting.data.support.BaseHistoryEntity;
import co.wadcorp.waiting.data.support.PersonOptionsConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name = "cw_waiting_history")
public class WaitingHistoryEntity extends BaseHistoryEntity {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "waiting_seq")
  private Long waitingSeq;

  @Column(name = "shop_id")
  private String shopId;

  @Column(name = "waiting_id")
  private String waitingId;

  @Column(name = "operation_date")
  private LocalDate operationDate;

  @Column(name = "register_channel")
  @Enumerated(EnumType.STRING)
  private RegisterChannel registerChannel;

  @Column(name = "customer_seq")
  private Long customerSeq;

  @Column(name = "customer_name")
  private String customerName;

  @Embedded
  private WaitingNumber waitingNumbers;

  @Column(name = "waiting_status")
  @Enumerated(EnumType.STRING)
  private WaitingStatus waitingStatus;

  @Column(name = "waiting_detail_status")
  @Enumerated(EnumType.STRING)
  private WaitingDetailStatus waitingDetailStatus;

  @Column(name = "remark")
  private String remark;

  @Column(name = "seat_option_name")
  private String seatOptionName;

  @Column(name = "total_person_count")
  private Integer totalPersonCount;

  @Column(name = "person_options", columnDefinition = "text")
  @Convert(converter = PersonOptionsConverter.class)
  private PersonOptionsData personOptionsData;

  @Column(name = "expected_sitting_date_time")
  private ZonedDateTime expectedSittingDateTime;

  @Column(name = "waiting_complete_date_time")
  private ZonedDateTime waitingCompleteDateTime;

  @Column(name = "ip")
  private String ip;

  @Builder
  public WaitingHistoryEntity(Long seq, Long waitingSeq, String shopId, String waitingId,
      LocalDate operationDate, RegisterChannel registerChannel, Long customerSeq,
      String customerName,
      WaitingNumber waitingNumbers, WaitingStatus waitingStatus,
      WaitingDetailStatus waitingDetailStatus, String remark, String seatOptionName,
      Integer totalPersonCount, PersonOptionsData personOptionsData,
      ZonedDateTime expectedSittingDateTime, ZonedDateTime waitingCompleteDateTime, String ip) {
    this.seq = seq;
    this.waitingSeq = waitingSeq;
    this.shopId = shopId;
    this.waitingId = waitingId;
    this.operationDate = operationDate;
    this.registerChannel = registerChannel;
    this.customerSeq = customerSeq;
    this.customerName = customerName;
    this.waitingNumbers = waitingNumbers;
    this.waitingStatus = waitingStatus;
    this.waitingDetailStatus = waitingDetailStatus;
    this.remark = remark;
    this.seatOptionName = seatOptionName;
    this.totalPersonCount = totalPersonCount;
    this.personOptionsData = personOptionsData;
    this.expectedSittingDateTime = expectedSittingDateTime;
    this.waitingCompleteDateTime = waitingCompleteDateTime;
    this.ip = ip;
  }

  public WaitingHistoryEntity(WaitingEntity waiting) {
    this.waitingSeq = waiting.getSeq();
    this.shopId = waiting.getShopId();
    this.waitingId = waiting.getWaitingId();
    this.operationDate = waiting.getOperationDate();
    this.registerChannel = waiting.getRegisterChannel();
    this.customerSeq = waiting.getCustomerSeq();
    this.customerName = waiting.getCustomerName();
    this.waitingNumbers = waiting.getWaitingNumbers();
    this.waitingStatus = waiting.getWaitingStatus();
    this.waitingDetailStatus = waiting.getWaitingDetailStatus();
    this.remark = waiting.getRemark();
    this.seatOptionName = waiting.getSeatOptionName();
    this.totalPersonCount = waiting.getTotalPersonCount();
    this.personOptionsData = waiting.getPersonOptionsData();
    this.expectedSittingDateTime = waiting.getExpectedSittingDateTime();
    this.waitingCompleteDateTime = waiting.getWaitingCompleteDateTime();
//    this.ip = ip;
  }

  @Builder
  private WaitingHistoryEntity(Long waitingSeq, String shopId, String waitingId,
      LocalDate operationDate, RegisterChannel registerChannel, Long customerSeq,
      String customerName, WaitingNumber waitingNumbers, WaitingStatus waitingStatus,
      WaitingDetailStatus waitingDetailStatus, String remark, String seatOptionName,
      Integer totalPersonCount, PersonOptionsData personOptionsData,
      ZonedDateTime expectedSittingDateTime, ZonedDateTime waitingCompleteDateTime) {
    this.waitingSeq = waitingSeq;
    this.shopId = shopId;
    this.waitingId = waitingId;
    this.operationDate = operationDate;
    this.registerChannel = registerChannel;
    this.customerSeq = customerSeq;
    this.customerName = customerName;
    this.waitingNumbers = waitingNumbers;
    this.waitingStatus = waitingStatus;
    this.waitingDetailStatus = waitingDetailStatus;
    this.remark = remark;
    this.seatOptionName = seatOptionName;
    this.totalPersonCount = totalPersonCount;
    this.personOptionsData = personOptionsData;
    this.expectedSittingDateTime = expectedSittingDateTime;
    this.waitingCompleteDateTime = waitingCompleteDateTime;
  }

  public Integer getWaitingNumber() {
    return this.waitingNumbers.getWaitingNumber();
  }

  public Integer getWaitingOrder() {
    return this.waitingNumbers.getWaitingOrder();
  }

  public String getOption() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.personOptionsData.getPersonOptionText());

    if (StringUtils.isNotEmpty(this.seatOptionName)) {
      sb.append(" / ").append(this.seatOptionName);
    }
    return sb.toString();
  }
}
