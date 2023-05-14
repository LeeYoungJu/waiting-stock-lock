package co.wadcorp.waiting.data.query.waiting.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WaitingCurrentStatusDto {

  private int teamCount;
  private int peopleCount;
  private List<CurrentStatus> currentStatuses;

  public WaitingCurrentStatusDto() {
  }

  public WaitingCurrentStatusDto(int teamCount, int peopleCount,
      List<CurrentStatus> currentStatuses) {
    this.teamCount = teamCount;
    this.peopleCount = peopleCount;
    this.currentStatuses = currentStatuses;
  }

  @Getter
  public static class CurrentStatus {

    private String id;
    private String seatOptionName;
    private SeatOption seatOption;
    private Integer teamCount;
    private Integer peopleCount;
    private Integer expectedWaitingTime;
    private Boolean isUsedExpectedWaitingPeriod;

    public CurrentStatus() {
    }

    public CurrentStatus(String id, String seatOptionName, SeatOption seatOption, Integer teamCount,
        Integer peopleCount, Integer expectedWaitingTime, Boolean isUsedExpectedWaitingPeriod) {
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
  public static class SeatOption {

    private Integer minSeatCount;
    private Integer maxSeatCount;
    private Boolean isPickup; // TODO: 2023/03/09 front 협의 후 naming 변경

    public SeatOption(Integer minSeatCount, Integer maxSeatCount, Boolean isTakeOut) {
      this.minSeatCount = minSeatCount;
      this.maxSeatCount = maxSeatCount;
      this.isPickup = isTakeOut;
    }

  }
}