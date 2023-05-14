package co.wadcorp.waiting.data.event;

import java.time.LocalDate;

public record TableStatusUpdateEvent(String shopId, LocalDate operationDate) {
}
