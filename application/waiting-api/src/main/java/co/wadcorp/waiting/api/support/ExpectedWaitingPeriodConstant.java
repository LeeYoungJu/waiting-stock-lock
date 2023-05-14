package co.wadcorp.waiting.api.support;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpectedWaitingPeriodConstant {

  public static final Integer MIN_EXPRESSION_WAITING_PERIOD_CONSTANT = 5;
  public static final Integer MAX_EXPRESSION_WAITING_PERIOD_CONSTANT = 180;
}
