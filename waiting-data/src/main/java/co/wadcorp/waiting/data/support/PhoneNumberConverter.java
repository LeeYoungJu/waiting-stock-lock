package co.wadcorp.waiting.data.support;

import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.libs.util.EncryptUtils;
import co.wadcorp.libs.vault.VaultKey;
import co.wadcorp.libs.vault.response.VaultService;
import jakarta.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * PhoneNumber <-> DB 간 변환
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class PhoneNumberConverter implements AttributeConverter<PhoneNumber, String> {

  private final VaultService awsSecretManager;

  @Override
  public String convertToDatabaseColumn(PhoneNumber phoneNumber) {
    if (phoneNumber == null) {
      return null;
    }

    String source = null;
    if (phoneNumber.isValid()) {
      source = phoneNumber.getE164();
    }
    return EncryptUtils.encrypt(source, awsSecretManager.getKey(VaultKey.PHONE));
  }

  @Override
  public PhoneNumber convertToEntityAttribute(String dbData) {
    if (StringUtils.isBlank(dbData)) {
      return null;
    }

    String strPhoneNumber = EncryptUtils.decrypt(dbData, awsSecretManager.getKey(VaultKey.PHONE));
    if (StringUtils.isBlank(strPhoneNumber)) {
      return null;
    }

    PhoneNumber phoneNumber = new PhoneNumber(strPhoneNumber);
    if (phoneNumber.isValid()) {
      return phoneNumber;
    }
    return PhoneNumber.getInvalidPhoneNumber(strPhoneNumber, "invalid db source");
  }
}

