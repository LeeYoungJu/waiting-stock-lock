package co.wadcorp.waiting.api.controller.settings;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.ControllerTest;
import co.wadcorp.waiting.api.controller.settings.dto.request.OrderCategoryMappingSaveRequest;
import co.wadcorp.waiting.api.controller.settings.dto.request.OrderCategoryMappingSaveRequest.MappingMenuDto;
import co.wadcorp.waiting.api.controller.settings.dto.request.OrderCategoryOrderingSaveRequest;
import co.wadcorp.waiting.api.controller.settings.dto.request.OrderCategoryOrderingSaveRequest.MappingCategoryDto;
import co.wadcorp.waiting.api.service.settings.dto.request.MenuType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

class OrderMenuMappingControllerTest extends ControllerTest {

  @DisplayName("메뉴판 조회 시 메뉴판 타입은 필수값이다.")
  @Test
  void getDisplayMappingMenus() throws Exception {
    mockMvc.perform(
            get("/api/v1/shops/{shopId}/settings/orders/menu-mapping", "1")
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("메뉴판 타입은 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메뉴판 저장 시 메뉴판 타입은 필수값이다.")
  @Test
  void saveMenusWithoutMenuType() throws Exception {
    OrderCategoryMappingSaveRequest request = OrderCategoryMappingSaveRequest.builder()
        .allChecked(false)
        .menus(List.of(MappingMenuDto.builder()
            .id(UUIDUtil.shortUUID())
            .isChecked(false)
            .ordering(1)
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/categories/{categoryId}/menu-mapping", "1",
                "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("메뉴판 타입은 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메뉴판 저장 시 카테고리 전체 체크 여부는 필수값이다.")
  @Test
  void saveMenusWithoutAllChecked() throws Exception {
    OrderCategoryMappingSaveRequest request = OrderCategoryMappingSaveRequest.builder()
        .menuType(MenuType.SHOP_MENU)
        .menus(List.of(MappingMenuDto.builder()
            .id(UUIDUtil.shortUUID())
            .isChecked(false)
            .ordering(1)
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/categories/{categoryId}/menu-mapping", "1",
                "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("카테고리 전체 체크 여부는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메뉴판 저장 시 메뉴 리스트는 필수값이다.")
  @Test
  void saveMenusWithEmptyMenus() throws Exception {
    OrderCategoryMappingSaveRequest request = OrderCategoryMappingSaveRequest.builder()
        .menuType(MenuType.SHOP_MENU)
        .allChecked(false)
        .menus(List.of())
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/categories/{categoryId}/menu-mapping", "1",
                "1")
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

  @DisplayName("메뉴판 저장 시 메뉴 ID는 필수값이다.")
  @Test
  void saveMenusWithoutMenuId() throws Exception {
    OrderCategoryMappingSaveRequest request = OrderCategoryMappingSaveRequest.builder()
        .menuType(MenuType.SHOP_MENU)
        .allChecked(false)
        .menus(List.of(MappingMenuDto.builder()
            .isChecked(false)
            .ordering(1)
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/categories/{categoryId}/menu-mapping", "1",
                "1")
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

  @DisplayName("메뉴판 저장 시 메뉴 체크 여부는 필수값이다.")
  @Test
  void saveMenusWithoutIsChecked() throws Exception {
    OrderCategoryMappingSaveRequest request = OrderCategoryMappingSaveRequest.builder()
        .menuType(MenuType.SHOP_MENU)
        .allChecked(false)
        .menus(List.of(MappingMenuDto.builder()
            .id(UUIDUtil.shortUUID())
            .ordering(1)
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/categories/{categoryId}/menu-mapping", "1",
                "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("메뉴 체크 여부는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메뉴판 저장 시 카테고리 순서는 1 이상이다.")
  @Test
  void saveMenusWithoutPositiveOrdering() throws Exception {
    OrderCategoryMappingSaveRequest request = OrderCategoryMappingSaveRequest.builder()
        .menuType(MenuType.SHOP_MENU)
        .allChecked(false)
        .menus(List.of(MappingMenuDto.builder()
            .id(UUIDUtil.shortUUID())
            .isChecked(false)
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/categories/{categoryId}/menu-mapping", "1",
                "1")
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

  @DisplayName("메뉴판 저장 시 메뉴 순서는 중복되지 않아야 한다.")
  @Test
  void saveMenusWithDuplicateOrdering() throws Exception {
    OrderCategoryMappingSaveRequest request = OrderCategoryMappingSaveRequest.builder()
        .menuType(MenuType.SHOP_MENU)
        .allChecked(false)
        .menus(List.of(
            MappingMenuDto.builder()
                .id(UUIDUtil.shortUUID())
                .isChecked(false)
                .ordering(1)
                .build(),
            MappingMenuDto.builder()
                .id(UUIDUtil.shortUUID())
                .isChecked(false)
                .ordering(1)
                .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/categories/{categoryId}/menu-mapping", "1",
                "1")
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

  @DisplayName("메뉴판 카테고리 순서 저장 시 메뉴판 타입은 필수값이다.")
  @Test
  void saveCategoryOrderingWithoutMenuType() throws Exception {
    OrderCategoryOrderingSaveRequest request = OrderCategoryOrderingSaveRequest.builder()
        .categories(List.of(MappingCategoryDto.builder()
            .id(UUIDUtil.shortUUID())
            .ordering(1)
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/categories/ordering", "1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("메뉴판 타입은 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메뉴판 카테고리 순서 저장 시 카테고리 리스트는 필수값이다.")
  @Test
  void saveCategoryOrderingWithoutCategories() throws Exception {
    OrderCategoryOrderingSaveRequest request = OrderCategoryOrderingSaveRequest.builder()
        .menuType(MenuType.SHOP_MENU)
        .categories(List.of())
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/categories/ordering", "1")
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

  @DisplayName("메뉴판 카테고리 순서 저장 시 카테고리 ID는 필수값이다.")
  @Test
  void saveCategoryOrderingWithoutCategoryId() throws Exception {
    OrderCategoryOrderingSaveRequest request = OrderCategoryOrderingSaveRequest.builder()
        .menuType(MenuType.SHOP_MENU)
        .categories(List.of(MappingCategoryDto.builder()
            .ordering(1)
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/categories/ordering", "1")
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

  @DisplayName("메뉴판 카테고리 순서 저장 시 카테고리 순서는 1 이상이다.")
  @Test
  void saveCategoryOrderingWithoutOrdering() throws Exception {
    OrderCategoryOrderingSaveRequest request = OrderCategoryOrderingSaveRequest.builder()
        .menuType(MenuType.SHOP_MENU)
        .categories(List.of(MappingCategoryDto.builder()
            .id(UUIDUtil.shortUUID())
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/categories/ordering", "1")
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

  @DisplayName("메뉴판 카테고리 순서 저장 시 카테고리 순서는 중복되지 않아야 한다.")
  @Test
  void saveCategoryOrderingWithDuplicateOrdering() throws Exception {
    OrderCategoryOrderingSaveRequest request = OrderCategoryOrderingSaveRequest.builder()
        .menuType(MenuType.SHOP_MENU)
        .categories(List.of(
            MappingCategoryDto.builder()
                .id(UUIDUtil.shortUUID())
                .ordering(1)
                .build(),
            MappingCategoryDto.builder()
                .id(UUIDUtil.shortUUID())
                .ordering(1)
                .build()
        ))
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/categories/ordering", "1")
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