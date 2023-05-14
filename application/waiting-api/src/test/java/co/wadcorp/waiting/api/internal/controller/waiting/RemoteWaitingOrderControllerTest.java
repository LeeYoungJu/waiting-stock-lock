package co.wadcorp.waiting.api.internal.controller.waiting;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.waiting.api.ControllerTest;
import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteOrderRequest;
import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteOrderRequest.OrderLineItem;
import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteWaitingOrderValidateRequest;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import java.math.BigDecimal;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

class RemoteWaitingOrderControllerTest extends ControllerTest {

  @DisplayName("선주문 메뉴 조회를 할 수 있다.")
  @Test
  void getOrderMenusWithInvalidMenuType() throws Exception {
    // given
    String channelShopId = "11";
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put("SHOP_UUID", channelShopId);

    when(channelShopIdConverter.getShopIds(any())).thenReturn(
        channelShopIdMapping
    );
    when(channelShopIdConverter.isSupport(any())).thenReturn(true);

    // then
    mockMvc.perform(
            get("/internal/api/v1/shops/{shopIds}/orders", channelShopId)
                .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
                .queryParam("menuType", "SHOP_MENU")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("선주문 메뉴 조회 시 메뉴 타입은 필수이다.")
  @Test
  void getOrderMenusWithoutMenuType() throws Exception {
    // given
    String channelShopId = "11";
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put("SHOP_UUID", channelShopId);

    when(channelShopIdConverter.getShopIds(any())).thenReturn(
        channelShopIdMapping
    );
    when(channelShopIdConverter.isSupport(any())).thenReturn(true);

    // then
    mockMvc.perform(
        get("/internal/api/v1/shops/{shopIds}/orders", channelShopId)
            .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("메뉴 타입은 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("선주문 메뉴 조회 시 메뉴 타입은 SHOP_MENU or TAKE_OUT_MENU 둘 중 하나여야 한다.")
  @Test
  void getOrderMenusNormal() throws Exception {
    // given
    String channelShopId = "11";
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put("SHOP_UUID", channelShopId);
    String invalidMenuType = "INVALID_MENU_TYPE"; // <= 메뉴 타입이 적합하지 않을 때

    when(channelShopIdConverter.getShopIds(any())).thenReturn(
        channelShopIdMapping
    );
    when(channelShopIdConverter.isSupport(any())).thenReturn(true);

    // then
    mockMvc.perform(
            get("/internal/api/v1/shops/{shopIds}/orders", channelShopId)
                .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
                .queryParam("menuType", invalidMenuType)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value(Matchers.containsString("Failed to convert")))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("선주문 정보를 검증할 수 있다.")
  @Test
  void validateOrderMenus() throws Exception {
    // given
    String channelShopId = "11";
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put("SHOP_UUID", channelShopId);

    RemoteWaitingOrderValidateRequest request = RemoteWaitingOrderValidateRequest.builder()
        .order(RemoteOrderRequest.builder()
            .totalPrice(BigDecimal.valueOf(12000))
            .orderLineItems(List.of(
                OrderLineItem.builder()
                    .menuId("MENU_ID_1")
                    .name("돈가스")
                    .unitPrice(BigDecimal.valueOf(10000))
                    .linePrice(BigDecimal.valueOf(10000))
                    .quantity(1)
                    .build(),
                OrderLineItem.builder()
                    .menuId("MENU_ID_2")
                    .name("콜라")
                    .unitPrice(BigDecimal.valueOf(1000))
                    .linePrice(BigDecimal.valueOf(2000))
                    .quantity(2)
                    .build()
            ))
            .build()
        )
        .build();

    when(channelShopIdConverter.getShopIds(any())).thenReturn(
        channelShopIdMapping
    );
    when(channelShopIdConverter.isSupport(any())).thenReturn(true);

    // then
    mockMvc.perform(
        post("/internal/api/v1/shops/{shopIds}/orders/validation", channelShopId)
            .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("선주문 정보 검증 시 request에 order 정보는 필수이다.")
  @Test
  void validateOrderMenusWithNullOrder() throws Exception {
    // given
    String channelShopId = "11";
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put("SHOP_UUID", channelShopId);

    RemoteWaitingOrderValidateRequest request = RemoteWaitingOrderValidateRequest.builder()
        .order(null)  // request에 order 정보가 넘어오지 않았을 때
        .build();

    when(channelShopIdConverter.getShopIds(any())).thenReturn(
        channelShopIdMapping
    );
    when(channelShopIdConverter.isSupport(any())).thenReturn(true);

    // then
    mockMvc.perform(
            post("/internal/api/v1/shops/{shopIds}/orders/validation", channelShopId)
                .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("주문 정보는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

}