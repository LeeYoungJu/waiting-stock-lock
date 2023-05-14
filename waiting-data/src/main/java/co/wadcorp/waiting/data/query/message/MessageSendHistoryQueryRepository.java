package co.wadcorp.waiting.data.query.message;

import static co.wadcorp.waiting.data.domain.message.QMessageSendHistoryEntity.messageSendHistoryEntity;

import co.wadcorp.waiting.data.domain.message.SendType;
import co.wadcorp.waiting.data.query.message.dto.MessageSendHistoryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageSendHistoryQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public Optional<MessageSendHistoryDto> findByWaitingIdInAndSendType(String waitingId, SendType sendType) {
    return jpaQueryFactory.select(Projections.fields(MessageSendHistoryDto.class,
            messageSendHistoryEntity.seq,
            messageSendHistoryEntity.seq,
            messageSendHistoryEntity.waitingHistorySeq,
            messageSendHistoryEntity.waitingId,
            messageSendHistoryEntity.requestId,
            messageSendHistoryEntity.encCustomerPhone,
            messageSendHistoryEntity.sendChannel,
            messageSendHistoryEntity.templateName,
            messageSendHistoryEntity.sendType,
            messageSendHistoryEntity.templateCode,
            messageSendHistoryEntity.content,
            messageSendHistoryEntity.buttons,
            messageSendHistoryEntity.status,
            messageSendHistoryEntity.failCode,
            messageSendHistoryEntity.failReason,
            messageSendHistoryEntity.sendDateTime
        ))
        .from(messageSendHistoryEntity)
        .where(messageSendHistoryEntity.waitingId.eq(waitingId)
            .and(messageSendHistoryEntity.sendType.eq(sendType)))
        .fetch()
        .stream()
        .findFirst();
  }

  public List<MessageSendHistoryDto> findByWaitingIdInAndSendType(List<String> waitingId, SendType sendType) {

    return jpaQueryFactory.select(Projections.fields(MessageSendHistoryDto.class,
            messageSendHistoryEntity.seq,
            messageSendHistoryEntity.seq,
            messageSendHistoryEntity.waitingHistorySeq,
            messageSendHistoryEntity.waitingId,
            messageSendHistoryEntity.requestId,
            messageSendHistoryEntity.encCustomerPhone,
            messageSendHistoryEntity.sendChannel,
            messageSendHistoryEntity.templateName,
            messageSendHistoryEntity.sendType,
            messageSendHistoryEntity.templateCode,
            messageSendHistoryEntity.content,
            messageSendHistoryEntity.buttons,
            messageSendHistoryEntity.status,
            messageSendHistoryEntity.failCode,
            messageSendHistoryEntity.failReason,
            messageSendHistoryEntity.sendDateTime
        ))
        .from(messageSendHistoryEntity)
        .where(messageSendHistoryEntity.waitingId.in(waitingId)
            .and(messageSendHistoryEntity.sendType.eq(sendType)))
        .fetch();
  }
}
