package co.wadcorp.waiting.data.domain.message;


import java.util.Optional;

public interface AlimtalkTemplateRepository {
  AlimtalkTemplateEntity save(AlimtalkTemplateEntity entity);

  Optional<AlimtalkTemplateEntity> findBySendTypeAndIsUsed(SendType sendType, boolean useYn);
}
