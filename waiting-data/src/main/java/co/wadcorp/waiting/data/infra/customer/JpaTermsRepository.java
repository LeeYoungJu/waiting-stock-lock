package co.wadcorp.waiting.data.infra.customer;

import co.wadcorp.waiting.data.domain.customer.TermsEntity;
import co.wadcorp.waiting.data.domain.customer.TermsRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTermsRepository extends JpaRepository<TermsEntity, Long>, TermsRepository {

}
