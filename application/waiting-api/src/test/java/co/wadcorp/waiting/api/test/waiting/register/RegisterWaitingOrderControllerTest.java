package co.wadcorp.waiting.api.test.waiting.register;

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
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.controller.waiting.register.RegisterWaitingOrderController;
import co.wadcorp.waiting.api.controller.waiting.register.dto.request.ValidateWaitingOrderManuStockRequest;
import co.wadcorp.waiting.api.service.waiting.register.RegisterDisplayMenuApiService;
import co.wadcorp.waiting.api.service.waiting.register.RegisterValidateMenuApiService;
import co.wadcorp.waiting.api.service.waiting.register.dto.response.RegisterWaitingOrderMenuResponse;
import co.wadcorp.waiting.api.service.waiting.register.dto.response.RegisterWaitingOrderMenuResponse.CategoryDto;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

class RegisterWaitingOrderControllerTest extends RestDocsSupport {

  private final RegisterDisplayMenuApiService registerDisplayMenuApiService = mock(
      RegisterDisplayMenuApiService.class);
  private final RegisterValidateMenuApiService registerValidateMenuApiService = mock(
      RegisterValidateMenuApiService.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object init() {
    return new RegisterWaitingOrderController(registerDisplayMenuApiService, registerValidateMenuApiService);
  }

  @Test
  @DisplayName("등록 - 웨이팅 등록 시 카테고리/메뉴 조회")
  void getOrderMenu() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    RegisterWaitingOrderMenuResponse response = defaultMenuListResponse();

    // when
    when(registerDisplayMenuApiService.getOrderMenu(any(), any(), any(LocalDate.class))).thenReturn(response);

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/register/orders",
                    SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("menuType", "SHOP_MENU"))
        .andExpect(status().isOk())
        .andDo(document("register-waiting-order-get",
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
                fieldWithPath("categories[].menus[].isUsedMenuQuantityPerTeam").type(
                        JsonFieldType.BOOLEAN)
                    .description("팀 당 주문 가능 수량 사용 여부"),
                fieldWithPath("categories[].menus[].menuQuantityPerTeam").type(JsonFieldType.NUMBER)
                    .description("팀 당 주문 가능 수량").optional(),
                fieldWithPath("categories[].menus[].isUsedDailyStock").type(JsonFieldType.BOOLEAN)
                    .description("일별 재고 사용 여부"),
                fieldWithPath("categories[].menus[].remainingQuantity").type(JsonFieldType.NUMBER)
                    .description("메뉴 남은 재고").optional(),
                fieldWithPath("categories[].menus[].isOutOfStock").type(JsonFieldType.BOOLEAN)
                    .description("품절 여부").optional()
            )
        ));
  }

  @Test
  @DisplayName("등록 - 웨이팅 등록 시 재고 체크")
  void validateStock() throws Exception {

    // given
    final String SHOP_ID = "SHOP_UUID";
    final String MENU_UUID = "MENU_UUID";

    ValidateWaitingOrderManuStockRequest request = ValidateWaitingOrderManuStockRequest.builder()
        .menus(List.of(
            ValidateWaitingOrderManuStockRequest.Menus.builder()
                .id(UUIDUtil.shortUUID())
                .name("치즈돈가스")
                .quantity(1)
                .build(),

            ValidateWaitingOrderManuStockRequest.Menus.builder()
                .id(UUIDUtil.shortUUID())
                .name("콜라")
                .quantity(1)
                .build()
        ))
        .build();

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.post(
                    "/api/v1/shops/{shopId}/register/orders/stock-validation",
                    SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("menuType", "SHOP_MENU")
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andDo(document("register-waiting-order-stock-validation",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            queryParameters(
                parameterWithName("menuType").description(
                    "매장/포장 메뉴 타입: SHOP_MENU(매장 메뉴), TAKE_OUT_MENU(포장 메뉴)")
            ),
            requestFields(
                fieldWithPath("menus").type(JsonFieldType.ARRAY).description("선택한 메뉴 목록"),
                fieldWithPath("menus[].id").type(JsonFieldType.STRING)
                    .description("메뉴 short UUID"),
                fieldWithPath("menus[].name").type(JsonFieldType.STRING).description("메뉴 이름"),
                fieldWithPath("menus[].quantity").type(JsonFieldType.NUMBER).description("선택 수량")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
            )
        ));

  }

  private RegisterWaitingOrderMenuResponse defaultMenuListResponse() {
    return RegisterWaitingOrderMenuResponse.builder()
        .categories(
            List.of(
                CategoryDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .name("음식")
                    .ordering(1)
                    .menus(
                        List.of(
                            RegisterWaitingOrderMenuResponse.MenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("돈까스")
                                .ordering(1)
                                .unitPrice(new BigDecimal(10000))
                                .isUsedMenuQuantityPerTeam(true)
                                .isUsedDailyStock(true)
                                .remainingQuantity(100)
                                .build(),

                            RegisterWaitingOrderMenuResponse.MenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("치즈돈까스")
                                .ordering(1)
                                .unitPrice(new BigDecimal(12000))
                                .isUsedMenuQuantityPerTeam(true)
                                .menuQuantityPerTeam(2)
                                .isUsedDailyStock(true)
                                .remainingQuantity(100)
                                .build(),

                            RegisterWaitingOrderMenuResponse.MenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("양념돈까스")
                                .ordering(1)
                                .unitPrice(new BigDecimal(11000))
                                .isUsedDailyStock(false)
                                .isUsedMenuQuantityPerTeam(false)
                                .build()
                        )
                    )
                    .build(),
                CategoryDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .name("음료")
                    .ordering(2)
                    .menus(
                        List.of(
                            RegisterWaitingOrderMenuResponse.MenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("콜라")
                                .ordering(1)
                                .unitPrice(new BigDecimal(2000))
                                .isUsedMenuQuantityPerTeam(false)
                                .isUsedDailyStock(true)
                                .remainingQuantity(100)
                                .build(),

                            RegisterWaitingOrderMenuResponse.MenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("사이다")
                                .ordering(2)
                                .unitPrice(new BigDecimal(2000))
                                .isUsedMenuQuantityPerTeam(false)
                                .isUsedDailyStock(true)
                                .remainingQuantity(200)
                                .build()
                        )
                    )
                    .build()
            )
        )
        .build();
  }

}