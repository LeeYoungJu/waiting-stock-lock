package co.wadcorp.waiting.data.service.waiting.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TableCurrentStatusDto {

  private int teamCount;
  private int peopleCount;
  private List<SeatsCurrentStatus> seatsCurrentStatuses;

  @Builder
  private TableCurrentStatusDto(int teamCount, int peopleCount,
      List<SeatsCurrentStatus> seatsCurrentStatuses) {
    this.teamCount = teamCount;
    this.peopleCount = peopleCount;
    this.seatsCurrentStatuses = seatsCurrentStatuses;
  }

  @Getter
  @NoArgsConstructor
  public static class SeatsCurrentStatus {

    private String id;
    private String seatOptionName;
    private SeatOption seatOption;
    private Integer teamCount;
    private Integer peopleCount;
    private Integer expectedWaitingTime;
    private Boolean isUsedExpectedWaitingPeriod;

    @Builder
    private SeatsCurrentStatus(String id, String seatOptionName, SeatOption seatOption,
        Integer teamCount,
        Integer peopleCount,
        Integer expectedWaitingTime, Boolean isUsedExpectedWaitingPeriod) {
      this.id = id;
      this.seatOptionName = seatOptionName;
      this.seatOption = seatOption;
      this.teamCount = teamCount;
      this.peopleCount = peopleCount;
      this.expectedWaitingTime = expectedWaitingTime;
      this.isUsedExpectedWaitingPeriod = isUsedExpectedWaitingPeriod;
    }

  }

  @Getter
  @Builder
  @NoArgsConstructor
  public static class SeatOption {

    private Integer minSeatCount;
    private Integer maxSeatCount;
    private Boolean isTakeOut;

    public SeatOption(Integer minSeatCount, Integer maxSeatCount, Boolean isTakeOut) {
      this.minSeatCount = minSeatCount;
      this.maxSeatCount = maxSeatCount;
      this.isTakeOut = isTakeOut;
    }

  }

}
