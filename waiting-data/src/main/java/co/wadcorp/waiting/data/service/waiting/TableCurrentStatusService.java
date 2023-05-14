package co.wadcorp.waiting.data.service.waiting;

import static co.wadcorp.libs.stream.StreamUtils.groupingBySet;

import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import co.wadcorp.waiting.data.enums.WaitingModeType;
import co.wadcorp.waiting.data.infra.waiting.CachingRedisTemplate;
import co.wadcorp.waiting.data.query.settings.HomeSettingsQueryRepository;
import co.wadcorp.waiting.data.query.waiting.WaitingQueryRepository;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingCurrentStatusCountDto;
import co.wadcorp.waiting.data.service.waiting.dto.TableCurrentStatusDto;
import co.wadcorp.waiting.data.service.waiting.dto.TableCurrentStatusDto.SeatOption;
import co.wadcorp.waiting.data.service.waiting.dto.TableCurrentStatusDto.SeatsCurrentStatus;
import co.wadcorp.waiting.shared.util.ObjectMapperUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
public class TableCurrentStatusService {

  private final CachingRedisTemplate cachingRedisTemplate;
  private final HomeSettingsQueryRepository homeSettingsQueryRepository;
  private final WaitingQueryRepository waitingQueryRepository;

  public TableCurrentStatusDto get(String shopId, LocalDate operationDate,
      WaitingModeType modeType) {
    String jsonResult = cachingRedisTemplate.getTableCurrentStatus(shopId, operationDate, modeType);

    if (StringUtils.hasText(jsonResult)) {
      TableCurrentStatusDto tableCurrentStatusDto = ObjectMapperUtils.readByObjectMapper(jsonResult,
          TableCurrentStatusDto.class);
      if (tableCurrentStatusDto != null) {
        return tableCurrentStatusDto;
      }
    }

    TableCurrentStatusDto tableCurrentStatusDto = createTableCurrentStatusDto(shopId,
        operationDate, modeType);
    String serialized = ObjectMapperUtils.convertToJson(tableCurrentStatusDto);
    if (serialized != null) {
      cachingRedisTemplate.setTableCurrentStatus(shopId, operationDate, modeType, serialized);
    }
    return tableCurrentStatusDto;
  }

  public TableCurrentStatusDto update(String shopId, LocalDate operationDate,
      WaitingModeType modeType) {
    TableCurrentStatusDto tableCurrentStatusDto = createTableCurrentStatusDto(shopId,
        operationDate, modeType);
    String serialized = ObjectMapperUtils.convertToJson(tableCurrentStatusDto);
    if (serialized != null) {
      cachingRedisTemplate.setTableCurrentStatus(shopId, operationDate, modeType, serialized);
    }
    return tableCurrentStatusDto;
  }

  // TODO: 2023/03/21 리팩토링 필요
  private TableCurrentStatusDto createTableCurrentStatusDto(String shopId,
      LocalDate operationDate, WaitingModeType modeType) {
    HomeSettingsEntity homeSettings = homeSettingsQueryRepository.findByShopId(shopId);
    List<SeatOptions> modeSettings = getModeSettings(homeSettings, modeType);

    List<WaitingCurrentStatusCountDto> currentStatusCountDtos = waitingQueryRepository.findCurrentWaitingStatuses(
        List.of(shopId), operationDate);

    // 총 인원 계산
    int peopleCount = currentStatusCountDtos.stream()
        .mapToInt(WaitingCurrentStatusCountDto::getTotalPersonCount)
        .sum();

    // 테이블 별 그룹화
    Map<String, Set<WaitingCurrentStatusCountDto>> tableModeSeatsGroup = groupingBySet(
        currentStatusCountDtos, WaitingCurrentStatusCountDto::getSeatOptionName);

    // 테이블 별 팀 수, 인원 수, 예상 대기시간 계산
    List<SeatsCurrentStatus> currentStatuses = modeSettings.stream()
        .map(item -> {
          Set<WaitingCurrentStatusCountDto> counts = tableModeSeatsGroup.getOrDefault(
              item.getName(),
              Set.of()
          );

          int tablePeopleCount = counts.stream()
              .mapToInt(WaitingCurrentStatusCountDto::getTotalPersonCount)
              .sum();
          Integer expectedWaitingTime = getExpectedWaitingTime(item, counts.size());
          SeatOption seatOption = SeatOption.builder()
              .minSeatCount(item.getMinSeatCount())
              .maxSeatCount(item.getMaxSeatCount())
              .isTakeOut(item.getIsTakeOut())
              .build();

          return SeatsCurrentStatus.builder()
              .id(item.getId())
              .seatOptionName(item.getName())
              .seatOption(seatOption)
              .teamCount(counts.size())
              .peopleCount(tablePeopleCount)
              .expectedWaitingTime(expectedWaitingTime)
              .isUsedExpectedWaitingPeriod(item.getIsUsedExpectedWaitingPeriod())
              .build();
        })
        .toList();

    return TableCurrentStatusDto.builder()
        .teamCount(currentStatusCountDtos.size())
        .peopleCount(peopleCount)
        .seatsCurrentStatuses(currentStatuses)
        .build();
  }

  private List<SeatOptions> getModeSettings(HomeSettingsEntity homeSettings,
      WaitingModeType modeType) {
    if (modeType.isDefault()) {
      return List.of(homeSettings.getDefaultModeSettings());
    }
    return homeSettings.getTableModeSettings();
  }

  private Integer getExpectedWaitingTime(SeatOptions defaultModeSettings, int teamCount) {
    if (defaultModeSettings.isNotUseExpectedWaitingPeriod()) {
      return null;
    }

    return defaultModeSettings.calculateExpectedWaitingPeriod(teamCount + 1);
  }

}
