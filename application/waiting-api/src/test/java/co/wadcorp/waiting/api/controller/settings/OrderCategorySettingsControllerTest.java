package co.wadcorp.waiting.api.controller.settings;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.ControllerTest;
import co.wadcorp.waiting.api.controller.settings.dto.request.OrderCategorySettingListRequest;
import co.wadcorp.waiting.api.controller.settings.dto.request.OrderCategorySettingListRequest.OrderCategoryDto;
import co.wadcorp.waiting.api.controller.settings.dto.request.OrderCategorySettingsRequest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

class OrderCategorySettingsControllerTest extends ControllerTest {

  @DisplayName("카테고리 단건 생성 시 ID는 필수값이다.")
  @Test
  void saveOrderCategoryWithoutId() throws Exception {
    OrderCategorySettingsRequest request = OrderCategorySettingsRequest.builder()
        .name("음료")
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/categories", "1")
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

  @DisplayName("카테고리 단건 생성 시 이름은 필수값이다.")
  @Test
  void saveOrderCategoryWithoutName() throws Exception {
    OrderCategorySettingsRequest request = OrderCategorySettingsRequest.builder()
        .id("shortUUID")
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/categories", "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("카테고리 이름은 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("카테고리 리스트 저장 시 ID는 필수값이다.")
  @Test
  void saveAllCategoriesWithoutId() throws Exception {
    OrderCategorySettingListRequest request = OrderCategorySettingListRequest.builder()
        .categories(List.of(OrderCategoryDto.builder()
            .name("카테고리 이름")
            .ordering(1)
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/categories/list", "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("카테고리 id는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("카테고리 리스트 저장 시 이름은 필수값이다.")
  @Test
  void saveAllCategoriesWithoutName() throws Exception {
    OrderCategorySettingListRequest request = OrderCategorySettingListRequest.builder()
        .categories(List.of(OrderCategoryDto.builder()
            .id(UUIDUtil.shortUUID())
            .ordering(1)
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/categories/list", "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("카테고리 이름은 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("카테고리 리스트 저장 시 순서는 1 이상이다.")
  @Test
  void saveAllCategoriesWithoutPositiveOrdering() throws Exception {
    OrderCategorySettingListRequest request = OrderCategorySettingListRequest.builder()
        .categories(List.of(OrderCategoryDto.builder()
            .id(UUIDUtil.shortUUID())
            .name("카테고리 이름")
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/categories/list", "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("카테고리 순서는 1 이상의 필수값입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("카테고리 리스트 저장 시 순서는 중복되지 않아야 한다.")
  @Test
  void saveAllCategoriesWithDuplicateOrdering() throws Exception {
    OrderCategorySettingListRequest request = OrderCategorySettingListRequest.builder()
        .categories(List.of(
            OrderCategoryDto.builder()
                .id(UUIDUtil.shortUUID())
                .name("카테고리1")
                .ordering(1)
                .build(),
            OrderCategoryDto.builder()
                .id(UUIDUtil.shortUUID())
                .name("카테고리2")
                .ordering(1)
                .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/categories/list", "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("순서값은 중복될 수 없습니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

}