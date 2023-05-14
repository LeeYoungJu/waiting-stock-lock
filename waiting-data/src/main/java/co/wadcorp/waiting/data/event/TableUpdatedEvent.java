package co.wadcorp.waiting.data.event;

import java.time.LocalDate;

public record TableUpdatedEvent(String shopId, LocalDate operationDate ) {

}