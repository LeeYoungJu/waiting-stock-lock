package co.wadcorp.waiting.data.query.customer;

import static co.wadcorp.waiting.data.domain.customer.QCustomerEntity.customerEntity;
import static co.wadcorp.waiting.data.domain.waiting.QWaitingEntity.waitingEntity;

import co.wadcorp.waiting.data.domain.customer.CustomerEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class CustomerQueryRepository {

  private final JPAQueryFactory queryFactory;

  public Optional<CustomerEntity> getCustomerBy(String waitingId) {
    return Optional.ofNullable(
        queryFactory
            .selectFrom(customerEntity)
            .innerJoin(waitingEntity).on(customerEntity.seq.eq(waitingEntity.customerSeq))
            .where(waitingEntity.waitingId.eq(waitingId))
            .fetchFirst()
    );
  }

}
