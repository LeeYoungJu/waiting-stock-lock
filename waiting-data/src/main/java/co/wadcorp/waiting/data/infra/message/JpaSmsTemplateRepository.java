package co.wadcorp.waiting.data.infra.message;

import co.wadcorp.waiting.data.domain.message.SendType;
import co.wadcorp.waiting.data.domain.message.SmsTemplateEntity;
import co.wadcorp.waiting.data.domain.message.SmsTemplateRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaSmsTemplateRepository extends SmsTemplateRepository, JpaRepository<SmsTemplateEntity, Long> {

  Optional<SmsTemplateEntity> findBySendTypeAndIsUsed(SendType sendType, boolean useYn);
}
