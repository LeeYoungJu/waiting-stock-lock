package co.wadcorp.waiting.data.infra.notice;

import co.wadcorp.waiting.data.domain.notice.NoticeEntity;
import co.wadcorp.waiting.data.domain.notice.NoticeRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaNoticeRepository extends NoticeRepository, JpaRepository<NoticeEntity, Long> {

}
