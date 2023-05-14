package co.wadcorp.waiting.api.service.waiting.management;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.controller.waiting.management.dto.request.WaitingMemoSaveRequest;
import co.wadcorp.waiting.data.domain.memo.WaitingMemoEntity;
import co.wadcorp.waiting.data.domain.memo.WaitingMemoHistoryEntity;
import co.wadcorp.waiting.data.domain.memo.WaitingMemoHistoryRepository;
import co.wadcorp.waiting.data.domain.memo.WaitingMemoRepository;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ManagementWaitingMemoApiServiceTest extends IntegrationTest {

  @Autowired
  private ManagementWaitingMemoApiService managementWaitingMemoApiService;

  @Autowired
  private WaitingMemoRepository waitingMemoRepository;

  @Autowired
  private WaitingMemoHistoryRepository waitingMemoHistoryRepository;

  @DisplayName("웨이팅별 메모 저장 시 기존에 저장된 데이터가 없으면 새로 저장되고 history가 쌓인다.")
  @Test
  void saveWaitingMemo() {
    // given
    String shopId = "test-shop-id";
    String waitingId = "test-waiting-id";
    WaitingMemoSaveRequest request = createWaitingMemoSaveRequest(waitingId,"단골, 진상, 창가선호");

    // when
    managementWaitingMemoApiService.save(shopId, request.toServiceRequest());

    // then
    WaitingMemoEntity waitingMemo = waitingMemoRepository.findByWaitingId(waitingId)
        .orElseThrow(() -> AppException.ofBadRequest(ErrorCode.NOT_FOUND_WAITING_MEMO));
    assertEquals("단골, 진상, 창가선호", waitingMemo.getMemo());

    List<WaitingMemoHistoryEntity> waitingMemoHistories = waitingMemoHistoryRepository.findAllByWaitingId(
        waitingId);
    assertThat(waitingMemoHistories).hasSize(1)
        .extracting("memo")
        .containsExactly("단골, 진상, 창가선호");
  }

  @DisplayName("웨이팅별 메모 저장 시 기존에 저장된 데이터가 있으면 메모 내용을 수정하고 history가 쌓인다.")
  @Test
  void saveWaitingMemoWhenAlreadyExists() {
    // given
    String shopId = "test-shop-id";
    String waitingId = "test-waiting-id";
    WaitingMemoSaveRequest request1 = createWaitingMemoSaveRequest(waitingId,"단골, 진상, 창가선호");
    WaitingMemoSaveRequest request2 = createWaitingMemoSaveRequest(waitingId,"단골, 진상, 창가선호, 주차");
    managementWaitingMemoApiService.save(shopId, request1.toServiceRequest());

    // when
    managementWaitingMemoApiService.save(shopId, request2.toServiceRequest());

    // then
    WaitingMemoEntity waitingMemo = waitingMemoRepository.findByWaitingId(waitingId)
        .orElseThrow(() -> AppException.ofBadRequest(ErrorCode.NOT_FOUND_WAITING_MEMO));
    assertEquals("단골, 진상, 창가선호, 주차", waitingMemo.getMemo());

    List<WaitingMemoHistoryEntity> waitingMemoHistories = waitingMemoHistoryRepository.findAllByWaitingId(
        waitingId).stream().sorted(Comparator.comparing(WaitingMemoHistoryEntity::getSeq)).toList();
    assertThat(waitingMemoHistories).hasSize(2)
        .extracting("memo")
        .containsExactly("단골, 진상, 창가선호", "단골, 진상, 창가선호, 주차");
  }

  @DisplayName("웨이팅별 메모 삭제 시 웨이팅의 메모는 빈값이 되고 history가 쌓인다.")
  @Test
  void deleteWaitingMemo() {
    // given
    String shopId = "test-shop-id";
    String waitingId = "test-waiting-id";
    WaitingMemoSaveRequest request = createWaitingMemoSaveRequest(waitingId,"단골, 진상, 창가선호");
    managementWaitingMemoApiService.save(shopId, request.toServiceRequest());

    // when
    managementWaitingMemoApiService.delete(waitingId);

    // then
    WaitingMemoEntity waitingMemo = waitingMemoRepository.findByWaitingId(waitingId)
        .orElseThrow(() -> AppException.ofBadRequest(ErrorCode.NOT_FOUND_WAITING_MEMO));
    assertEquals("", waitingMemo.getMemo());

    List<WaitingMemoHistoryEntity> waitingMemoHistories = waitingMemoHistoryRepository.findAllByWaitingId(
        waitingId).stream().sorted(Comparator.comparing(WaitingMemoHistoryEntity::getSeq)).toList();
    assertThat(waitingMemoHistories).hasSize(2)
        .extracting("memo")
        .containsExactly("단골, 진상, 창가선호", "");
  }

  @DisplayName("웨이팅별 메모 삭제 시 이전에 저장된 메모가 없으면 NOT_FOUND_WAITING_MEMO 예외가 발생한다.")
  @Test
  void deleteWaitingMemoNotFoundMemo() {
    // given
    String waitingId = "test-waiting-id";

    // when
    assertThrows(AppException.class, () -> managementWaitingMemoApiService.delete(waitingId));
  }

  private WaitingMemoSaveRequest createWaitingMemoSaveRequest(String waitingId, String memo) {
    return WaitingMemoSaveRequest.builder()
        .waitingId(waitingId)
        .memo(memo)
        .build();
  }

  private WaitingMemoEntity createTestWaitingMemo(String shopId, String waitingId, String memo) {
    WaitingMemoEntity entity = WaitingMemoEntity.builder()
        .shopId(shopId)
        .waitingId(waitingId)
        .memo(memo)
        .build();

    return waitingMemoRepository.save(entity);
  }

}