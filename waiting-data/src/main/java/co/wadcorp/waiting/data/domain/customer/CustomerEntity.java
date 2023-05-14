package co.wadcorp.waiting.data.domain.customer;

import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.data.support.PhoneNumberConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name = "cw_customer")
public class CustomerEntity extends BaseEntity {

  public static final CustomerEntity EMPTY_CUSTOMER_ENTITY = new CustomerEntity();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "enc_customer_phone", columnDefinition = "VARCHAR")
  @Convert(converter = PhoneNumberConverter.class)
  private PhoneNumber encCustomerPhone;

  @Builder
  public CustomerEntity(PhoneNumber encCustomerPhone) {
    this.encCustomerPhone = encCustomerPhone;
  }

  public static CustomerEntity create(PhoneNumber phoneNumber) {
    return CustomerEntity.builder().encCustomerPhone(phoneNumber).build();
  }

  public String getLocalPhoneNumber() {
    return this.encCustomerPhone.getLocal();
  }
}
