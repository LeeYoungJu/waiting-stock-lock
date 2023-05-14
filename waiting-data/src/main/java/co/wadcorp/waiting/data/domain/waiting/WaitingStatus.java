package co.wadcorp.waiting.data.domain.waiting;

import lombok.Getter;

@Getter
public enum WaitingStatus {
  WAITING("웨이팅중"),
  SITTING("착석"),
  CANCEL("취소"),
  EXPIRATION("만료");

  private final String value;

  WaitingStatus(String value) {
    this.value = value;
  }
}
