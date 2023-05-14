package co.wadcorp.waiting.api.service.settings;

import static org.junit.jupiter.api.Assertions.*;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.controller.settings.dto.request.MemoKeywordCreateRequest;
import co.wadcorp.waiting.api.controller.settings.dto.request.MemoKeywordOrderingRequest;
import co.wadcorp.waiting.api.controller.settings.dto.request.MemoKeywordOrderingRequest.KeywordOrderingDto;
import co.wadcorp.waiting.api.controller.settings.dto.request.MemoKeywordUpdateRequest;
import co.wadcorp.waiting.api.service.settings.dto.response.MemoKeywordListResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.MemoKeywordListResponse.MemoKeywordDto;
import co.wadcorp.waiting.api.service.settings.dto.response.MemoKeywordResponse;
import co.wadcorp.waiting.data.domain.memo.MemoKeywordEntity;
import co.wadcorp.waiting.data.domain.memo.MemoKeywordRepository;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MemoSettingsApiServiceTest extends IntegrationTest {

  @Autowired
  MemoSettingsApiService memoSettingsApiService;

  @Autowired
  MemoKeywordRepository memoKeywordRepository;

  @DisplayName("메모 키워드 리스트를 odering 순서에 맞게 조회한다.")
  @Test
  void getMemoKeywords() {
    // given
    String shopId = "test-shop-id";
    MemoKeywordEntity keyword3 = createTestMemoKeyword(shopId, "단골손님", 3);
    MemoKeywordEntity keyword1 = createTestMemoKeyword(shopId, "창가쪽", 1);
    MemoKeywordEntity keyword2 = createTestMemoKeyword(shopId, "진상", 2);

    // when
    MemoKeywordListResponse response = memoSettingsApiService.getMemoKeywords(shopId);

    // then
    assertEquals(3, response.getKeywords().size());

    MemoKeywordDto responseKeyword1 = response.getKeywords().get(0);
    MemoKeywordDto responseKeyword2 = response.getKeywords().get(1);
    MemoKeywordDto responseKeyword3 = response.getKeywords().get(2);

    assertEquals(keyword1.getOrdering(), responseKeyword1.getOrdering());
    assertEquals(keyword2.getOrdering(), responseKeyword2.getOrdering());
    assertEquals(keyword3.getOrdering(), responseKeyword3.getOrdering());

    assertEquals(keyword1.getKeyword(), responseKeyword1.getKeyword());
    assertEquals(keyword2.getKeyword(), responseKeyword2.getKeyword());
    assertEquals(keyword3.getKeyword(), responseKeyword3.getKeyword());
  }

  @DisplayName("메모 키워드 단건 조회 시 특정 키워드 id로 조회 가능하다.")
  @Test
  void getMemoKeyword() {
    // given
    String shopId = "test-shop-id";
    MemoKeywordEntity memoKeyword = createTestMemoKeyword(shopId, "키워드", 1);
    String keywordId = memoKeyword.getKeywordId();

    // when
    MemoKeywordResponse response = memoSettingsApiService.getMemoKeyword(keywordId);

    // then
    assertEquals(memoKeyword.getKeywordId(), response.getId());
    assertEquals(memoKeyword.getKeyword(), response.getKeyword());
    assertEquals(memoKeyword.getOrdering(), response.getOrdering());
  }

  @DisplayName("메모 키워드 단건 조회 시 존재하지 않는 keywordId 값으로 조회하면 NOT_FOUND_MEMO_KEYWORD 예외가 발생한다.")
  @Test
  void getMemoKeywordWithUnknownId() {
    // given
    String shopId = "test-shop-id";
    MemoKeywordEntity memoKeyword = createTestMemoKeyword(shopId, "키워드", 1);
    String requestKeywordId = "unknown-keyword-id";

    // when then
    assertThrows(AppException.class,
        () -> memoSettingsApiService.getMemoKeyword(requestKeywordId));
  }

  @DisplayName("메모 키워드 생성 시 생성된 키워드의 ordering 값은 기존 max ordering + 1 이다.")
  @Test
  void createMemoKeyword() {
    // given
    String shopId = "test-shop-id";
    MemoKeywordCreateRequest request1 = createMemoKeywordCreateRequest("키워드1");
    MemoKeywordCreateRequest request2 = createMemoKeywordCreateRequest("키워드2");
    MemoKeywordCreateRequest request3 = createMemoKeywordCreateRequest("키워드3");

    // when
    memoSettingsApiService.create(shopId, request1.toServiceRequest());
    memoSettingsApiService.create(shopId, request2.toServiceRequest());
    memoSettingsApiService.create(shopId, request3.toServiceRequest());

    // then
    List<MemoKeywordEntity> entities = memoKeywordRepository.findAllByShopIdAndIsDeleted(
        shopId, false).stream().sorted().toList();

    assertEquals(3, entities.size());
    assertEquals(1, entities.get(0).getOrdering());
    assertEquals(2, entities.get(1).getOrdering());
    assertEquals(3, entities.get(2).getOrdering());
  }

  @DisplayName("메모 단건 수정을 할 수 있다.")
  @Test
  void updateMemoKeyword() {
    // given
    String shopId = "test-shop-id";
    String originKeyword = "키워드";
    String updateKeyword = "수정키워드";
    MemoKeywordEntity memoKeyword = createTestMemoKeyword(shopId, originKeyword, 1);
    String keywordId = memoKeyword.getKeywordId();
    MemoKeywordUpdateRequest request = MemoKeywordUpdateRequest.builder()
        .keyword(updateKeyword)
        .build();

    // when
    MemoKeywordResponse response = memoSettingsApiService.update(keywordId,
        request.toServiceRequest());

    // then
    MemoKeywordEntity updatedEntity = memoKeywordRepository.findByKeywordId(response.getId())
        .orElseThrow(() -> AppException.ofBadRequest(ErrorCode.NOT_FOUND_MEMO_KEYWORD));
    assertEquals(updateKeyword, updatedEntity.getKeyword());
  }

  @DisplayName("메모 키워드 단건을 삭제할 수 있다.")
  @Test
  void deleteMemoKeyword() {
    // given
    String shopId = "test-shop-id";
    MemoKeywordEntity keyword1 = createTestMemoKeyword(shopId, "키워드1", 1);
    MemoKeywordEntity keyword2 = createTestMemoKeyword(shopId, "키워드2", 2);
    MemoKeywordEntity keyword3 = createTestMemoKeyword(shopId, "키워드3", 3);

    // when
    memoSettingsApiService.delete(keyword2.getKeywordId());

    // then
    List<MemoKeywordEntity> entities = memoKeywordRepository.findAllByShopIdAndIsDeleted(
        shopId, false).stream().sorted().toList();
    assertEquals(2, entities.size());
    assertEquals(keyword1.getKeywordId(), entities.get(0).getKeywordId());
    assertEquals(keyword3.getKeywordId(), entities.get(1).getKeywordId());
  }

  @DisplayName("메모 키워드 순서를 재정렬할 수 있다.")
  @Test
  void updateMemoKeywordsOrdering() {
    // given
    String shopId = "test-shop-id";
    MemoKeywordEntity keyword1 = createTestMemoKeyword(shopId, "키워드1", 1);
    MemoKeywordEntity keyword2 = createTestMemoKeyword(shopId, "키워드2", 2);
    MemoKeywordEntity keyword3 = createTestMemoKeyword(shopId, "키워드3", 3);

    // 키워드1, 키워드2, 키워드3 ==> 키워드2, 키워드3, 키워드1
    MemoKeywordOrderingRequest request = MemoKeywordOrderingRequest.builder()
        .keywords(List.of(
            KeywordOrderingDto.builder()
                .id(keyword2.getKeywordId())
                .ordering(1)
                .build(),
            KeywordOrderingDto.builder()
                .id(keyword3.getKeywordId())
                .ordering(2)
                .build(),
            KeywordOrderingDto.builder()
                .id(keyword1.getKeywordId())
                .ordering(3)
                .build()
        ))
        .build();

    // when
    memoSettingsApiService.updateMemoKeywordsOrdering(shopId, request.toServiceRequest());

    // then
    List<MemoKeywordEntity> entities = memoKeywordRepository.findAllByShopIdAndIsDeleted(
        shopId,false).stream().sorted().toList();

    assertEquals("키워드2", entities.get(0).getKeyword());
    assertEquals("키워드3", entities.get(1).getKeyword());
    assertEquals("키워드1", entities.get(2).getKeyword());
  }

  private MemoKeywordEntity createTestMemoKeyword(String shopId, String keyword, int ordering) {
    MemoKeywordEntity memoKeyword = MemoKeywordEntity.builder()
        .keywordId(UUIDUtil.shortUUID())
        .shopId(shopId)
        .keyword(keyword)
        .ordering(ordering)
        .build();

    return memoKeywordRepository.save(memoKeyword);
  }

  private MemoKeywordCreateRequest createMemoKeywordCreateRequest(String keyword) {
    return MemoKeywordCreateRequest.builder()
        .id(UUIDUtil.shortUUID())
        .keyword("새로운키워드")
        .build();
  }

}