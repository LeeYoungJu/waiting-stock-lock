package co.wadcorp.waiting.data.query.waiting.dto;

import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.waiting.data.domain.waiting.PersonOption;
import co.wadcorp.waiting.data.domain.waiting.PersonOptionsData;
import co.wadcorp.waiting.data.domain.waiting.RegisterChannel;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class WaitingHistoriesDto {

  private static final String EMPTY_STRING = "";
  private static final long CAN_PUT_OFF_COUNT = 2L;

  private final WaitingDto waiting;
  private final List<WaitingHistoryDto> waitingHistories;

  public WaitingHistoriesDto(WaitingDto waiting, List<WaitingHistoryDto> waitingHistories) {
    this.waiting = waiting;
    this.waitingHistories = waitingHistories;
  }

  @Getter
  public static class WaitingDto {

    private final String waitingId;
    private final String shopId;
    private final RegisterChannel registerChannel;
    private final WaitingStatus waitingStatus;
    private final WaitingDetailStatus waitingDetailStatus;
    private final LocalDate operationDate;
    private final Integer waitingNumber;
    private final Integer waitingOrder;
    private final PhoneNumber customerPhone;
    private final String customerName;
    private final int visitCount;
    private final Integer totalPersonCount;
    private final PersonOptionsData personOptionsData;
    private final String seatOptionName;
    private final ZonedDateTime regDateTime;

    public List<PersonOption> getPersonOptions() {
      return personOptionsData.getPersonOptions();
    }

    @QueryProjection
    public WaitingDto(String waitingId, String shopId, RegisterChannel registerChannel,
        WaitingStatus waitingStatus, WaitingDetailStatus waitingDetailStatus, LocalDate operationDate, Integer waitingNumber,
        Integer waitingOrder, PhoneNumber customerPhone, String customerName, int visitCount,
        Integer totalPersonCount,
        PersonOptionsData personOptionsData, String seatOptionName, ZonedDateTime regDateTime) {
      this.waitingId = waitingId;
      this.shopId = shopId;
      this.registerChannel = registerChannel;
      this.waitingStatus = waitingStatus;
      this.waitingDetailStatus = waitingDetailStatus;
      this.operationDate = operationDate;
      this.waitingNumber = waitingNumber;
      this.waitingOrder = waitingOrder;
      this.customerPhone = customerPhone;
      this.customerName = customerName;
      this.visitCount = visitCount;
      this.totalPersonCount = totalPersonCount;
      this.personOptionsData = personOptionsData;
      this.seatOptionName = seatOptionName;
      this.regDateTime = regDateTime;
    }

    public String getCustomerName() {
      return StringUtils.defaultString(customerName, EMPTY_STRING);
    }

    public String getPersonOptionText() {
      List<PersonOption> personOptions = this.personOptionsData.getPersonOptions();

      String option = personOptions.stream()
          .map(item -> item.getName() + EMPTY_STRING + item.getCount())
          .collect(Collectors.joining("/"));

      StringBuilder sb = new StringBuilder();
      sb.append(option);

      if (StringUtils.isNotEmpty(this.seatOptionName)) {
        sb.append("/").append(this.seatOptionName);
      }
      return sb.toString();
    }

    public String getLocalPhoneNumber() {
      if(Objects.isNull(customerPhone)) {
        return EMPTY_STRING;
      }

      return this.customerPhone.getLocal();
    }
  }

  public long getCanPutOffCount() {
    return CAN_PUT_OFF_COUNT - this.waitingHistories.stream()
        .filter(item -> item.getWaitingDetailStatus() == WaitingDetailStatus.PUT_OFF)
        .count();
  }

  @Getter
  public static class WaitingHistoryDto {

    private final WaitingStatus waitingStatus;
    private final WaitingDetailStatus waitingDetailStatus;
    private final ZonedDateTime regDateTime;

    @QueryProjection
    public WaitingHistoryDto(WaitingStatus waitingStatus, WaitingDetailStatus waitingDetailStatus,
        ZonedDateTime regDateTime) {
      this.waitingStatus = waitingStatus;
      this.waitingDetailStatus = waitingDetailStatus;
      this.regDateTime = regDateTime;
    }
  }
}
