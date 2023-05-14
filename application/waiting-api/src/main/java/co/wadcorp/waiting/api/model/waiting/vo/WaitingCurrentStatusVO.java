package co.wadcorp.waiting.api.model.waiting.vo;

import static co.wadcorp.libs.stream.StreamUtils.convert;

import co.wadcorp.waiting.data.query.waiting.dto.WaitingCurrentStatusDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingCurrentStatusDto.SeatOption;
import co.wadcorp.waiting.data.service.waiting.dto.TableCurrentStatusDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WaitingCurrentStatusVO {

  private final int teamCount;
  private final int peopleCount;
  private final List<SeatsCurrentStatus> seatsCurrentStatuses;

  @Builder
  public WaitingCurrentStatusVO(int teamCount, int peopleCount,
      List<SeatsCurrentStatus> seatsCurrentStatuses) {
    this.teamCount = teamCount;
    this.peopleCount = peopleCount;
    this.seatsCurrentStatuses = seatsCurrentStatuses;
  }

  @Getter
  public static class SeatsCurrentStatus {

    private final String id;
    private final String seatOptionName;
    private final SeatOption seatOption;
    private final Integer teamCount;
    private final Integer peopleCount;
    private final Integer expectedWaitingTime;
    private final Boolean isUsedExpectedWaitingPeriod;

    @Builder
    public SeatsCurrentStatus(String id, String seatOptionName, SeatOption seatOption,
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

    public static SeatsCurrentStatus toDto(
        TableCurrentStatusDto.SeatsCurrentStatus currentStatus) {

      return SeatsCurrentStatus.builder()
          .id(currentStatus.getId())
          .seatOptionName(currentStatus.getSeatOptionName())
          .seatOption(SeatOption.builder()
              .minSeatCount(currentStatus.getSeatOption().getMinSeatCount())
              .maxSeatCount(currentStatus.getSeatOption().getMaxSeatCount())
              .isPickup(currentStatus.getSeatOption().getIsTakeOut())
              .build()
          )
          .teamCount(currentStatus.getTeamCount())
          .peopleCount(currentStatus.getPeopleCount())
          .expectedWaitingTime(currentStatus.getExpectedWaitingTime())
          .isUsedExpectedWaitingPeriod(currentStatus.getIsUsedExpectedWaitingPeriod())
          .build();
    }
  }

  public static WaitingCurrentStatusVO toDto(TableCurrentStatusDto dto) {
    return WaitingCurrentStatusVO.builder()
        .teamCount(dto.getTeamCount())
        .peopleCount(dto.getPeopleCount())
        .seatsCurrentStatuses(convert(dto.getSeatsCurrentStatuses(), SeatsCurrentStatus::toDto))
        .build();
  }
}
