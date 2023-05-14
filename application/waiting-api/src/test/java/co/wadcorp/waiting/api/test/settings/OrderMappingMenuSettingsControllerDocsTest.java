package co.wadcorp.waiting.api.test.settings;

import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentRequest;
import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.controller.settings.OrderMenuSettingsController;
import co.wadcorp.waiting.api.controller.settings.dto.request.OrderMenuSettingsRequest;
import co.wadcorp.waiting.api.service.settings.OrderMenuSettingsApiService;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderMenuSettingsServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderMenuSettingsListResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderMenuSettingsListResponse.OrderCategoryDto;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderMenuSettingsListResponse.OrderMenuDto;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderMenuSettingsResponse;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.data.support.Price;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

class OrderMappingMenuSettingsControllerDocsTest extends RestDocsSupport {

  private final OrderMenuSettingsApiService orderMenuSettingsApiService = mock(
      OrderMenuSettingsApiService.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object init() {
    return new OrderMenuSettingsController(orderMenuSettingsApiService);
  }

  @Test
  @DisplayName("메뉴 리스트 조회")
  void getMenus() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    OrderMenuSettingsListResponse response = defaultMenuListResponse();

    // when
    when(orderMenuSettingsApiService.getMenus(any())).thenReturn(response);

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/settings/orders/menus",
                    SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("order-menus-settings-get",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
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
                fieldWithPath("categories[].menus[].isUsedDailyStock").type(JsonFieldType.BOOLEAN)
                    .description("일별 재고 사용 여부"),
                fieldWithPath("categories[].menus[].dailyStock").type(JsonFieldType.NUMBER)
                    .description("일별 재고 수량").optional(),
                fieldWithPath("categories[].menus[].isUsedMenuQuantityPerTeam").type(
                        JsonFieldType.BOOLEAN)
                    .description("팀 당 주문 가능 수량 사용 여부"),
                fieldWithPath("categories[].menus[].menuQuantityPerTeam").type(JsonFieldType.NUMBER)
                    .description("팀 당 주문 가능 수량").optional()
            )
        ));
  }


  @Test
  @DisplayName("메뉴 단건 조회")
  void getMenu() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String MENU_UUID = "MENU_UUID";
    String menuId = UUIDUtil.shortUUID();
    String categoryId = UUIDUtil.shortUUID();

    OrderMenuSettingsResponse response = defaultMenuResponse(menuId, categoryId);

    // when
    when(orderMenuSettingsApiService.getMenu(any())).thenReturn(response);

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.get(
                    "/api/v1/shops/{shopId}/settings/orders/menus/{menuId}",
                    SHOP_ID, MENU_UUID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("order-menu-settings-get",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디"),
                parameterWithName("menuId").description("메뉴 short UUID")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("id").type(JsonFieldType.STRING)
                    .description("메뉴 short UUID"),
                fieldWithPath("categoryId").type(JsonFieldType.STRING)
                    .description("카테고리 short UUID"),
                fieldWithPath("name").type(JsonFieldType.STRING)
                    .description("메뉴 이름"),
                fieldWithPath("unitPrice").type(JsonFieldType.NUMBER)
                    .description("메뉴 단위 가격"),
                fieldWithPath("isUsedDailyStock").type(JsonFieldType.BOOLEAN)
                    .description("일별 재고 사용 여부"),
                fieldWithPath("dailyStock").type(JsonFieldType.NUMBER)
                    .description("일별 재고 수량").optional(),
                fieldWithPath("isUsedMenuQuantityPerTeam").type(JsonFieldType.BOOLEAN)
                    .description("팀 당 주문 가능 수량 사용 여부"),
                fieldWithPath("menuQuantityPerTeam").type(JsonFieldType.NUMBER)
                    .description("팀 당 주문 가능 수량").optional()
            )
        ));
  }


  @Test
  @DisplayName("메뉴 단건 생성")
  void createMenu() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    String menuId = UUIDUtil.shortUUID();
    String categoryId = UUIDUtil.shortUUID();

    OrderMenuSettingsResponse response = defaultMenuResponse(menuId, categoryId);
    OrderMenuSettingsRequest request = defaultMenuRequest(menuId, categoryId);

    // when
    when(orderMenuSettingsApiService.create(any(), any(OrderMenuSettingsServiceRequest.class),
        any(LocalDate.class))
    )
        .thenReturn(response);

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/settings/orders/menus",
                    SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andDo(document("order-menu-settings-save",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            requestFields(
                fieldWithPath("id").type(JsonFieldType.STRING)
                    .description("메뉴 short UUID"),
                fieldWithPath("categoryId").type(JsonFieldType.STRING)
                    .description("카테고리 short UUID"),
                fieldWithPath("name").type(JsonFieldType.STRING)
                    .description("메뉴 이름"),
                fieldWithPath("unitPrice").type(JsonFieldType.NUMBER)
                    .description("메뉴 단위 가격"),
                fieldWithPath("isUsedDailyStock").type(JsonFieldType.BOOLEAN)
                    .description("일별 재고 사용 여부"),
                fieldWithPath("dailyStock").type(JsonFieldType.NUMBER)
                    .description("일별 재고 수량").optional(),
                fieldWithPath("isUsedMenuQuantityPerTeam").type(JsonFieldType.BOOLEAN)
                    .description("팀 당 주문 가능 수량 사용 여부"),
                fieldWithPath("menuQuantityPerTeam").type(JsonFieldType.NUMBER)
                    .description("팀 당 주문 가능 수량").optional()
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("id").type(JsonFieldType.STRING)
                    .description("메뉴 short UUID"),
                fieldWithPath("categoryId").type(JsonFieldType.STRING)
                    .description("카테고리 short UUID"),
                fieldWithPath("name").type(JsonFieldType.STRING)
                    .description("메뉴 이름"),
                fieldWithPath("unitPrice").type(JsonFieldType.NUMBER)
                    .description("메뉴 단위 가격"),
                fieldWithPath("isUsedDailyStock").type(JsonFieldType.BOOLEAN)
                    .description("일별 재고 사용 여부"),
                fieldWithPath("dailyStock").type(JsonFieldType.NUMBER)
                    .description("일별 재고 수량").optional(),
                fieldWithPath("isUsedMenuQuantityPerTeam").type(JsonFieldType.BOOLEAN)
                    .description("팀 당 주문 가능 수량 사용 여부"),
                fieldWithPath("menuQuantityPerTeam").type(JsonFieldType.NUMBER)
                    .description("팀 당 주문 가능 수량").optional()
            )
        ));
  }


  @Test
  @DisplayName("메뉴 단건 수정")
  void updateMenu() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String MENU_UUID = "MENU_UUID";
    String menuId = UUIDUtil.shortUUID();
    String categoryId = UUIDUtil.shortUUID();

    OrderMenuSettingsResponse response = defaultMenuResponse(menuId, categoryId);
    OrderMenuSettingsRequest request = defaultMenuRequest(menuId, categoryId);

    // when
    when(orderMenuSettingsApiService.update(any(), any(OrderMenuSettingsServiceRequest.class),
        any(LocalDate.class)))
        .thenReturn(response);

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.post(
                    "/api/v1/shops/{shopId}/settings/orders/menus/{menuId}/update",
                    SHOP_ID, MENU_UUID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andDo(document("order-menu-settings-update",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디"),
                parameterWithName("menuId").description("메뉴 short UUID")
            ),
            requestFields(
                fieldWithPath("id").type(JsonFieldType.STRING)
                    .description("메뉴 short UUID"),
                fieldWithPath("categoryId").type(JsonFieldType.STRING)
                    .description("카테고리 short UUID"),
                fieldWithPath("name").type(JsonFieldType.STRING)
                    .description("메뉴 이름"),
                fieldWithPath("unitPrice").type(JsonFieldType.NUMBER)
                    .description("메뉴 단위 가격"),
                fieldWithPath("isUsedDailyStock").type(JsonFieldType.BOOLEAN)
                    .description("일별 재고 사용 여부"),
                fieldWithPath("dailyStock").type(JsonFieldType.NUMBER)
                    .description("일별 재고 수량").optional(),
                fieldWithPath("isUsedMenuQuantityPerTeam").type(JsonFieldType.BOOLEAN)
                    .description("팀 당 주문 가능 수량 사용 여부"),
                fieldWithPath("menuQuantityPerTeam").type(JsonFieldType.NUMBER)
                    .description("팀 당 주문 가능 수량").optional()
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("id").type(JsonFieldType.STRING)
                    .description("메뉴 short UUID"),
                fieldWithPath("categoryId").type(JsonFieldType.STRING)
                    .description("카테고리 short UUID"),
                fieldWithPath("name").type(JsonFieldType.STRING)
                    .description("메뉴 이름"),
                fieldWithPath("unitPrice").type(JsonFieldType.NUMBER)
                    .description("메뉴 단위 가격"),
                fieldWithPath("isUsedDailyStock").type(JsonFieldType.BOOLEAN)
                    .description("일별 재고 사용 여부"),
                fieldWithPath("dailyStock").type(JsonFieldType.NUMBER)
                    .description("일별 재고 수량").optional(),
                fieldWithPath("isUsedMenuQuantityPerTeam").type(JsonFieldType.BOOLEAN)
                    .description("팀 당 주문 가능 수량 사용 여부"),
                fieldWithPath("menuQuantityPerTeam").type(JsonFieldType.NUMBER)
                    .description("팀 당 주문 가능 수량").optional()
            )
        ));
  }


  @Test
  @DisplayName("메뉴 단건 삭제")
  void deleteMenu() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String MENU_UUID = "MENU_UUID";

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.post(
                    "/api/v1/shops/{shopId}/settings/orders/menus/{menuId}/delete",
                    SHOP_ID, MENU_UUID)
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andDo(document("order-menu-settings-delete",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디"),
                parameterWithName("menuId").description("메뉴 short UUID")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
            )
        ));
  }

  private OrderMenuSettingsRequest defaultMenuRequest(String uuid, String categoryUuid) {
    return OrderMenuSettingsRequest.builder()
        .id(uuid)
        .categoryId(categoryUuid)
        .name("사이다")
        .unitPrice(new BigDecimal(2000))
        .isUsedDailyStock(true)
        .dailyStock(1500)
        .isUsedMenuQuantityPerTeam(false)
        .build();
  }

  private OrderMenuSettingsResponse defaultMenuResponse(String uuid, String categoryUuid) {
    return OrderMenuSettingsResponse.builder()
        .id(uuid)
        .categoryId(categoryUuid)
        .name("사이다")
        .unitPrice(Price.of(new BigDecimal(2000)))
        .isUsedDailyStock(true)
        .dailyStock(1500)
        .isUsedMenuQuantityPerTeam(false)
        .build();
  }

  private OrderMenuSettingsListResponse defaultMenuListResponse() {
    return OrderMenuSettingsListResponse.builder()
        .categories(
            List.of(
                OrderCategoryDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .name("음식")
                    .ordering(1)
                    .menus(
                        List.of(
                            OrderMenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("돈까스")
                                .ordering(1)
                                .unitPrice(new BigDecimal(10000))
                                .isUsedDailyStock(false)
                                .isUsedMenuQuantityPerTeam(true)
                                .build(),

                            OrderMenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("치즈돈까스")
                                .ordering(1)
                                .unitPrice(new BigDecimal(12000))
                                .isUsedDailyStock(true)
                                .dailyStock(200)
                                .isUsedMenuQuantityPerTeam(true)
                                .menuQuantityPerTeam(2)
                                .build(),

                            OrderMenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("양념돈까스")
                                .ordering(1)
                                .unitPrice(new BigDecimal(11000))
                                .isUsedDailyStock(true)
                                .dailyStock(500)
                                .isUsedMenuQuantityPerTeam(false)
                                .build()
                        )
                    )
                    .build(),
                OrderCategoryDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .name("음료")
                    .ordering(2)
                    .menus(
                        List.of(
                            OrderMenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("콜라")
                                .ordering(1)
                                .unitPrice(new BigDecimal(2000))
                                .isUsedDailyStock(true)
                                .dailyStock(1000)
                                .isUsedMenuQuantityPerTeam(false)
                                .build(),

                            OrderMenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("사이다")
                                .ordering(2)
                                .unitPrice(new BigDecimal(2000))
                                .isUsedDailyStock(true)
                                .dailyStock(1500)
                                .isUsedMenuQuantityPerTeam(false)
                                .build()
                        )
                    )
                    .build()
            )
        )
        .build();
  }
}