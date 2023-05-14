package co.wadcorp.waiting.data.domain.message;


import java.util.List;
import java.util.Optional;

public interface MessageSendHistoryRepository {
  MessageSendHistoryEntity save(MessageSendHistoryEntity entity);

  Optional<MessageSendHistoryEntity> findByWaitingHistorySeq(Long waitingHistorySeq);

  List<MessageSendHistoryEntity> findAllByWaitingId(String waitingId);
}
