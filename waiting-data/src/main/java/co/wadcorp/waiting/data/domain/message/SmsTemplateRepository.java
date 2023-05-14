package co.wadcorp.waiting.data.domain.message;


import java.util.Optional;

public interface SmsTemplateRepository {
  SmsTemplateEntity save(SmsTemplateEntity entity);

  Optional<SmsTemplateEntity> findBySendTypeAndIsUsed(SendType sendType, boolean useYn);
}
