package co.wadcorp.waiting.api.test.settings;

import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentRequest;
import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.controller.settings.OrderMenuMappingController;
import co.wadcorp.waiting.api.controller.settings.dto.request.OrderCategoryMappingSaveRequest;
import co.wadcorp.waiting.api.controller.settings.dto.request.OrderCategoryMappingSaveRequest.MappingMenuDto;
import co.wadcorp.waiting.api.controller.settings.dto.request.OrderCategoryOrderingSaveRequest;
import co.wadcorp.waiting.api.controller.settings.dto.request.OrderCategoryOrderingSaveRequest.MappingCategoryDto;
import co.wadcorp.waiting.api.service.settings.OrderMenuMappingApiService;
import co.wadcorp.waiting.api.service.settings.dto.request.MenuType;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategoryMappingSaveServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategoryOrderingSaveServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderDisplayMenuServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderCategoryOrderingServiceResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderCategoryOrderingServiceResponse.MappingCategoryServiceDto;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderDisplayMenuMappingResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderDisplayMenuMappingResponse.OrderDisplayCategoryDto;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderDisplayMenuMappingResponse.OrderDisplayMenuDto;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderMenuMappingServiceResponse;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.data.support.Price;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

class OrderMenuMappingControllerDocsTest extends RestDocsSupport {

