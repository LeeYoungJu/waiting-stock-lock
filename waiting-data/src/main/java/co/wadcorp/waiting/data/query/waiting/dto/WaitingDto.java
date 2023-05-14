package co.wadcorp.waiting.data.query.waiting.dto;

import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.waiting.data.domain.waiting.PersonOption;
import co.wadcorp.waiting.data.domain.waiting.PersonOptionsData;
import co.wadcorp.waiting.data.domain.waiting.RegisterChannel;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WaitingDto {

  private Long seq;
  private String waitingId;
  private String shopId;
  private RegisterChannel registerChannel;
  private LocalDate operationDate;
  private long customerSeq;
  private PhoneNumber customerPhoneNumber;
  private String customerName;
  private int sittingCount;
  private int waitingNumber;
  private int waitingOrder;
  private WaitingStatus waitingStatus;
  private WaitingDetailStatus waitingDetailStatus;
  private String waitingMemo;
  private String seatOptionName;
  private int totalSeatCount;
  private PersonOptionsData personOptions;
  private ZonedDateTime expectedSittingDateTime;
  private ZonedDateTime waitingCompleteDateTime;
  private ZonedDateTime regDateTime;

  public List<PersonOption> getPersonOptions() {
    return personOptions.getPersonOptions();
  }

  public String getCustomerPhoneNumber() {
    if (Objects.isNull(customerPhoneNumber)) {
      return null;
    }
    return customerPhoneNumber.getLocal();
  }

  public String getPersonOptionText() {
    return this.personOptions.getPersonOptionText();
  }

}