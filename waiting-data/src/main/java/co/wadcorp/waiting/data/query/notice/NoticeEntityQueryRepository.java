package co.wadcorp.waiting.data.query.notice;

import co.wadcorp.waiting.data.domain.notice.NoticeVO;
import co.wadcorp.waiting.data.domain.notice.QNoticeEntity;
import co.wadcorp.waiting.data.domain.notice.QNoticeReadEntity;
import co.wadcorp.waiting.data.domain.notice.QNoticeVO;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeEntityQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public List<NoticeVO> findAllExposedNotice(String shopId) {
    var nowDateTime = ZonedDateTimeUtils.nowOfSeoul();

    var noticeEntity = QNoticeEntity.noticeEntity;
    var noticeReadEntity = QNoticeReadEntity.noticeReadEntity;

    return jpaQueryFactory.select(new QNoticeVO(
            noticeEntity.seq, noticeEntity.noticeType, noticeEntity.noticeTitle,
            noticeEntity.noticePreview, noticeEntity.noticeContent, noticeEntity.regDateTime,
            noticeReadEntity.noticeReadId))
        .from(noticeEntity)
        .leftJoin(noticeReadEntity)
        .on(noticeEntity.seq.eq(noticeReadEntity.noticeReadId.noticeSeq)
            .and(noticeReadEntity.noticeReadId.shopId.eq(shopId)))
        .where(noticeEntity.isEnable
            .and(noticeEntity.openDateTime.before(nowDateTime))
            .and(noticeEntity.closeDateTime.after(nowDateTime)
                .or(noticeEntity.isNoticeAlways))
        ).fetch();
  }

}
