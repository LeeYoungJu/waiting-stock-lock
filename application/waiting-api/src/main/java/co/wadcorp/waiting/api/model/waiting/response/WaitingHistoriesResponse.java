package co.wadcorp.waiting.api.model.waiting.response;

import co.wadcorp.libs.datetime.ISO8601;
import co.wadcorp.waiting.data.domain.waiting.RegisterChannel;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingHistoriesDto.WaitingDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingHistoriesDto.WaitingHistoryDto;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WaitingHistoriesResponse {

  @JsonUnwrapped
  private final Waiting waiting;
  private final List<WaitingHistory> waitingHistories;

  public WaitingHistoriesResponse(Waiting waiting, List<WaitingHistory> waitingHistories) {
    this.waiting = waiting;
    this.waitingHistories = waitingHistories;
  }


  public static WaitingHistoriesResponse toDto(WaitingDto waitingDto,
      List<WaitingHistoryDto> waitingHistoriesDto) {

    Waiting waiting = Waiting.builder()
        .waitingId(waitingDto.getWaitingId())
        .shopId(waitingDto.getShopId())
        .registerChannel(waitingDto.getRegisterChannel())
        .operationDate(waitingDto.getOperationDate().toString())
        .waitingNumber(waitingDto.getWaitingNumber())
        .customerPhone(waitingDto.getLocalPhoneNumber())
        .customerName(waitingDto.getCustomerName())
        .visitCount(waitingDto.getVisitCount())
        .totalPersonCount(waitingDto.getTotalPersonCount())
        .personOptionText(waitingDto.getPersonOptionText())
        .build();

    List<WaitingHistory> waitingHistories = waitingHistoriesDto.stream()
        .map(item -> WaitingHistory.builder()
            .waitingStatus(item.getWaitingStatus().name())
            .waitingStatusText(item.getWaitingStatus().getValue())
            .waitingDetailStatus(item.getWaitingDetailStatus().name())
            .waitingDetailStatusText(item.getWaitingDetailStatus().getValue())
            .regDateTime(ISO8601.format(item.getRegDateTime()))
            .build())
        .toList();

    return new WaitingHistoriesResponse(waiting, waitingHistories);
  }

  @Getter
  public static class Waiting {

    private final String waitingId;
    private final String shopId;
    private final String registerChannelText;
    private final String operationDate;
    private final Integer waitingNumber;
    private final String customerPhone;
    private final String customerName;
    private final int visitCount;
    private final Integer totalPersonCount;
    private final String personOptionText;

    @Builder
    public Waiting(String waitingId, String shopId, RegisterChannel registerChannel, String operationDate, Integer waitingNumber,
        String customerPhone, String customerName, int visitCount, Integer totalPersonCount,
        String personOptionText) {
      this.waitingId = waitingId;
      this.shopId = shopId;
      this.registerChannelText = registerChannel.getValue();
      this.operationDate = operationDate;
      this.waitingNumber = waitingNumber;
      this.customerPhone = customerPhone;
      this.customerName = customerName;
      this.visitCount = visitCount;
      this.totalPersonCount = totalPersonCount;
      this.personOptionText = personOptionText;
    }
  }


  @Getter
  public static class WaitingHistory {
    private final String waitingStatus;
    private final String waitingStatusText;
    private final String waitingDetailStatus;
    private final String waitingDetailStatusText;
    private final Boolean isSendAlimtalk;
    private final String regDateTime;

    @Builder
    public WaitingHistory(String waitingStatus, String waitingStatusText, String waitingDetailStatus,
        String waitingDetailStatusText, Boolean isSendAlimtalk,
        String regDateTime) {
      this.waitingStatus = waitingStatus;
      this.waitingStatusText = waitingStatusText;
      this.waitingDetailStatus = waitingDetailStatus;
      this.waitingDetailStatusText = waitingDetailStatusText;
      this.isSendAlimtalk = isSendAlimtalk;
      this.regDateTime = regDateTime;
    }
  }
}