  private final OrderMenuMappingApiService orderMenuMappingApiService = mock(
      OrderMenuMappingApiService.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object init() {
    return new OrderMenuMappingController(orderMenuMappingApiService);
  }

  @Test
  @DisplayName("설정 - 유형별 주문 설정 조회")
  void getMenus() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    OrderDisplayMenuMappingResponse response = defaultMenuMappingResponse();

    // when
    when(orderMenuMappingApiService.getDisplayMappingMenus(any(),
        any(OrderDisplayMenuServiceRequest.class))).thenReturn(response);

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/settings/orders/menu-mapping",
                    SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("menuType", "SHOP_MENU"))
        .andExpect(status().isOk())
        .andDo(document("order-menus-mapping-settings-get",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            queryParameters(
                parameterWithName("menuType").description(
                    "매장/포장 메뉴 타입: SHOP_MENU(매장 메뉴), TAKE_OUT_MENU(포장 메뉴)")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("categories").type(JsonFieldType.ARRAY)
                    .description("카테고리 목록"),
                fieldWithPath("categories[].id").type(JsonFieldType.STRING)
                    .description("카테고리 short UUID"),
                fieldWithPath("categories[].name").type(JsonFieldType.STRING)
                    .description("카테고리 이름"),
                fieldWithPath("categories[].ordering").type(JsonFieldType.NUMBER)
                    .description("카테고리 노출 순서"),
                fieldWithPath("categories[].allChecked").type(JsonFieldType.BOOLEAN)
                    .description("전체 체크 여부 (신규메뉴 추가 시 자동 매핑 여부)"),

                fieldWithPath("categories[].menus").type(JsonFieldType.ARRAY)
                    .description("메뉴 목록"),
                fieldWithPath("categories[].menus[].id").type(JsonFieldType.STRING)
                    .description("메뉴 short UUID"),
                fieldWithPath("categories[].menus[].name").type(JsonFieldType.STRING)
                    .description("메뉴 이름"),
                fieldWithPath("categories[].menus[].ordering").type(JsonFieldType.NUMBER)
                    .description("메뉴 노출 순서"),
                fieldWithPath("categories[].menus[].unitPrice").type(JsonFieldType.NUMBER)
                    .description("메뉴 단위 가격"),
                fieldWithPath("categories[].menus[].isChecked").type(JsonFieldType.BOOLEAN)
                    .description("체크 여부")
            )
        ));
  }

  @Test
  @DisplayName("설정 - 유형별 주문 설정 카테고리별 메뉴 저장")
  void saveMenus() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    String menuId = UUIDUtil.shortUUID();
    String categoryId = UUIDUtil.shortUUID();

    OrderCategoryMappingSaveRequest request = defaultSaveMenuMappingRequest(menuId);
    OrderMenuMappingServiceResponse response = defaultSaveMenuMappingResponse(menuId, categoryId);

    // when
    when(orderMenuMappingApiService.saveMenuMapping(any(),
        any(OrderCategoryMappingSaveServiceRequest.class))
    )
        .thenReturn(response);

    // then
    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/categories/{categoryId}/menu-mapping", SHOP_ID,
                categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andDo(document("order-menus-mapping-settings-save",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장 ID"),
                parameterWithName("categoryId").description("카테고리 ID")
            ),
            requestFields(
                fieldWithPath("menuType").type(JsonFieldType.STRING)
                    .description("매장/포장 메뉴 타입: SHOP_MENU(매장 메뉴), TAKE_OUT_MENU(포장 메뉴)"),
                fieldWithPath("allChecked").type(JsonFieldType.BOOLEAN)
                    .description("전체 체크 여부 (신규메뉴 추가 시 자동 매핑 여부)"),

                fieldWithPath("menus").type(JsonFieldType.ARRAY)
                    .description("메뉴 목록"),
                fieldWithPath("menus[].id").type(JsonFieldType.STRING)
                    .description("메뉴 short UUID"),
                fieldWithPath("menus[].isChecked").type(JsonFieldType.BOOLEAN)
                    .description("체크 여부"),
                fieldWithPath("menus[].ordering").type(JsonFieldType.NUMBER)
                    .description("메뉴 순서")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("id").type(JsonFieldType.STRING)
                    .description("카테고리 short UUID"),
                fieldWithPath("allChecked").type(JsonFieldType.BOOLEAN)
                    .description("전체 체크 여부 (신규메뉴 추가 시 자동 매핑 여부)"),

                fieldWithPath("menus").type(JsonFieldType.ARRAY)
                    .description("메뉴 목록"),
                fieldWithPath("menus[].id").type(JsonFieldType.STRING)
                    .description("메뉴 short UUID"),
                fieldWithPath("menus[].name").type(JsonFieldType.STRING)
                    .description("메뉴 이름"),
                fieldWithPath("menus[].isChecked").type(JsonFieldType.BOOLEAN)
                    .description("체크 여부"),
                fieldWithPath("menus[].ordering").type(JsonFieldType.NUMBER)
                    .description("메뉴 순서")
            )
        ));
  }

  @DisplayName("설정 - 유형별 주문 설정 카테고리 순서 저장")
  @Test
  void saveCategoryOrdering() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";

    OrderCategoryOrderingSaveRequest request = defaultOrderCategoryOrderingSaveRequest();
    OrderCategoryOrderingServiceResponse response = defaultOrderCategoryOrderingServiceResponse();

    // when
    when(orderMenuMappingApiService.saveCategoryOrdering(any(),
        any(OrderCategoryOrderingSaveServiceRequest.class))
    )
        .thenReturn(response);

    // then
    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/orders/categories/ordering", SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andDo(document("order-menus-mapping-settings-category-ordering-save",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장 ID")
            ),
            requestFields(
                fieldWithPath("menuType").type(JsonFieldType.STRING)
                    .description("매장/포장 메뉴 타입: SHOP_MENU(매장 메뉴), TAKE_OUT_MENU(포장 메뉴)"),

                fieldWithPath("categories").type(JsonFieldType.ARRAY)
                    .description("카테고리 목록"),
                fieldWithPath("categories[].id").type(JsonFieldType.STRING)
                    .description("카테고리 short UUID"),
                fieldWithPath("categories[].ordering").type(JsonFieldType.NUMBER)
                    .description("카테고리 순서")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),

                fieldWithPath("categories").type(JsonFieldType.ARRAY)
                    .description("카테고리 목록"),
                fieldWithPath("categories[].id").type(JsonFieldType.STRING)
                    .description("카테고리 short UUID"),
                fieldWithPath("categories[].ordering").type(JsonFieldType.NUMBER)
                    .description("카테고리 순서")
            )
        ));
  }

  private OrderCategoryMappingSaveRequest defaultSaveMenuMappingRequest(String menuId) {
    return OrderCategoryMappingSaveRequest.builder()
        .menuType(MenuType.SHOP_MENU)
        .allChecked(true)
        .menus(List.of(MappingMenuDto.builder()
            .id(menuId)
            .isChecked(true)
            .ordering(1)
            .build()
        ))
        .build();
  }

  private OrderMenuMappingServiceResponse defaultSaveMenuMappingResponse(String menuId,
      String categoryId) {
    return OrderMenuMappingServiceResponse.builder()
        .id(categoryId)
        .allChecked(true)
        .menus(List.of(
            OrderMenuMappingServiceResponse.MappingMenu.builder()
                .id(menuId)
                .name("콜라")
                .isChecked(true)
                .ordering(1)
                .build()
        ))
        .build();
  }

  private OrderDisplayMenuMappingResponse defaultMenuMappingResponse() {
    return OrderDisplayMenuMappingResponse.builder()
        .categories(List.of(
            OrderDisplayCategoryDto.builder()
                .id(UUIDUtil.shortUUID())
                .name("음료")
                .ordering(1)
                .allChecked(true)
                .menus(
                    List.of(
                        OrderDisplayMenuDto.builder()
                            .id(UUIDUtil.shortUUID())
                            .name("콜라")
                            .ordering(1)
                            .unitPrice(Price.of(new BigDecimal(2000)))
                            .isChecked(true)
                            .build(),
                        OrderDisplayMenuDto.builder()
                            .id(UUIDUtil.shortUUID())
                            .name("사이다")
                            .ordering(2)
                            .unitPrice(Price.of(new BigDecimal(2000)))
                            .isChecked(true)
                            .build()
                    ))
                .build()
        ))
        .build();
  }

  private OrderCategoryOrderingSaveRequest defaultOrderCategoryOrderingSaveRequest() {
    return OrderCategoryOrderingSaveRequest.builder()
        .menuType(MenuType.SHOP_MENU)
        .categories(List.of(MappingCategoryDto.builder()
            .id(UUIDUtil.shortUUID())
            .ordering(1)
            .build()
        ))
        .build();
  }

  private OrderCategoryOrderingServiceResponse defaultOrderCategoryOrderingServiceResponse() {
    return OrderCategoryOrderingServiceResponse.builder()
        .categories(List.of(MappingCategoryServiceDto.builder()
            .id(UUIDUtil.shortUUID())
            .ordering(1)
            .build()
        ))
        .build();
  }

}