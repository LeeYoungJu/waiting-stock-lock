package co.wadcorp.waiting.data.infra.notice;

import co.wadcorp.waiting.data.domain.notice.NoticeReadEntity;
import co.wadcorp.waiting.data.domain.notice.NoticeReadId;
import co.wadcorp.waiting.data.domain.notice.NoticeReadRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaNoticeReadRepository extends NoticeReadRepository, JpaRepository<NoticeReadEntity, NoticeReadId> {

}
