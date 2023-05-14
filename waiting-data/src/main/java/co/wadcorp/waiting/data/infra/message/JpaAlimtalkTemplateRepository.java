package co.wadcorp.waiting.data.infra.message;

import co.wadcorp.waiting.data.domain.message.AlimtalkTemplateEntity;
import co.wadcorp.waiting.data.domain.message.AlimtalkTemplateRepository;
import co.wadcorp.waiting.data.domain.message.SendType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAlimtalkTemplateRepository extends AlimtalkTemplateRepository, JpaRepository<AlimtalkTemplateEntity, Long> {


  Optional<AlimtalkTemplateEntity> findBySendTypeAndIsUsed(SendType sendType, boolean useYn);
}
