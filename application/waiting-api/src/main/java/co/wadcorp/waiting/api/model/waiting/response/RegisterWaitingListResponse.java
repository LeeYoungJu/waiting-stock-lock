package co.wadcorp.waiting.api.model.waiting.response;

import co.wadcorp.libs.datetime.ISO8601;
import co.wadcorp.waiting.api.model.waiting.vo.PageVO;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingOnRegistrationDto;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterWaitingListResponse {

  private final List<RegisterWaitingDto> waiting;

  @JsonUnwrapped
  private final PageVO page;

  @Getter
  public static class RegisterWaitingDto {

    private final int waitingNumber;
    private final String seatOptionName;
    private final int totalPersonCount;
    private final String regDateTime;

    @Builder
    private RegisterWaitingDto(int waitingNumber, String seatOptionName, int totalPersonCount,
        ZonedDateTime regDateTime) {
      this.waitingNumber = waitingNumber;
      this.seatOptionName = seatOptionName;
      this.totalPersonCount = totalPersonCount;
      this.regDateTime = ISO8601.format(regDateTime);
    }

    public static RegisterWaitingDto toDto(WaitingOnRegistrationDto dto) {
      return RegisterWaitingDto.builder()
          .waitingNumber(dto.getWaitingNumber())
          .seatOptionName(dto.getSeatOptionName())
          .totalPersonCount(dto.getTotalPersonCount())
          .regDateTime(dto.getRegDateTime())
          .build();
    }

  }

}
