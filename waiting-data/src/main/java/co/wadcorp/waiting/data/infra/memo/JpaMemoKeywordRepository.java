package co.wadcorp.waiting.data.infra.memo;

import co.wadcorp.waiting.data.domain.memo.MemoKeywordEntity;
import co.wadcorp.waiting.data.domain.memo.MemoKeywordRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaMemoKeywordRepository extends MemoKeywordRepository,
    JpaRepository<MemoKeywordEntity, Long> {

}
