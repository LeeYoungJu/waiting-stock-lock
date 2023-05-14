package co.wadcorp.waiting.api.internal.service.waiting.dto.response;

import static co.wadcorp.waiting.api.internal.service.waiting.dto.response.CreatedOrderDto.OrderLineItem;

import co.wadcorp.libs.datetime.ISO8601;
import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import co.wadcorp.waiting.data.domain.waiting.PersonOptionsData;
import co.wadcorp.waiting.data.domain.waiting.RegisterChannel;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.shared.util.LocalDateUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RemoteListWaitingResponse {

  private final String id;
  private final String shopName;
  private final RegisterChannel registerChannel;
  private final String operationDate;
  private final String customerPhoneNumber;
  private final int waitingNumber; // 웨이팅 채번 (고객이 발급받는 번호)
  private final Integer waitingOrder; // 웨이팅 순서 (대기열 순서)
  private final int waitingRegisteredOrder; // 웨이팅 등록 순서
  private final WaitingStatus waitingStatus;
  private final WaitingDetailStatus waitingDetailStatus;
  private final int totalPersonCount;
  private String expectedSittingDateTime;
  private String waitingCompleteDateTime; // 착석, 취소 완료 시간 (nullable)
  private final List<PersonOptionDto> personOptions = new ArrayList<>();
  private final TableDto table;
  private final RemoteWaitingListOrderMenuDto order;
  private final String regDateTime; // 등록 시간

  @Builder
  private RemoteListWaitingResponse(String id, String shopName, RegisterChannel registerChannel,
      LocalDate operationDate, String customerPhoneNumber, int waitingNumber, Integer waitingOrder,
      int waitingRegisteredOrder, WaitingStatus waitingStatus,
      WaitingDetailStatus waitingDetailStatus, int totalPersonCount,
      ZonedDateTime expectedSittingDateTime, ZonedDateTime waitingCompleteDateTime,
      List<PersonOptionDto> personOptions, TableDto table, RemoteWaitingListOrderMenuDto order,
      ZonedDateTime regDateTime) {
    this.id = id;
    this.shopName = shopName;
    this.registerChannel = registerChannel;
    this.operationDate = LocalDateUtils.convertToString(operationDate);
    this.customerPhoneNumber = customerPhoneNumber;
    this.waitingNumber = waitingNumber;
    this.waitingOrder = waitingOrder;
    this.waitingRegisteredOrder = waitingRegisteredOrder;
    this.waitingStatus = waitingStatus;
    this.waitingDetailStatus = waitingDetailStatus;
    this.totalPersonCount = totalPersonCount;
    if (expectedSittingDateTime != null) {
      this.expectedSittingDateTime = ISO8601.format(expectedSittingDateTime);
    }
    if (waitingCompleteDateTime != null) {
      this.waitingCompleteDateTime = ISO8601.format(waitingCompleteDateTime);
    }
    if (personOptions != null) {
      this.personOptions.addAll(personOptions);
    }
    this.table = table;
    this.order = order;
    this.regDateTime = ISO8601.format(regDateTime);
  }

  public static RemoteListWaitingResponse of(WaitingEntity waiting, String shopName,
      String customerPhoneNumber, RemoteWaitingListOrderMenuDto orderMenuDto) {
    return RemoteListWaitingResponse.builder()
        .id(waiting.getWaitingId())
        .shopName(shopName)
        .registerChannel(waiting.getRegisterChannel())
        .operationDate(waiting.getOperationDate())
        .customerPhoneNumber(customerPhoneNumber)
        .waitingNumber(waiting.getWaitingNumber())
        .waitingRegisteredOrder(waiting.getWaitingOrder())
        .waitingStatus(waiting.getWaitingStatus())
        .waitingDetailStatus(waiting.getWaitingDetailStatus())
        .totalPersonCount(waiting.getTotalPersonCount())
        .waitingCompleteDateTime(waiting.getWaitingCompleteDateTime())
        .personOptions(convertPersonOptions(waiting.getPersonOptionsData()))
        .order(orderMenuDto)
        .regDateTime(waiting.getRegDateTime())
        .build();
  }


  public static RemoteListWaitingResponse of(WaitingEntity waiting, String shopName,
      String customerPhoneNumber, int waitingTeamCount, SeatOptions seatOptions,
      ZonedDateTime nowDateTime, RemoteWaitingListOrderMenuDto orderMenuDto) {
    return RemoteListWaitingResponse.builder()
        .id(waiting.getWaitingId())
        .shopName(shopName)
        .registerChannel(waiting.getRegisterChannel())
        .operationDate(waiting.getOperationDate())
        .customerPhoneNumber(customerPhoneNumber)
        .waitingNumber(waiting.getWaitingNumber())
        .waitingOrder(waitingTeamCount)
        .waitingRegisteredOrder(waiting.getWaitingOrder())
        .waitingStatus(waiting.getWaitingStatus())
        .waitingDetailStatus(waiting.getWaitingDetailStatus())
        .totalPersonCount(waiting.getTotalPersonCount())
        .expectedSittingDateTime(
            getExpectedSittingDateTime(nowDateTime, seatOptions, waitingTeamCount))
        .waitingCompleteDateTime(waiting.getWaitingCompleteDateTime())
        .personOptions(convertPersonOptions(waiting.getPersonOptionsData()))
        .table(TableDto.builder()
            .id(seatOptions.getId())
            .name(seatOptions.getName())
            .isTakeOut(seatOptions.getIsTakeOut())
            .build()
        )
        .order(orderMenuDto)
        .regDateTime(waiting.getRegDateTime())
        .build();
  }

  private static ZonedDateTime getExpectedSittingDateTime(ZonedDateTime nowDateTime,
      SeatOptions seatOptions, int waitingTeamCount) {
    if (seatOptions.isNotUseExpectedWaitingPeriod()) {
      return null;
    }

    Integer expectedWaitingPeriod = seatOptions.calculateExpectedWaitingPeriod(waitingTeamCount);
    return nowDateTime.plusMinutes(expectedWaitingPeriod);
  }

  private static List<PersonOptionDto> convertPersonOptions(PersonOptionsData personOptionsData) {
    return personOptionsData.getPersonOptions().stream()
        .map(po -> PersonOptionDto.builder()
            .name(po.getName())
            .count(po.getCount())
            .additionalOptions(po.getAdditionalOptions().stream()
                .map(ao -> AdditionalOptionDto.builder()
                    .name(ao.getName())
                    .count(ao.getCount())
                    .build()
                )
                .toList()
            )
            .build()
        )
        .toList();
  }

}
