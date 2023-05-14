package co.wadcorp.waiting.data.domain.settings;

import java.util.List;

public class DefaultPrecautionSettingDataFactory {

  private static final String EMPTY_STRING = "";
  private static final List<Precaution> EMPTY_LIST = List.of();

  public static PrecautionSettingsData create() {

    return PrecautionSettingsData.builder()
        .messagePrecaution(EMPTY_STRING)
        .isUsedPrecautions(false)
        .precautions(EMPTY_LIST)
        .build();

  }
}
