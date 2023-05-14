package co.wadcorp.waiting.data.domain.settings;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.wadcorp.waiting.data.exception.AppException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HomeSettingsDataTest {

  @DisplayName("테이블 모드 좌석 종류의 수는 2~4개 사이이다.")
  @ValueSource(ints = {2, 3, 4})
  @ParameterizedTest
  void validateTableModeSeatOptionsSize(int seatCount) {
    // given
    List<SeatOptions> seatOptions = new ArrayList<>();
    for (int i = 0; i < seatCount; i++) {
      seatOptions.add(
          SeatOptions.builder()
              .name("홀" + i)
              .build()
      );
    }

    // when // then
    assertDoesNotThrow(() -> {
      HomeSettingsData.builder()
          .waitingModeType("TABLE")
          .tableModeSettings(seatOptions)
          .build();
    });
  }

  @DisplayName("테이블 모드 좌석 종류의 수가 2개 미만이거나 4개 초과 시 예외가 발생한다.")
  @ValueSource(ints = {1, 5})
  @ParameterizedTest
  void validateTableModeSeatOptionsSize2(int seatCount) {
    // given
    List<SeatOptions> seatOptions = new ArrayList<>();
    for (int i = 0; i < seatCount; i++) {
      seatOptions.add(
          SeatOptions.builder()
              .name("홀" + i)
              .build()
      );
    }

    // when // then
    assertThatThrownBy(() -> HomeSettingsData.builder()
        .waitingModeType("TABLE")
        .tableModeSettings(seatOptions)
        .build()
    )
        .isInstanceOf(AppException.class)
        .hasMessage("좌석 종류는 2개 이상 4개 이하여야 합니다.");
  }

  @DisplayName("테이블 모드 좌석 이름은 중복될 수 없다.")
  @Test
  void validateDuplicateSeatName() {
    // given
    List<SeatOptions> seatOptions = List.of(
        SeatOptions.builder()
            .name("홀")
            .build(),
        SeatOptions.builder()
            .name("홀")
            .build()
    );

    // when // then
    assertThatThrownBy(() -> HomeSettingsData.builder()
        .waitingModeType("TABLE")
        .tableModeSettings(seatOptions)
        .build()
    )
        .isInstanceOf(AppException.class)
        .hasMessage("좌석명은 중복될 수 없습니다.");
  }

}