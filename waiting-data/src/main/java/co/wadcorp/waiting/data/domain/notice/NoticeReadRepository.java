package co.wadcorp.waiting.data.domain.notice;

import java.util.List;

public interface NoticeReadRepository {

  <S extends NoticeReadEntity> List<S> saveAll(Iterable<S> noticeReadEntities);

}
