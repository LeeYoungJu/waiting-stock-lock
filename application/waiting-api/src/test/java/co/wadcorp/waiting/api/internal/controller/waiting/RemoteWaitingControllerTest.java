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
import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteWaitingListRequest;
import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteWaitingRegisterRequest;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

class RemoteWaitingControllerTest extends ControllerTest {

  @DisplayName("원격 웨이팅 등록 시 테이블 id는 필수값이다.")
  @Test
  void registerWaitingWithoutTableId() throws Exception {
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put("SHOP_UUID", "11");

    when(channelShopIdConverter.getShopIds(any())).thenReturn(
        channelShopIdMapping
    );
    when(channelShopIdConverter.isSupport(any())).thenReturn(true);

    RemoteWaitingRegisterRequest request = RemoteWaitingRegisterRequest.builder()
        .totalPersonCount(2)
        .build();

    mockMvc.perform(
            post("/internal/api/v1/shops/{shopIds}/waiting", "1")
                .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("테이블 id는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("원격 웨이팅 등록 시 총 인원은 필수값이다.")
  @Test
  void registerWaitingWithout() throws Exception {
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put("SHOP_UUID", "11");

    when(channelShopIdConverter.getShopIds(any())).thenReturn(
        channelShopIdMapping
    );
    when(channelShopIdConverter.isSupport(any())).thenReturn(true);

    RemoteWaitingRegisterRequest request = RemoteWaitingRegisterRequest.builder()
        .tableId("tableId")
        .build();

    mockMvc.perform(
            post("/internal/api/v1/shops/{shopIds}/waiting", "1")
                .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("총 인원은 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("원격 웨이팅 등록 시 주문정보는 필수가 아니다.")
  @Test
  void registerWaitingWithoutOrderInfo() throws Exception {
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put("SHOP_UUID", "11");

    when(channelShopIdConverter.getShopIds(any())).thenReturn(
        channelShopIdMapping
    );
    when(channelShopIdConverter.isSupport(any())).thenReturn(true);

    RemoteWaitingRegisterRequest request = RemoteWaitingRegisterRequest.builder()
        .tableId("tableId")
        .totalPersonCount(2)
        .build();

    mockMvc.perform(
            post("/internal/api/v1/shops/{shopIds}/waiting", "1")
                .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("원격 웨이팅 등록 시 주문정보를 포함할 수 있다.")
  @Test
  void registerWaitingWithOrderInfo() throws Exception {
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put("SHOP_UUID", "11");

    when(channelShopIdConverter.getShopIds(any())).thenReturn(
        channelShopIdMapping
    );
    when(channelShopIdConverter.isSupport(any())).thenReturn(true);

    RemoteWaitingRegisterRequest request = RemoteWaitingRegisterRequest.builder()
        .tableId("tableId")
        .totalPersonCount(2)
        .order(RemoteOrderRequest.builder()
            .totalPrice(BigDecimal.valueOf(10000))
            .orderLineItems(List.of(RemoteOrderRequest.OrderLineItem.builder()
                    .menuId("MENU_ID")
                    .name("메뉴1")
                    .unitPrice(BigDecimal.valueOf(10000))
                    .linePrice(BigDecimal.valueOf(10000))
                    .quantity(1)
                    .build()
                )
            )
            .build())
        .build();

    mockMvc.perform(
            post("/internal/api/v1/shops/{shopIds}/waiting", "1")
                .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("원격 웨이팅 등록 시 주문정보에 빈 배열의 메뉴를 포함할 수 있다.")
  @Test
  void registerWaitingWithEmptyMenu() throws Exception {
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put("SHOP_UUID", "11");

    when(channelShopIdConverter.getShopIds(any())).thenReturn(
        channelShopIdMapping
    );
    when(channelShopIdConverter.isSupport(any())).thenReturn(true);

    RemoteWaitingRegisterRequest request = RemoteWaitingRegisterRequest.builder()
        .tableId("tableId")
        .totalPersonCount(2)
        .order(RemoteOrderRequest.builder()
            .totalPrice(BigDecimal.valueOf(0))
            .orderLineItems(List.of())
            .build())
        .build();

    mockMvc.perform(
            post("/internal/api/v1/shops/{shopIds}/waiting", "1")
                .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("원격 웨이팅 목록 조회 시 웨이팅 id 리스트는 필수값이다.")
  @Test
  void findWaitingsWithoutWaitingIds() throws Exception {
    RemoteWaitingListRequest request = RemoteWaitingListRequest.builder()
        .build();

    mockMvc.perform(
            post("/internal/api/v1/waiting/list")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("웨이팅 ID 리스트는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("원격 웨이팅 중복/3회초과 검증 시 전화번호는 필수값이다.")
  @Test
  void checkValidationWithoutCustomerNumber() throws Exception {

    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put("SHOP_UUID", "11");

    when(channelShopIdConverter.getShopIds(any())).thenReturn(
        channelShopIdMapping
    );
    when(channelShopIdConverter.isSupport(any())).thenReturn(true);

    mockMvc.perform(
            get("/internal/api/v1/shops/{shopIds}/register/waiting/validation", "SHOP_UUID")
                .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("전화번호는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("원격 웨이팅 타매장 웨이팅 시 전화번호는 필수값이다.")
  @Test
  void findWaitingsOfOtherShopsWithoutCustomerNumber() throws Exception {

    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put("SHOP_UUID", "11");

    when(channelShopIdConverter.getShopIds(any())).thenReturn(
        channelShopIdMapping
    );
    when(channelShopIdConverter.isSupport(any())).thenReturn(true);

    mockMvc.perform(
            get("/internal/api/v1/shops/{shopIds}/register/waiting/others", "SHOP_UUID")
                .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("전화번호는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

}