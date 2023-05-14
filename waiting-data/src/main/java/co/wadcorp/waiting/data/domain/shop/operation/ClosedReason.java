package co.wadcorp.waiting.data.domain.shop.operation;

public enum ClosedReason {

  MANUAL("수동 종료"),
  OPERATION_HOUR_CLOSED("운영 시간 종료"),
  CLOSED_DAY("휴무일");

  private final String value;

  ClosedReason(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
