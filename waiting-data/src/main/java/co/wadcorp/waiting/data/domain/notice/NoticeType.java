package co.wadcorp.waiting.data.domain.notice;

public enum NoticeType {

  NORMAL("공지"),
  UPDATE("업데이트");

  private final String value;

  NoticeType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
