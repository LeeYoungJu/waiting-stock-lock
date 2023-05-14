package co.wadcorp.waiting.data.query.waiting.dto;

import co.wadcorp.libs.phone.PhoneNumber;
import com.querydsl.core.annotations.QueryProjection;
import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
public class MyWaitingInfoDto {

  private Integer waitingNumber;
  private PhoneNumber phoneNumber;
  private Integer totalPersonCount;
  private ZonedDateTime regDateTime;

  @QueryProjection
  public MyWaitingInfoDto(Integer waitingNumber, PhoneNumber phoneNumber, Integer totalPersonCount,
      ZonedDateTime regDateTime) {
    this.waitingNumber = waitingNumber;
    this.phoneNumber = phoneNumber;
    this.totalPersonCount = totalPersonCount;
    this.regDateTime = regDateTime;
  }
}
