package co.wadcorp.waiting.data.domain.settings;


import co.wadcorp.waiting.data.domain.order.OrderType;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class SeatOptions {

  public static final SeatOptions EMPTY_SEAT_OPTIONS = new SeatOptions();
  private static final Integer MIN_EXPECTED_WAITING_PERIOD_MINUTE = 5;
  public static final String DEFAULT_MODE_TAKE_OUT_NAME_TEXT = "포장";
  public static final String DEFAULT_MODE_NOT_TAKE_OUT_NAME_TEXT = "착석";

  private String id;
  private String name;
  private Integer minSeatCount;
  private Integer maxSeatCount;
  private Integer expectedWaitingPeriod;
  private Boolean isUsedExpectedWaitingPeriod;
  private Boolean isDefault;
  private Boolean isTakeOut;

  @Builder
  private SeatOptions(String id, String name, Integer minSeatCount, Integer maxSeatCount,
      Integer expectedWaitingPeriod, Boolean isUsedExpectedWaitingPeriod, Boolean isDefault,
      Boolean isTakeOut) {
    this.id = id;
    this.name = name;
    this.minSeatCount = minSeatCount;
    this.maxSeatCount = maxSeatCount;
    this.expectedWaitingPeriod = expectedWaitingPeriod;
    this.isUsedExpectedWaitingPeriod = isUsedExpectedWaitingPeriod;
    this.isDefault = isDefault;
    this.isTakeOut = isTakeOut;
  }

  public void validSeatCount(int totalSeatCount) {
    if (this.isTakeOut) {
      return;
    }
    if (totalSeatCount < this.minSeatCount) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.UNDER_AVAILABLE_SEATS);
    }
    if (totalSeatCount > this.maxSeatCount) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.EXCEED_AVAILABLE_SEATS);
    }
  }

  @JsonIgnore
  public boolean isNotUseExpectedWaitingPeriod() {
    return !this.isUsedExpectedWaitingPeriod;
  }

  public Integer calculateExpectedWaitingPeriod(int teamCount) {
    if (!this.isUsedExpectedWaitingPeriod) {
      return 0;
    }

    int calculateExpectedWaitingPeriod = teamCount * expectedWaitingPeriod;
    return Integer.max(MIN_EXPECTED_WAITING_PERIOD_MINUTE, calculateExpectedWaitingPeriod);
  }

}
