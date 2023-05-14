package co.wadcorp.waiting.data.domain.waiting;

import co.wadcorp.waiting.data.domain.waiting.validator.WaitingCancelValidator;
import co.wadcorp.waiting.data.domain.waiting.validator.WaitingSittingValidator;
import co.wadcorp.waiting.data.domain.waiting.validator.WaitingValidator;
import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.data.support.PersonOptionsConverter;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
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
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@Table(name = "cw_waiting")
public class WaitingEntity extends BaseEntity {

  public static final WaitingEntity EMPTY_WAITING_ENTITY = new WaitingEntity();
  private static final String DELIMITER = ", ";
  private static final Long CAN_UNDO_DURATION_MINUTE = 30L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

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

  @Builder
  public WaitingEntity(String shopId, String waitingId, LocalDate operationDate,
      RegisterChannel registerChannel, Long customerSeq, String customerName,
      WaitingNumber waitingNumbers, WaitingStatus waitingStatus,
      WaitingDetailStatus waitingDetailStatus, String remark,
      String seatOptionName, Integer totalPersonCount, PersonOptionsData personOptionsData,
      ZonedDateTime expectedSittingDateTime, ZonedDateTime waitingCompleteDateTime) {
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

  public List<PersonOption> getPersonOptions() {
    return this.personOptionsData.getPersonOptions();
  }

  public void calling(LocalDate operationDate, WaitingHistories histories) {
    WaitingValidator.validateCalling(this, operationDate, histories);
    this.waitingDetailStatus = WaitingDetailStatus.CALL;
  }

  public boolean isWaitingStatus() {
    return this.waitingStatus == WaitingStatus.WAITING;
  }

  public boolean isCanceled() {
    return this.waitingStatus == WaitingStatus.CANCEL;
  }

  public boolean isSitting() {
    return this.waitingStatus == WaitingStatus.SITTING;
  }

  public boolean isCancelStatus() {
    return this.waitingStatus == WaitingStatus.CANCEL;
  }

  public boolean isSameOperationDate(LocalDate operationDate) {
    return this.operationDate.isEqual(operationDate);
  }

  public boolean isSameSeatOptionName(String seatOptionName) {
    return this.seatOptionName.equals(seatOptionName);
  }

  public boolean isSameWaitingOrder(Integer waitingOrder) {
    return this.waitingNumbers.isSameWaitingOrder(waitingOrder);
  }

  public void sitting(LocalDate requestOperationDate) {
    WaitingSittingValidator.validate(this, requestOperationDate);

    WaitingDetailStatus sitting = WaitingDetailStatus.SITTING;
    this.waitingDetailStatus = sitting;
    this.waitingStatus = sitting.getWaitingStatus();
    this.waitingCompleteDateTime = ZonedDateTimeUtils.nowOfSeoul();
  }

  public void cancelByCustomer() {
    WaitingCancelValidator.validateStatus(this);

    WaitingDetailStatus cancelByCustomer = WaitingDetailStatus.CANCEL_BY_CUSTOMER;
    this.waitingDetailStatus = cancelByCustomer;
    this.waitingStatus = cancelByCustomer.getWaitingStatus();
    this.waitingCompleteDateTime = ZonedDateTimeUtils.nowOfSeoul();
  }

  public void cancelByShop() {
    WaitingCancelValidator.validateStatus(this);

    WaitingDetailStatus cancelByShop = WaitingDetailStatus.CANCEL_BY_SHOP;
    this.waitingDetailStatus = cancelByShop;
    this.waitingStatus = cancelByShop.getWaitingStatus();
    this.waitingCompleteDateTime = ZonedDateTimeUtils.nowOfSeoul();
  }

  public void cancelBySitting() {
    WaitingCancelValidator.validateStatus(this);

    WaitingDetailStatus cancelBySitting = WaitingDetailStatus.CANCEL_BY_SITTING;
    this.waitingDetailStatus = cancelBySitting;
    this.waitingStatus = cancelBySitting.getWaitingStatus();
    this.waitingCompleteDateTime = ZonedDateTimeUtils.nowOfSeoul();
  }

  public void cancelByOutOfStock() {
    WaitingCancelValidator.validateStatus(this);

    WaitingDetailStatus cancelByOutOfStock = WaitingDetailStatus.CANCEL_BY_OUT_OF_STOCK;
    this.waitingDetailStatus = cancelByOutOfStock;
    this.waitingStatus = cancelByOutOfStock.getWaitingStatus();
    this.waitingCompleteDateTime = ZonedDateTimeUtils.nowOfSeoul();
  }

  public void noShow() {
    WaitingCancelValidator.validateStatus(this);

    WaitingDetailStatus cancelByNoShow = WaitingDetailStatus.CANCEL_BY_NO_SHOW;
    this.waitingDetailStatus = cancelByNoShow;
    this.waitingStatus = cancelByNoShow.getWaitingStatus();
    this.waitingCompleteDateTime = ZonedDateTimeUtils.nowOfSeoul();
  }

  public void putOff(int maxWaitingOrder, LocalDate operationDate,
      WaitingHistories waitingHistories) {
    WaitingValidator.validatePutOff(this, maxWaitingOrder, operationDate, waitingHistories);

    WaitingDetailStatus putOff = WaitingDetailStatus.PUT_OFF;
    this.waitingDetailStatus = putOff;
    this.waitingStatus = putOff.getWaitingStatus();
    this.waitingNumbers.putOffWaitingOrder(maxWaitingOrder);

    this.remark = getRemarkString(putOff, this.remark);
  }

  public void undo(List<WaitingEntity> waitingEntities) {
    WaitingValidator.validateUndo(this, waitingEntities);

    WaitingDetailStatus undo = WaitingDetailStatus.UNDO;
    this.waitingDetailStatus = undo;
    this.waitingStatus = undo.getWaitingStatus();
    this.waitingCompleteDateTime = null;

    this.remark = getRemarkString(undo, this.remark);
  }

  public void undoByCustomer(List<WaitingEntity> waitingEntities) {
    WaitingValidator.validateUndoByCustomer(this, waitingEntities);

    WaitingDetailStatus undoByCustomer = WaitingDetailStatus.UNDO_BY_CUSTOMER;
    this.waitingDetailStatus = undoByCustomer;
    this.waitingStatus = undoByCustomer.getWaitingStatus();
    this.waitingCompleteDateTime = null;

    this.remark = getRemarkString(undoByCustomer, this.remark);
  }


  public void delayed(LocalDate operationDate) {
    WaitingValidator.validateDelayed(this, operationDate);

    WaitingDetailStatus delay = WaitingDetailStatus.DELAY;
    this.waitingDetailStatus = delay;
    this.waitingStatus = delay.getWaitingStatus();
  }

  public void expire() {
    WaitingDetailStatus expiration = WaitingDetailStatus.EXPIRATION;
    this.waitingDetailStatus = expiration;
    this.waitingStatus = expiration.getWaitingStatus();
    this.waitingCompleteDateTime = ZonedDateTimeUtils.nowOfSeoul();
  }

  public boolean canUndo(ZonedDateTime now) {
    Duration between = Duration.between(this.waitingCompleteDateTime, now);

    return between.toMinutes() <= CAN_UNDO_DURATION_MINUTE;
  }

  private String getRemarkString(WaitingDetailStatus waitingDetailStatus, String remark) {
    WaitingRemark waitingRemark = WaitingRemark.find(waitingDetailStatus);

    if (StringUtils.isBlank(remark) || waitingRemark == WaitingRemark.UNKNOWN) {
      return waitingRemark.getValue();
    }
    if (StringUtils.contains(remark, waitingRemark.getValue())) {
      return remark;
    }
    return StringUtils.joinWith(DELIMITER, remark, waitingRemark.getValue());
  }

  public Boolean isGuest() {
    return this.customerSeq == null;
  }

  public boolean isWaitingOrderLessThan(Integer waitingOrder) {
    return this.waitingNumbers.isWaitingOrderLessThan(waitingOrder);
  }

  public boolean isWaitingOrderGreaterOrEqualThan(int waitingOrder) {
    return this.waitingNumbers.isWaitingOrderGreaterOrEqualThan(waitingOrder);
  }

  public void readyToEnter(LocalDate operationDate, WaitingHistories histories) {
    WaitingValidator.validateReadyToEnter(this, operationDate, histories);
    this.waitingDetailStatus = WaitingDetailStatus.READY_TO_ENTER;
  }
}
