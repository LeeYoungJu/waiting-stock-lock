package co.wadcorp.waiting.data.event;

import java.time.LocalDate;

public record UndoEvent(String shopId, Long waitingHistorySeq, LocalDate operationDate, String deviceId) {

}
