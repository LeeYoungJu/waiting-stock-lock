package co.wadcorp.waiting.data.domain.customer;

import java.util.List;

public interface TermsRepository {

  List<TermsEntity> findAllByIsUsedIsTrue();
}
