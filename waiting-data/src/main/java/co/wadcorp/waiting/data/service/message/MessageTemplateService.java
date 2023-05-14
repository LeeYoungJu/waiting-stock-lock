package co.wadcorp.waiting.data.service.message;

import co.wadcorp.waiting.data.domain.message.AlimtalkTemplateEntity;
import co.wadcorp.waiting.data.domain.message.AlimtalkTemplateRepository;
import co.wadcorp.waiting.data.domain.message.MessageTemplate;
import co.wadcorp.waiting.data.domain.message.SendType;
import co.wadcorp.waiting.data.domain.message.SmsTemplateEntity;
import co.wadcorp.waiting.data.domain.message.SmsTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MessageTemplateService {

  private final AlimtalkTemplateRepository alimtalkTemplateRepository;
  private final SmsTemplateRepository smsTemplateRepository;

  public MessageTemplate getMessageTemplate(SendType sendType, boolean isUsed) {
    AlimtalkTemplateEntity alimtalkTemplate = alimtalkTemplateRepository.findBySendTypeAndIsUsed(sendType, isUsed)
        .orElse(AlimtalkTemplateEntity.EMPTY_TEMPLATE);

    SmsTemplateEntity smsTemplateEntity = smsTemplateRepository.findBySendTypeAndIsUsed(sendType, isUsed)
        .orElse(SmsTemplateEntity.EMPTY_TEMPLATE);

    return new MessageTemplate(alimtalkTemplate.getTemplateName(), alimtalkTemplate.getTemplateCode(),
        alimtalkTemplate.getTemplateContent(), smsTemplateEntity.getTemplateContent());
  }
}
