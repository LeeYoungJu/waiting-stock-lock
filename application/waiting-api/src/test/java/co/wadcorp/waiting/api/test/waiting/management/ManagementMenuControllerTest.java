package co.wadcorp.waiting.api.test.waiting.management;

import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentRequest;
import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.controller.waiting.management.ManagementMenuController;
import co.wadcorp.waiting.api.service.waiting.management.ManagementDisplayMenuApiService;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementOrderMenuResponse;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementOrderMenuResponse.CategoryDto;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementOrderMenuResponse.MenuDto;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

class ManagementMenuControllerTest extends RestDocsSupport {

  private final ManagementDisplayMenuApiService managementDisplayMenuApiService = mock(
      ManagementDisplayMenuApiService.class);

  @Override
  public Object init() {
    return new ManagementMenuController(managementDisplayMenuApiService);
  }


  @Test
  @DisplayName("대시보드 - 수기등록 메뉴 조회")
  void getMenus() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";

    // when
    when(managementDisplayMenuApiService.getDisplayMenu(any(), any(), any(LocalDate.class))).thenReturn(defaultResponse());

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.get(
                    "/api/v1/shops/{shopId}/management/waiting/orders/menus",
                    SHOP_ID)
                .queryParam("menuType", "SHOP_MENU")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("management-waiting-menus-get",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장 아이디 shortUUID")
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
                fieldWithPath("categories[].menus[].isUsedMenuQuantityPerTeam")
                    .type(JsonFieldType.BOOLEAN)
                    .description("팀 당 주문 가능 수량 사용 여부"),
                fieldWithPath("categories[].menus[].menuQuantityPerTeam").type(JsonFieldType.NUMBER)
                    .optional()
                    .description("팀 당 주문 가능 수량"),
                fieldWithPath("categories[].menus[].isUsedDailyStock").type(JsonFieldType.BOOLEAN)
                    .description("일별 재고 사용 여부"),
                fieldWithPath("categories[].menus[].remainingQuantity").type(JsonFieldType.NUMBER)
                    .description("메뉴 남은 재고").optional()
            )
        ));
  }

  private ManagementOrderMenuResponse defaultResponse() {
    return ManagementOrderMenuResponse.builder()
        .categories(
            List.of(
                CategoryDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .name("음식")
                    .ordering(1)
                    .menus(
                        List.of(
                            MenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("돈까스")
                                .ordering(1)
                                .unitPrice(new BigDecimal(10000))
                                .isUsedMenuQuantityPerTeam(true)
                                .menuQuantityPerTeam(2)
                                .isUsedDailyStock(true)
                                .remainingQuantity(2)
                                .build(),

                            MenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("치즈돈까스")
                                .ordering(1)
                                .unitPrice(new BigDecimal(12000))
                                .isUsedMenuQuantityPerTeam(true)
                                .menuQuantityPerTeam(2)
                                .isUsedDailyStock(true)
                                .remainingQuantity(2)
                                .build(),

                            MenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("양념돈까스")
                                .ordering(1)
                                .unitPrice(new BigDecimal(11000))
                                .isUsedMenuQuantityPerTeam(true)
                                .menuQuantityPerTeam(2)
                                .isUsedDailyStock(true)
                                .remainingQuantity(2)
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
                            MenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("콜라")
                                .ordering(1)
                                .unitPrice(new BigDecimal(2000))
                                .isUsedMenuQuantityPerTeam(true)
                                .menuQuantityPerTeam(2)
                                .isUsedDailyStock(true)
                                .remainingQuantity(2)
                                .build(),

                            MenuDto.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("사이다")
                                .ordering(2)
                                .unitPrice(new BigDecimal(2000))
                                .isUsedMenuQuantityPerTeam(true)
                                .menuQuantityPerTeam(2)
                                .isUsedDailyStock(true)
                                .remainingQuantity(2)
                                .build()
                        )
                    )
                    .build()
            )
        )
        .build();
  }

}