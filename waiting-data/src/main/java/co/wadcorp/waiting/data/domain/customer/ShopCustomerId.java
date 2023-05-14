package co.wadcorp.waiting.data.domain.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ShopCustomerId implements Serializable {

  @Column(name = "customer_seq")
  private Long customerSeq;

  @Column(name = "shop_id")
  private String shopId;

}
