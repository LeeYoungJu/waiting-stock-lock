package co.wadcorp.waiting.data.domain.waiting;

import lombok.Getter;

@Getter
public enum RegisterChannel {

  WAITING_APP("현장"),
  WAITING_MANAGER("수기"),
  CATCH_APP("원격")
  ;

  private final String value;

  RegisterChannel(String value) {
    this.value = value;
  }

}
