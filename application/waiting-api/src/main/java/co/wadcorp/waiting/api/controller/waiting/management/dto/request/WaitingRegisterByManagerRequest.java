package co.wadcorp.waiting.api.controller.waiting.management.dto.request;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.model.waiting.vo.PersonOptionVO;
import co.wadcorp.waiting.api.model.waiting.vo.SeatOptionVO;
import co.wadcorp.waiting.api.model.waiting.vo.TermsCustomerVO;
import co.wadcorp.waiting.data.domain.customer.TermsCustomerEntity;
import co.wadcorp.waiting.data.domain.order.OrderEntity;
import co.wadcorp.waiting.data.domain.order.OrderStatus;
import co.wadcorp.waiting.data.domain.order.OrderType;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData.AdditionalOption;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData.PersonOptionSetting;
import co.wadcorp.waiting.data.domain.stock.MenuQuantity;
import co.wadcorp.waiting.data.domain.waiting.PersonOption;
import co.wadcorp.waiting.data.domain.waiting.PersonOptionsData;
import co.wadcorp.waiting.data.domain.waiting.RegisterChannel;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.support.Price;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 *  수기 웨이팅 등록 Request
 *  REGISTER_CHANNEL : MANAGER_APP
 * */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitingRegisterByManagerRequest {

  private String phoneNumber;
  private String name;
  private Integer totalPersonCount; // 총입장인원 (착석 + 비착석)
  private List<PersonOptionVO> personOptions;
  private SeatOptionVO seatOption;
  private List<TermsCustomerVO> termsCustomer;
  private OrderDto order;

  public WaitingEntity toWaitingEntity(String shopId, Long customerSeq, LocalDate operationDate,
      String customerName, HomeSettingsData homeSettings, OptionSettingsData optionSettings, WaitingNumber waitingNumbers,
      ZonedDateTime expectedSittingDateTime) {

    return WaitingEntity.builder()
        .shopId(shopId)
        .waitingId(UUIDUtil.shortUUID().toUpperCase())
        .customerSeq(customerSeq)
        .customerName(customerName)
        .operationDate(operationDate)
        .registerChannel(RegisterChannel.WAITING_MANAGER)
        .waitingNumbers(waitingNumbers)
        .waitingStatus(WaitingStatus.WAITING)
        .waitingDetailStatus(WaitingDetailStatus.WAITING)
        .totalPersonCount(totalPersonCount)
        .seatOptionName(homeSettings.findSeatOptionsBySeatOptionId(seatOption.getId()).getName())
        .personOptionsData(convertPersonOptionsData(optionSettings))
        .expectedSittingDateTime(expectedSittingDateTime)
        .build();
  }

  public List<TermsCustomerEntity> toTermsCustomerEntities(String shopId, Long waitingSeq, Long customerSeq) {
    return termsCustomer.stream()
        .map(t -> t.toTermsCustomerEntity(shopId, waitingSeq, customerSeq))
        .toList();
  }


  public PersonOptionsData convertPersonOptionsData(OptionSettingsData optionSettings) {
    return PersonOptionsData.builder()
        .personOptions(convertPersonOptions(optionSettings))
        .build();
  }

  private List<PersonOption> convertPersonOptions(OptionSettingsData optionSettings) {
    return personOptions.stream()
        .map(o -> {
          PersonOptionSetting personOption = optionSettings.findPersonOption(o.getId());
          List<PersonOption.AdditionalOption> additionalOptionList = o.getAdditionalOptions().stream()
              .map(item -> {
                AdditionalOption additionalOption = personOption.findAdditionalOption(item.getId());
                return PersonOption.AdditionalOption.builder()
                    .name(additionalOption.getName())
                    .count(item.getCount())
                    .build();
              })
              .toList();

          return new PersonOption(personOption.getName(), o.getCount(), additionalOptionList);
        })
        .toList();
  }

  public OrderEntity toOrderEntity(String shopId, String waitingId, LocalDate operationDate,
      OrderType orderType) {
    String orderId = UUIDUtil.shortUUID();

    return OrderEntity.builder()
        .shopId(shopId)
        .waitingId(waitingId)
        .orderId(orderId)
        .operationDate(operationDate)
        .orderType(orderType)
        .totalPrice(Price.of(order.getTotalPrice()))
        .orderLineItems(
            this.order.toOrderLineItemEntity(orderId)
        )
        .build();
  }

  @JsonIgnore
  public List<String> getMenuIds() {
    return this.order.getMenuIds();
  }

  public List<MenuQuantity> toMenuQuantity() {
    return this.order.toMenuQuantity();
  }
}
