package co.wadcorp.waiting.data.domain.memo;

import java.util.List;
import java.util.Optional;

public interface MemoKeywordRepository {

  MemoKeywordEntity save(MemoKeywordEntity entity);

  Optional<MemoKeywordEntity> findByKeywordId(String keywordId);

  List<MemoKeywordEntity> findAllByShopIdAndIsDeleted(String shopId, boolean isDeleted);
}
