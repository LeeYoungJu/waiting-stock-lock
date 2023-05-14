package co.wadcorp.waiting.data.domain.shop.operation.status;

/**
 * 매장에서 설정한 운영 상태
 */
public enum RegistrableStatus {

  OPEN("영업 중"),
  BY_PASS("바로 입장"),
  CLOSED("영업 종료");

  private final String value;

  RegistrableStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
