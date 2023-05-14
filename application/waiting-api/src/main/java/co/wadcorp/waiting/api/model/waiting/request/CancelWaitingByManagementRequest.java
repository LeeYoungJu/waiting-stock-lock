package co.wadcorp.waiting.api.model.waiting.request;

import co.wadcorp.waiting.api.model.waiting.vo.CancelReason;

public record CancelWaitingByManagementRequest(CancelReason cancelReason) {

}
