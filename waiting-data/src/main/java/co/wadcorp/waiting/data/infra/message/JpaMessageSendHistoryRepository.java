package co.wadcorp.waiting.data.infra.message;

import co.wadcorp.waiting.data.domain.message.MessageSendHistoryEntity;
import co.wadcorp.waiting.data.domain.message.MessageSendHistoryRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaMessageSendHistoryRepository extends MessageSendHistoryRepository, JpaRepository<MessageSendHistoryEntity, Long> {

  Optional<MessageSendHistoryEntity> findByWaitingHistorySeq(Long waitingHistorySeq);

  List<MessageSendHistoryEntity> findAllByWaitingId(String waitingId);
}
