package co.wadcorp.waiting.data.query.memo;

import static co.wadcorp.waiting.data.domain.memo.QMemoKeywordEntity.memoKeywordEntity;

import co.wadcorp.waiting.data.domain.memo.MemoKeywordEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class MemoKeywordQuery {

  private final JPAQueryFactory queryFactory;

  public List<MemoKeywordEntity> findAllBy(String shopId) {
    return queryFactory
        .select(memoKeywordEntity)
        .from(memoKeywordEntity)
        .where(
            memoKeywordEntity.shopId.eq(shopId),
            memoKeywordEntity.isDeleted.isFalse()
        )
        .fetch();
  }

  public int findNextOrderingBy(String shopId) {
    Integer maxOrdering = queryFactory
        .select(memoKeywordEntity.ordering.max())
        .from(memoKeywordEntity)
        .where(
            memoKeywordEntity.shopId.eq(shopId),
            memoKeywordEntity.isDeleted.isFalse()
        )
        .fetchOne();

    return maxOrdering == null ? 1 : maxOrdering+1;
  }

  public Optional<MemoKeywordEntity> findOneByKeywordId(String keywordId) {
    if(keywordId == null) {
      return Optional.empty();
    }

    return Optional.ofNullable(queryFactory
        .select(memoKeywordEntity)
        .from(memoKeywordEntity)
        .where(
            memoKeywordEntity.keywordId.eq(keywordId),
            memoKeywordEntity.isDeleted.isFalse()
        )
      .fetchOne()
    );
  }
}
