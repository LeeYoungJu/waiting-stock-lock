package co.wadcorp.waiting.data.config;

import java.time.LocalDate;

public class WaitingNumberConstructor {

  /**
   * 날짜 끝자리 + ## 오름차순 (단, 날짜 끝자리가 0인 경우, 1로 표기)
   * */
  public static Integer initWaitingNumber() {
    int date = LocalDate.now().getDayOfMonth();
    int lastNumOfDate = date % 10;

    // 날짜 끝자리가 0인 경우, 1로 시작
    int prefixNumber = lastNumOfDate == 0 ? 1 : lastNumOfDate;

    String waitingNumber = String.format("%d%02d", prefixNumber, 1);

    return Integer.parseInt(waitingNumber);
  }
}
