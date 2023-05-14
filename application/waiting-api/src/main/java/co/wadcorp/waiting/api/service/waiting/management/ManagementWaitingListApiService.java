package co.wadcorp.waiting.api.service.waiting.management;

import static co.wadcorp.libs.stream.StreamUtils.convert;

import co.wadcorp.waiting.api.model.waiting.request.WaitingListRequest;
import co.wadcorp.waiting.api.model.waiting.response.WaitingListResponse;
import co.wadcorp.waiting.api.model.waiting.vo.PageVO;
import co.wadcorp.waiting.api.model.waiting.vo.WaitingVO;
import co.wadcorp.waiting.data.domain.message.SendType;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.query.message.MessageSendHistoryQueryRepository;
import co.wadcorp.waiting.data.query.message.dto.MessageSendHistoryDto;
import co.wadcorp.waiting.data.query.order.WaitingOrderQueryRepository;
import co.wadcorp.waiting.data.query.order.dto.WaitingOrderDto;
import co.wadcorp.waiting.data.query.waiting.WaitingHistoryQueryRepository;
import co.wadcorp.waiting.data.query.waiting.WaitingPageListQueryRepository;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingCalledCountDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingCalledHistoryDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingDto;
import co.wadcorp.waiting.data.service.settings.HomeSettingsService;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ManagementWaitingListApiService {

  private final WaitingPageListQueryRepository waitingPageListQueryRepository;
  private final WaitingOrderQueryRepository waitingOrderQueryRepository;

  private final HomeSettingsService homeSettingsService;

  private final WaitingHistoryQueryRepository waitingHistoryQueryRepository;
  private final MessageSendHistoryQueryRepository messageSendHistoryQueryRepository;

  public WaitingListResponse getWaitingList(String shopId, LocalDate operationDate,
      WaitingListRequest request) {

    Page<WaitingDto> waitingDtoPage = getWaitingDtos(shopId, operationDate, request);
    List<WaitingDto> content = waitingDtoPage.getContent();

    // 호출 횟수 구하기
    Map<String, WaitingCalledCountDto> calledCountDtoMap = getWaitingCalledCountDtoMap(content);
    // 입장 임박 알림톡 발송 여부 구하기
    Map<String, Boolean> isSentReadyToEnterAlarm = getIsSentReadyToEnterAlarmMap(content);
    // 선주문 구하기
    Map<String, WaitingOrderDto> waitingOrderDtoMap = getWaitingOrderDtoMap(content);

    Pageable pageable = waitingDtoPage.getPageable();
    return WaitingListResponse.builder()
        .page(new PageVO(waitingDtoPage.getTotalElements(), request.getPage(),

            pageable.getPageSize()))
        .waiting(content.stream()
            .map(item ->
                WaitingVO.toDto(
                    item,
                    calledCountDtoMap.getOrDefault(item.getWaitingId(),
                        WaitingCalledCountDto.EMPTY),
                    isSentReadyToEnterAlarm.getOrDefault(item.getWaitingId(), false),
                    waitingOrderDtoMap.get(item.getWaitingId())
                ))
            .toList())
        .build();
  }

  private Page<WaitingDto> getWaitingDtos(String shopId, LocalDate operationDate,
      WaitingListRequest request) {

    if (request.isTableMode()) {
      HomeSettingsEntity homeSettings = homeSettingsService.getHomeSettings(shopId);
      List<String> tableModeSeatOptions = homeSettings.getTableModeSeatOptionNames(
          request.getSeatOptionId()
      );

      return waitingPageListQueryRepository.getTableWaitingList(
          shopId,
          operationDate,
          WaitingStatus.valueOf(request.getWaitingStatus()),
          tableModeSeatOptions,
          PageRequest.of(request.pageByPageRequest(), request.getLimit())
      );
    }

    return waitingPageListQueryRepository.getDefaultWaitingList(
        shopId, operationDate, WaitingStatus.valueOf(request.getWaitingStatus()),
        PageRequest.of(request.pageByPageRequest(), request.getLimit()));
  }

  private Map<String, Boolean> getIsSentReadyToEnterAlarmMap(List<WaitingDto> content) {
    List<String> waitingIds = convert(content, WaitingDto::getWaitingId);

    List<MessageSendHistoryDto> messageSendHistoryDtos = messageSendHistoryQueryRepository.findByWaitingIdInAndSendType(
        waitingIds,
        SendType.WAITING_READY_TO_ENTER
    );

    return messageSendHistoryDtos.stream()
        .collect(Collectors.toMap(
            MessageSendHistoryDto::getWaitingId,
            item -> true,
            (p1, p2) -> p1)
        );
  }

  private Map<String, WaitingCalledCountDto> getWaitingCalledCountDtoMap(
      List<WaitingDto> content
  ) {
    // 호출 횟수 조회 Query
    List<String> waitingIds = convert(content, WaitingDto::getWaitingId);
    List<WaitingCalledHistoryDto> calledHistory = waitingHistoryQueryRepository.getWaitingCalledHistory(
        waitingIds);

    return calledHistory
        .stream()
        .collect(Collectors.groupingBy(WaitingCalledHistoryDto::getWaitingId, withStatistics()));
  }

  private Map<String, WaitingOrderDto> getWaitingOrderDtoMap(List<WaitingDto> content) {
    List<String> waitingIds = convert(content, WaitingDto::getWaitingId);

    List<WaitingOrderDto> waitingOrderDtos = waitingOrderQueryRepository.findByWaitingIds(
        waitingIds);

    return waitingOrderDtos.stream()
        .collect(Collectors.toMap(
            WaitingOrderDto::getWaitingId,
            item -> item,
            (item1, item2) -> item1)
        );
  }

  private Collector<WaitingCalledHistoryDto, ?, WaitingCalledCountDto> withStatistics() {
    class Accumulate {

      long count = 0;
      ZonedDateTime lastCalledDateTime;

      void accumulate(WaitingCalledHistoryDto waitingCalledHistoryDto) {
        count++;
        if (lastCalledDateTime == null || waitingCalledHistoryDto.getRegDateTime().isAfter(
            lastCalledDateTime)) {
          lastCalledDateTime = waitingCalledHistoryDto.getRegDateTime();
        }
      }

      Accumulate merge(Accumulate another) {
        count += another.count;
        if (lastCalledDateTime == null || another.lastCalledDateTime.isAfter(lastCalledDateTime)) {
          lastCalledDateTime = another.lastCalledDateTime;
        }
        return this;
      }

      WaitingCalledCountDto finish() {
        return new WaitingCalledCountDto(count, lastCalledDateTime);
      }
    }
    return Collector.of(Accumulate::new, Accumulate::accumulate, Accumulate::merge,
        Accumulate::finish);
  }

}
