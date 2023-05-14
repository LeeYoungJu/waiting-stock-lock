package co.wadcorp.waiting.api.controller.settings;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.ControllerTest;
import co.wadcorp.waiting.api.controller.settings.dto.request.OrderMenuSettingsRequest;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

class OrderMenuSettingsControllerTest extends ControllerTest {

  @DisplayName("메뉴 단건 생성 시 ID는 필수값이다.")
  @Test
  void saveOrderCategoryWithoutId() throws Exception {
    OrderMenuSettingsRequest request = OrderMenuSettingsRequest.builder()
        .categoryId(UUIDUtil.shortUUID())
        .name("돈카츠")
        .unitPrice(BigDecimal.valueOf(1000))
        .isUsedDailyStock(true)
        .dailyStock(300)
        .isUsedMenuQuantityPerTeam(true)
        .menuQuantityPerTeam(2)
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/menus", "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("메뉴 ID는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메뉴 단건 생성 시 카테고리 ID는 필수값이다.")
  @Test
  void saveOrderCategoryWithoutCategoryId() throws Exception {
    OrderMenuSettingsRequest request = OrderMenuSettingsRequest.builder()
        .id(UUIDUtil.shortUUID())
        .name("돈카츠")
        .unitPrice(BigDecimal.valueOf(1000))
        .isUsedDailyStock(true)
        .dailyStock(300)
        .isUsedMenuQuantityPerTeam(true)
        .menuQuantityPerTeam(2)
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/menus", "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("카테고리 ID는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메뉴 단건 생성 시 이름은 필수값이다.")
  @Test
  void saveOrderCategoryWithoutName() throws Exception {
    OrderMenuSettingsRequest request = OrderMenuSettingsRequest.builder()
        .id(UUIDUtil.shortUUID())
        .categoryId(UUIDUtil.shortUUID())
        .unitPrice(BigDecimal.valueOf(1000))
        .isUsedDailyStock(true)
        .dailyStock(300)
        .isUsedDailyStock(false)
        .isUsedMenuQuantityPerTeam(false)
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/menus", "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("메뉴 이름은 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메뉴 단건 생성 시 가격은 필수값이다.")
  @Test
  void saveOrderCategoryWithoutPrice() throws Exception {
    OrderMenuSettingsRequest request = OrderMenuSettingsRequest.builder()
        .id(UUIDUtil.shortUUID())
        .categoryId(UUIDUtil.shortUUID())
        .name("돈카츠")
        .isUsedDailyStock(true)
        .dailyStock(300)
        .isUsedMenuQuantityPerTeam(false)
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/menus", "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("가격은 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메뉴 단건 생성 시 일별 재고 사용 여부는 필수값이다.")
  @Test
  void saveOrderCategoryWithoutUsedDailyStock() throws Exception {
    OrderMenuSettingsRequest request = OrderMenuSettingsRequest.builder()
        .id(UUIDUtil.shortUUID())
        .categoryId(UUIDUtil.shortUUID())
        .name("돈카츠")
        .unitPrice(BigDecimal.valueOf(1000))
        .isUsedMenuQuantityPerTeam(false)
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/menus", "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("일별 재고 사용 여부는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메뉴 단건 생성 시 한 팀당 주문 가능 여부는 필수값이다.")
  @Test
  void saveOrderCategoryWithoutMenuQuantityPerTeam() throws Exception {
    OrderMenuSettingsRequest request = OrderMenuSettingsRequest.builder()
        .id(UUIDUtil.shortUUID())
        .categoryId(UUIDUtil.shortUUID())
        .name("돈카츠")
        .unitPrice(BigDecimal.valueOf(1000))
        .isUsedDailyStock(true)
        .dailyStock(300)
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/menus", "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("한 팀당 주문 가능 여부는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메뉴 단건 수정 시 ID는 필수값이다.")
  @Test
  void updateOrderCategoryWithoutId() throws Exception {
    OrderMenuSettingsRequest request = OrderMenuSettingsRequest.builder()
        .categoryId(UUIDUtil.shortUUID())
        .name("돈카츠")
        .unitPrice(BigDecimal.valueOf(1000))
        .isUsedDailyStock(true)
        .dailyStock(300)
        .isUsedMenuQuantityPerTeam(true)
        .menuQuantityPerTeam(2)
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/menus/{menuId}/update", "1", "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("메뉴 ID는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메뉴 단건 수정 시 카테고리 ID는 필수값이다.")
  @Test
  void updateOrderCategoryWithoutCategoryId() throws Exception {
    OrderMenuSettingsRequest request = OrderMenuSettingsRequest.builder()
        .id(UUIDUtil.shortUUID())
        .name("돈카츠")
        .unitPrice(BigDecimal.valueOf(1000))
        .isUsedDailyStock(true)
        .dailyStock(300)
        .isUsedMenuQuantityPerTeam(true)
        .menuQuantityPerTeam(2)
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/menus/{menuId}/update", "1", "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("카테고리 ID는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메뉴 단건 수정 시 이름은 필수값이다.")
  @Test
  void updateOrderCategoryWithoutName() throws Exception {
    OrderMenuSettingsRequest request = OrderMenuSettingsRequest.builder()
        .id(UUIDUtil.shortUUID())
        .categoryId(UUIDUtil.shortUUID())
        .unitPrice(BigDecimal.valueOf(1000))
        .isUsedDailyStock(true)
        .dailyStock(300)
        .isUsedDailyStock(false)
        .isUsedMenuQuantityPerTeam(false)
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/menus/{menuId}/update", "1", "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("메뉴 이름은 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메뉴 단건 수정 시 가격은 필수값이다.")
  @Test
  void updateOrderCategoryWithoutPrice() throws Exception {
    OrderMenuSettingsRequest request = OrderMenuSettingsRequest.builder()
        .id(UUIDUtil.shortUUID())
        .categoryId(UUIDUtil.shortUUID())
        .name("돈카츠")
        .isUsedDailyStock(true)
        .dailyStock(300)
        .isUsedMenuQuantityPerTeam(false)
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/menus/{menuId}/update", "1", "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("가격은 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메뉴 단건 수정 시 일별 재고 사용 여부는 필수값이다.")
  @Test
  void updateOrderCategoryWithoutUsedDailyStock() throws Exception {
    OrderMenuSettingsRequest request = OrderMenuSettingsRequest.builder()
        .id(UUIDUtil.shortUUID())
        .categoryId(UUIDUtil.shortUUID())
        .name("돈카츠")
        .unitPrice(BigDecimal.valueOf(1000))
        .isUsedMenuQuantityPerTeam(false)
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/menus/{menuId}/update", "1", "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("일별 재고 사용 여부는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메뉴 단건 수정 시 한 팀당 주문 가능 여부는 필수값이다.")
  @Test
  void updateOrderCategoryWithoutMenuQuantityPerTeam() throws Exception {
    OrderMenuSettingsRequest request = OrderMenuSettingsRequest.builder()
        .id(UUIDUtil.shortUUID())
        .categoryId(UUIDUtil.shortUUID())
        .name("돈카츠")
        .unitPrice(BigDecimal.valueOf(1000))
        .isUsedDailyStock(true)
        .dailyStock(300)
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/menus/{menuId}/update", "1", "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("한 팀당 주문 가능 여부는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

}