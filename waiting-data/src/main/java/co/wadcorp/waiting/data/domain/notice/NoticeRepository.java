package co.wadcorp.waiting.data.domain.notice;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository {

  List<NoticeEntity> findAll();

  Optional<NoticeEntity> findById(Long seq);

  NoticeEntity save(NoticeEntity noticeEntity);

  void deleteById(Long seq);

}
