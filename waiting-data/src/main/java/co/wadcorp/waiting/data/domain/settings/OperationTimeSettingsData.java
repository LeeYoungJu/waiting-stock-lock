package co.wadcorp.waiting.data.domain.settings;

import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.AutoPauseSettings.PauseReason;
import co.wadcorp.waiting.data.enums.OperationDay;
import co.wadcorp.waiting.data.exception.AppException;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationTimeSettingsData {

  private List<OperationTimeForDay> operationTimeForDays;
  private Boolean isUsedAutoPause;
  private AutoPauseSettings autoPauseSettings;

  public List<PauseReason> getPauseReasons() {
    return this.autoPauseSettings.pauseReasons;
  }

  public LocalTime getAutoPauseStartTime() {
    return this.autoPauseSettings.getAutoPauseStartTime();
  }

  public LocalTime getAutoPauseEndTime() {
    return this.autoPauseSettings.getAutoPauseEndTime();
  }

  public OperationTimeForDay findOperationTimeForDay(OperationDay day) {
    return operationTimeForDays.stream()
        .filter(item -> day.isSameDay(item.getDay()))
        .findFirst()
        .orElseThrow(
            () -> new AppException(
                HttpStatus.BAD_REQUEST, String.format("Illegal args for day : [%s]", day)));
  }

  public PauseReason getDefaultPauseReason() {
    return this.autoPauseSettings.getDefaultPauseReason();
  }

  public PauseReason findReason(String pauseReasonId) {
    return this.autoPauseSettings.findReason(pauseReasonId);
  }

  /**
   * 요일별로 시간 변화가 있었는지 체크 후 요일별 변화 여부를 List에 담고 그 List를 ChangeChecker 객체에 담은 후 반환한다.
   * @param compareData: 변화 여부를 비교할 데이터
   * @return OperationTimeForDaysChangeChecker
   */
  public OperationTimeForDaysChangeChecker checkChangesInTimeForDays(OperationTimeSettingsData compareData) {
    if(compareData == null) {
      return DefaultOperationTimeSettingDataFactory
          .createOperationTimeForDaysChangeChecker();
    }

    List<OperationTimeForDayChangeData> changeDataList = this.operationTimeForDays.stream()
        .map(operationTimeForDay -> {
          OperationTimeForDay sameTimeForDay =
              operationTimeForDay.getSameDayTimeData(compareData.getOperationTimeForDays())
                  .orElse(OperationTimeForDay.EMPTY);

          return OperationTimeForDayChangeData.builder()
              .day(operationTimeForDay.getDay())
              .isChanged(!operationTimeForDay.equalsTimeData(sameTimeForDay)) // 동일하지 않으면 변화가 있는 것.
              .build();
        }).toList();

    return new OperationTimeForDaysChangeChecker(changeDataList);
  }

  @EqualsAndHashCode
  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class OperationTimeForDayChangeData {
    private String day;
    private boolean isChanged;
  }

  @EqualsAndHashCode
  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class OperationTimeForDay {

    private String day;
    private LocalTime operationStartTime;
    private LocalTime operationEndTime;
    private Boolean isClosedDay;

    private static final OperationTimeForDay EMPTY = new OperationTimeForDay();

    public boolean isOpened() {
      return !isClosedDay;
    }


    /**
     * 현재 객체와 요일이 같은 객체를 리스트에서 찾아서 반환해주는 함수
     * @param operationTimeForDays : 탐색 대상 리스트
     * @return 탐색 결과(Optional)
     */
    public Optional<OperationTimeForDay> getSameDayTimeData(List<OperationTimeForDay> operationTimeForDays) {
      return operationTimeForDays.stream()
          .filter(timeData -> timeData.getDay().equals(this.day))
          .findFirst();
    }

    public boolean equalsTimeData(OperationTimeForDay compareData) {
      return compareData.operationStartTime.equals(this.operationStartTime)
          && compareData.operationEndTime.equals(this.operationEndTime);
    }
  }

  @EqualsAndHashCode
  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class AutoPauseSettings {

    private LocalTime autoPauseStartTime;
    private LocalTime autoPauseEndTime;
    private List<PauseReason> pauseReasons;

    public Boolean isPaused(LocalTime localTime) {
      return localTime.isAfter(autoPauseStartTime) && localTime.isBefore(autoPauseEndTime);
    }

    public PauseReason getDefaultPauseReason() {
      return pauseReasons.stream()
          .filter(item -> item.isDefault)
          .findFirst()
          .orElseGet(DefaultOperationTimeSettingDataFactory::createPauseReason);
    }

    public PauseReason findReason(String pauseReasonId) {
      return pauseReasons.stream()
          .filter(item -> item.getId().equals(pauseReasonId))
          .findFirst()
          .orElseThrow(
              () -> new AppException(HttpStatus.BAD_REQUEST, "일시 중지 사유를 찾을 수 없습니다. 다시 시도해주세요.")
          );
    }

    @EqualsAndHashCode
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PauseReason {

      private String id;
      private Boolean isDefault;
      private String reason;
    }
  }

}
