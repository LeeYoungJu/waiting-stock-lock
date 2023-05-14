package co.wadcorp.waiting.data.event;

import java.time.LocalDate;

public record DelayedEvent(String shopId, Long waitingHistorySeq, LocalDate operationDate) {

}