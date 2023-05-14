package co.wadcorp.waiting.data.event;

import java.time.LocalDate;

public record RegisteredEvent(String shopId, Long waitingHistorySeq, LocalDate operationDate, String deviceId) {

}
