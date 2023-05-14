package co.wadcorp.waiting.data.domain.settings;

import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.OperationTimeForDayChangeData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OperationTimeForDaysChangeChecker {

  private Map<String, Boolean> mapChecker = new HashMap<>();

  public OperationTimeForDaysChangeChecker(List<OperationTimeForDayChangeData> changeDataList) {
    for(OperationTimeForDayChangeData dayChangeChecker : changeDataList) {
      mapChecker.put(dayChangeChecker.getDay(), dayChangeChecker.isChanged());
    }
  }

  public boolean isThereChangeInDay(String day) {
    return mapChecker.get(day);
  }
}
