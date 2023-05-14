package co.wadcorp.waiting.infra.message;


import co.wadcorp.waiting.infra.message.dto.SendMessageRequest;
import co.wadcorp.waiting.infra.message.dto.SendMessageResponse;

public interface MessageSendClient {

  SendMessageResponse send(SendMessageRequest sendMessageRequest);
}
