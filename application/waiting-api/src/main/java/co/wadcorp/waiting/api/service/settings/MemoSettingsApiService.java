package co.wadcorp.waiting.api.service.settings;

import co.wadcorp.waiting.api.service.settings.dto.request.MemoKeywordMergeServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.request.MemoKeywordOrderingServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.response.MemoKeywordResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.MemoKeywordListResponse;
import co.wadcorp.waiting.data.domain.memo.MemoKeywordEntity;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.query.memo.MemoKeywordQuery;
import co.wadcorp.waiting.data.service.memo.MemoKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MemoSettingsApiService {

  private final MemoKeywordService memoKeywordService;
  private final MemoKeywordQuery memoKeywordQuery;

  @Transactional
  public MemoKeywordListResponse getMemoKeywords(String shopId) {
    return MemoKeywordListResponse.of(
        memoKeywordQuery.findAllBy(shopId));
  }

  @Transactional
  public MemoKeywordResponse create(String shopId, MemoKeywordMergeServiceRequest request) {
    Integer nextOrdering = memoKeywordQuery.findNextOrderingBy(shopId);

    return MemoKeywordResponse.of(
        memoKeywordService.save(request.toEntity(shopId, nextOrdering))
    );
  }

  public MemoKeywordResponse getMemoKeyword(String keywordId) {
    MemoKeywordEntity memoKeyword = memoKeywordQuery.findOneByKeywordId(keywordId)
        .orElseThrow(() ->
            AppException.ofBadRequest(ErrorCode.NOT_FOUND_MEMO_KEYWORD));

    return MemoKeywordResponse.of(memoKeyword);
  }

  @Transactional
  public MemoKeywordResponse update(String keywordId, MemoKeywordMergeServiceRequest request) {
    MemoKeywordEntity memoKeyword = memoKeywordService.findById(keywordId);
    memoKeyword.updateKeyword(request.getKeyword());
    return MemoKeywordResponse.of(memoKeyword);
  }

  @Transactional
  public void delete(String keywordId) {
    MemoKeywordEntity memoKeyword = memoKeywordService.findById(keywordId);
    memoKeyword.delete();
  }

  @Transactional
  public MemoKeywordListResponse updateMemoKeywordsOrdering(String shopId,
      MemoKeywordOrderingServiceRequest request) {
    return MemoKeywordListResponse.of(
        memoKeywordService.updateMemoKeywordOrdering(shopId, request.toDataDto())
    );
  }
}
