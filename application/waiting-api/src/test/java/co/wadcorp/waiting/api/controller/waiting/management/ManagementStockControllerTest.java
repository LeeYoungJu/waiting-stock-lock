package co.wadcorp.waiting.api.controller.waiting.management;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.ControllerTest;
import co.wadcorp.waiting.api.controller.waiting.management.dto.request.ManagementStockUpdateRequest;
import co.wadcorp.waiting.api.controller.waiting.management.dto.request.ManagementStockUpdateRequest.ManagementStockCategoryDto;
import co.wadcorp.waiting.api.controller.waiting.management.dto.request.ManagementStockUpdateRequest.ManagementStockMenuDto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

class ManagementStockControllerTest extends ControllerTest {

  private static final String X_REQUEST_ID = "X-REQUEST-Id";

  @DisplayName("재고관리 저장 시 카테고리 리스트는 필수값이다.")
  @Test
  void saveOrderCategoryWithoutCategories() throws Exception {
    ManagementStockUpdateRequest request = ManagementStockUpdateRequest.builder()
        .categories(List.of())
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/management/stock", "1")
                .header(X_REQUEST_ID, "deviceId")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("카테고리 리스트는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("재고관리 저장 시 카테고리 ID는 필수값이다.")
  @Test
  void saveOrderCategoryWithoutCategoryId() throws Exception {
    ManagementStockUpdateRequest request = ManagementStockUpdateRequest.builder()
        .categories(List.of(ManagementStockCategoryDto.builder()
            .name("카테고리1")
            .menus(List.of(ManagementStockMenuDto.builder()
                .id(UUIDUtil.shortUUID())
                .name("메뉴1")
                .additionalQuantity(1)
                .isOutOfStock(false)
                .build()
            ))
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/management/stock", "1")
                .header(X_REQUEST_ID, "deviceId")
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

  @DisplayName("재고관리 저장 시 카테고리 이름은 필수값이다.")
  @Test
  void saveOrderCategoryWithoutCategoryName() throws Exception {
    ManagementStockUpdateRequest request = ManagementStockUpdateRequest.builder()
        .categories(List.of(ManagementStockCategoryDto.builder()
            .id(UUIDUtil.shortUUID())
            .menus(List.of(ManagementStockMenuDto.builder()
                .id(UUIDUtil.shortUUID())
                .name("메뉴1")
                .additionalQuantity(1)
                .isOutOfStock(false)
                .build()
            ))
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/management/stock", "1")
                .header(X_REQUEST_ID, "deviceId")
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

  @DisplayName("재고관리 저장 시 메뉴 리스트는 필수값이다.")
  @Test
  void saveOrderCategoryWithoutMenus() throws Exception {
    ManagementStockUpdateRequest request = ManagementStockUpdateRequest.builder()
        .categories(List.of(ManagementStockCategoryDto.builder()
            .id(UUIDUtil.shortUUID())
            .name("카테고리1")
            .menus(List.of())
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/management/stock", "1")
                .header(X_REQUEST_ID, "deviceId")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("메뉴 리스트는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("재고관리 저장 시 메뉴 ID는 필수값이다.")
  @Test
  void saveOrderCategoryWithoutMenuId() throws Exception {
    ManagementStockUpdateRequest request = ManagementStockUpdateRequest.builder()
        .categories(List.of(ManagementStockCategoryDto.builder()
            .id(UUIDUtil.shortUUID())
            .name("카테고리1")
            .menus(List.of(ManagementStockMenuDto.builder()
                .name("메뉴1")
                .additionalQuantity(1)
                .isOutOfStock(false)
                .build()
            ))
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/management/stock", "1")
                .header(X_REQUEST_ID, "deviceId")
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

  @DisplayName("재고관리 저장 시 메뉴 이름은 필수값이다.")
  @Test
  void saveOrderCategoryWithoutMenuName() throws Exception {
    ManagementStockUpdateRequest request = ManagementStockUpdateRequest.builder()
        .categories(List.of(ManagementStockCategoryDto.builder()
            .id(UUIDUtil.shortUUID())
            .name("카테고리1")
            .menus(List.of(ManagementStockMenuDto.builder()
                .id(UUIDUtil.shortUUID())
                .additionalQuantity(1)
                .isOutOfStock(false)
                .build()
            ))
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/management/stock", "1")
                .header(X_REQUEST_ID, "deviceId")
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

  @DisplayName("재고관리 저장 시 재고 추가수량은 필수값이다.")
  @Test
  void saveOrderCategoryWithoutAdditionalQuantity() throws Exception {
    ManagementStockUpdateRequest request = ManagementStockUpdateRequest.builder()
        .categories(List.of(ManagementStockCategoryDto.builder()
            .id(UUIDUtil.shortUUID())
            .name("카테고리1")
            .menus(List.of(ManagementStockMenuDto.builder()
                .id(UUIDUtil.shortUUID())
                .name("메뉴1")
                .isOutOfStock(false)
                .build()
            ))
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/management/stock", "1")
                .header(X_REQUEST_ID, "deviceId")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("재고 추가수량은 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("재고관리 저장 시 재고 품절 여부는 필수값이다.")
  @Test
  void saveOrderCategoryWithoutIsOutOfStock() throws Exception {
    ManagementStockUpdateRequest request = ManagementStockUpdateRequest.builder()
        .categories(List.of(ManagementStockCategoryDto.builder()
            .id(UUIDUtil.shortUUID())
            .name("카테고리1")
            .menus(List.of(ManagementStockMenuDto.builder()
                .id(UUIDUtil.shortUUID())
                .name("메뉴1")
                .additionalQuantity(1)
                .build()
            ))
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/management/stock", "1")
                .header(X_REQUEST_ID, "deviceId")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("재고 품절 여부는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

}