package co.wadcorp.waiting.data.event.settings;

import java.time.LocalDate;

public record ChangedRemoteOperationTimeSettingsEvent(String shopId, LocalDate settingStartDate) {

}
