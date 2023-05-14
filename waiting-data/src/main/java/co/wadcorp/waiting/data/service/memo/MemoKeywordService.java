package co.wadcorp.waiting.data.service.memo;

import co.wadcorp.libs.stream.StreamUtils;
import co.wadcorp.waiting.data.domain.memo.MemoKeywordEntity;
import co.wadcorp.waiting.data.domain.memo.MemoKeywordOrderingUpdateDto;
import co.wadcorp.waiting.data.domain.memo.MemoKeywordRepository;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemoKeywordService {

  private final MemoKeywordRepository memoKeywordRepository;

  public MemoKeywordEntity save(MemoKeywordEntity entity) {
    return memoKeywordRepository.save(entity);
  }

  public MemoKeywordEntity findById(String keywordId) {
    return memoKeywordRepository.findByKeywordId(keywordId)
        .orElseThrow(() -> AppException.ofBadRequest(ErrorCode.NOT_FOUND_MEMO_KEYWORD));
  }

  public List<MemoKeywordEntity> updateMemoKeywordOrdering(String shopId,
      MemoKeywordOrderingUpdateDto memoKeywordOrderingUpdateDto) {
    List<MemoKeywordEntity> keywords = memoKeywordRepository.findAllByShopIdAndIsDeleted(shopId,
        false);
    Map<String, MemoKeywordEntity> keywordsMap = StreamUtils.convertToMap(
        keywords, MemoKeywordEntity::getKeywordId);

    memoKeywordOrderingUpdateDto.getOrderingDataList().stream()
        .forEach(orderingData -> {
          MemoKeywordEntity memoKeyword = keywordsMap.get(orderingData.getKeywordId());
          memoKeyword.updateOrdering(orderingData.getOrdering());
        });

    return keywords;
  }
}

