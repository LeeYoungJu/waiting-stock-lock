package co.wadcorp.waiting.data.service.message;

import co.wadcorp.waiting.data.domain.message.MessageSendHistoryEntity;
import co.wadcorp.waiting.data.domain.message.MessageSendHistoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MessageSendHistoryService {

  private final MessageSendHistoryRepository messageSendHistoryRepository;

  public MessageSendHistoryEntity save(MessageSendHistoryEntity entity) {
    return messageSendHistoryRepository.save(entity);
  }

//  public List<MessageSendHistoryEntity> findAllByWaitingId(String waitingId) {
//    return messageSendHistoryRepository.findAllByWaitingId(waitingId);
//  }

}
