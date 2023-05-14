package co.wadcorp.waiting.data.enums;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum WaitingModeType {

  /**
   * 기본 웨이팅 현황
   */
  DEFAULT,
  /**
   * 테이블별 웨이팅 현황
   */
  TABLE;

  WaitingModeType() {
  }

  public static String getValue(String type) {
    return Arrays.stream(values()).anyMatch(e -> String.valueOf(e).equals(type))
        ? type
        : String.valueOf(DEFAULT);
  }

  public boolean isDefault() {
    return this == DEFAULT;
  }

}
