package co.wadcorp.waiting.data.event;

import java.util.List;

public record WaitingUpdatedEvent(List<String> waitingIds) {

}