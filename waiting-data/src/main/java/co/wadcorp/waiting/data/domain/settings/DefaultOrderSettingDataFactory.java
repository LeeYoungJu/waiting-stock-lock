package co.wadcorp.waiting.data.domain.settings;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultOrderSettingDataFactory {

  public static OrderSettingsData create() {
    return OrderSettingsData.builder()
        .isPossibleOrder(false)
        .build();
  }

}
