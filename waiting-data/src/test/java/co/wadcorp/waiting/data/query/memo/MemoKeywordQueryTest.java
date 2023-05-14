package co.wadcorp.waiting.data.query.memo;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.IntegrationTest;
import co.wadcorp.waiting.data.domain.memo.MemoKeywordEntity;
import co.wadcorp.waiting.data.domain.memo.MemoKeywordRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MemoKeywordQueryTest extends IntegrationTest {

  @Autowired
  private MemoKeywordQuery memoKeywordQuery;

  @Autowired
  private MemoKeywordRepository memoKeywordRepository;

  @DisplayName("shopId를 기준을 메모 키워드 리스트를 조회한다.")
  @Test
  void findAllBy() {
    // given
    String shopId = "test-shop-id";
    createTestMemoKeyword(shopId, "키워드1", 1);
    createTestMemoKeyword(shopId, "키워드2", 2);
    createTestMemoKeyword(shopId, "키워드3", 3);

    // when
    List<MemoKeywordEntity> entities = memoKeywordQuery.findAllBy(shopId);

    // then
    assertThat(entities).hasSize(3)
        .extracting("keyword")
        .contains("키워드1", "키워드2", "키워드3");
  }

  @DisplayName("메모 키워드 저장 시 저장 될 ordering 값을 구할 수 있다.")
  @Test
  void findNextOrderingBy() {
    // given
    String shopId = "test-shop-id";
    createTestMemoKeyword(shopId, "키워드1", 1);

    // when
    int nextOrdering = memoKeywordQuery.findNextOrderingBy(shopId);

    // then
    assertEquals(2, nextOrdering);
  }

  @DisplayName("nextOrdering 값을 구할 때 기존에 저장된 메모 키워드 데이터가 없으면 1을 반환한다.")
  @Test
  void findNextOrderingByWhenNoData() {
    // given
    String shopId = "test-shop-id";

    // when
    int nextOrdering = memoKeywordQuery.findNextOrderingBy(shopId);

    // then
    assertEquals(1, nextOrdering);
  }

  @DisplayName("nextOrdering 값을 구할 때 중간 값이 비어도 (최대값+1)을 반환한다.")
  @Test
  void findNextOrderingByWhenNoMiddleData() {
    // given
    String shopId = "test-shop-id";
    createTestMemoKeyword(shopId, "키워드1", 1);
    createTestMemoKeyword(shopId, "키워드3", 3);

    // when
    int nextOrdering = memoKeywordQuery.findNextOrderingBy(shopId);

    // then
    assertEquals(4, nextOrdering);
  }

  @DisplayName("키워드 id를 기준으로 단건 메모 키워드를 조회할 수 있다.")
  @Test
  void findOneByKeywordId() {
    // given
    String shopId = "test-shop-id";
    MemoKeywordEntity keyword = createTestMemoKeyword(shopId, "키워드1", 1);

    // when
    Optional<MemoKeywordEntity> findKeyword = memoKeywordQuery.findOneByKeywordId(
        keyword.getKeywordId());

    // then
    assertEquals("키워드1", findKeyword.get().getKeyword());
  }

  @DisplayName("단건 메모 키워드를 조회 시 알 수 없는 id면 empty optional을 반환한다.")
  @Test
  void findOneByKeywordIdWithUnknownId() {
    // given
    String shopId = "test-shop-id";
    String unknownKeywordId = "unknown-keyword-id";

    // when
    Optional<MemoKeywordEntity> findKeyword = memoKeywordQuery.findOneByKeywordId(
        unknownKeywordId);

    // then
    assertTrue(findKeyword.isEmpty());
  }

  @DisplayName("단건 메모 키워드를 조회 시 id가 null이면 empty optional을 반환한다.")
  @Test
  void findOneByKeywordIdWithNullId() {
    // when
    Optional<MemoKeywordEntity> findKeyword = memoKeywordQuery.findOneByKeywordId(null);

    // then
    assertTrue(findKeyword.isEmpty());
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

}