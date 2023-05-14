package co.wadcorp.waiting.data.event;

import java.time.LocalDate;
import java.util.List;

public record SeatCanceledEvent(String shopId, Long waitingHistorySeq, List<Long> canceledWaitingHistorySeq, LocalDate operationDate, String deviceId) {

}
