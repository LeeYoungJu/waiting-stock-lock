package co.wadcorp.waiting.data.query.waiting;

import static co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelProcessingStatus.CREATED;
import static co.wadcorp.waiting.data.domain.waiting.cancel.QAutoCancelTargetEntity.autoCancelTargetEntity;

import co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelTargetEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
@Transactional(readOnly = true)
public class AutoCancelTargetQueryRepository {

  private final JPAQueryFactory queryFactory;

  public List<AutoCancelTargetEntity> findByExpectedTimeWithLimit(ZonedDateTime now, long limit) {
    return queryFactory
        .select(autoCancelTargetEntity)
        .from(autoCancelTargetEntity)
        .where(
            autoCancelTargetEntity.processingStatus.eq(CREATED),
            autoCancelTargetEntity.expectedCancelDateTime.before(now)
        )
        .limit(limit)
        .orderBy(autoCancelTargetEntity.seq.asc())
        .fetch();
  }

}
