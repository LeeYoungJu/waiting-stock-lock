package co.wadcorp.waiting.api.test.internal.waiting;

import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentRequest;
import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.internal.controller.waiting.RemoteWaitingOrderController;
import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteOrderRequest;
import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteOrderRequest.OrderLineItem;
import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteWaitingOrderValidateRequest;
import co.wadcorp.waiting.api.internal.service.waiting.RemoteDisplayMenuApiService;
import co.wadcorp.waiting.api.internal.service.waiting.RemoteWaitingOrderValidateApiService;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingOrderMenuResponse;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingOrderMenuResponse.CategoryDto;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingOrderMenuResponse.MenuDto;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

public class RemoteWaitingOrderControllerDocsTest extends RestDocsSupport {

  private final RemoteDisplayMenuApiService remoteDisplayMenuApiService = mock(
      RemoteDisplayMenuApiService.class
  );
  private final RemoteWaitingOrderValidateApiService remoteWaitingOrderValidateApiService = mock(
      RemoteWaitingOrderValidateApiService.class
  );

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object init() {
    return new RemoteWaitingOrderController(remoteDisplayMenuApiService,
        remoteWaitingOrderValidateApiService);
  }

  @DisplayName("원격 웨이팅 선주문 메뉴 조회")
  @Test
  void getOrderMenu() throws Exception {
    // given
    final String SHOP_ID = "SHOP_ID";
    String menuType = "SHOP_MENU";

    MenuDto menu1 = MenuDto.builder()
        .id(UUIDUtil.shortUUID())
        .name("돈까스")
        .ordering(1)
        .unitPrice(new BigDecimal(10000))
        .isUsedMenuQuantityPerTeam(true)
        .menuQuantityPerTeam(3)
        .isUsedDailyStock(true)
        .remainingQuantity(100)
        .isOutOfStock(false)
        .build();

    MenuDto menu2 = MenuDto.builder()
        .id(UUIDUtil.shortUUID())
        .name("냉면")
        .ordering(2)
        .unitPrice(new BigDecimal(8000))
        .isUsedMenuQuantityPerTeam(true)
        .menuQuantityPerTeam(2)
        .isUsedDailyStock(true)
        .remainingQuantity(110)
        .isOutOfStock(false)
        .build();

    MenuDto menu3 = MenuDto.builder()
        .id(UUIDUtil.shortUUID())
        .name("볶음밥")
        .ordering(3)
        .unitPrice(new BigDecimal(9000))
        .isUsedMenuQuantityPerTeam(true)
        .menuQuantityPerTeam(5)
        .isUsedDailyStock(true)
        .remainingQuantity(3)
        .isOutOfStock(true)
        .build();

    MenuDto menu4 = MenuDto.builder()
        .id(UUIDUtil.shortUUID())
        .name("콜라")
        .ordering(1)
        .unitPrice(new BigDecimal(2000))
        .isUsedMenuQuantityPerTeam(false)
        .isUsedDailyStock(true)
        .remainingQuantity(100)
        .isOutOfStock(false)
        .build();

    MenuDto menu5 = MenuDto.builder()
        .id(UUIDUtil.shortUUID())
        .name("사이다")
        .ordering(2)
        .unitPrice(new BigDecimal(2100))
        .isUsedMenuQuantityPerTeam(false)
        .isUsedDailyStock(true)
        .remainingQuantity(200)
        .isOutOfStock(false)
        .build();

    CategoryDto category1 = CategoryDto.builder()
        .id(UUIDUtil.shortUUID()).name("음식").ordering(1)
        .menus(List.of(menu1, menu2, menu3))
        .build();

    CategoryDto category2 = CategoryDto.builder()
        .id(UUIDUtil.shortUUID()).name("음료").ordering(1)
        .menus(List.of(menu4, menu5))
        .build();

    RemoteWaitingOrderMenuResponse response = createOrderMenuResponse(List.of(category1, category2));

    // when
    when(remoteDisplayMenuApiService.getOrderMenu(any(), any(DisplayMappingType.class), any()))
        .thenReturn(response);

    // then
    mockMvc.perform(
        get("/internal/api/v1/shops/{shopIds}/orders", SHOP_ID)
            .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
            .queryParam("menuType", menuType)
        )
        .andExpect(status().isOk())
        .andDo(document("remote-waiting-order-menu-get",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopIds").description("B2C의 shopSeq")
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

  @DisplayName("원격 웨이팅 선주문 요청 검증")
  @Test
  void checkOrderMenu() throws Exception {
    // given
    final String SHOP_ID = "SHOP_ID";
    BigDecimal totalPrice = BigDecimal.valueOf(14000);

    OrderLineItem menu1 = OrderLineItem.builder()
        .menuId(UUIDUtil.shortUUID())
        .name("돈까스")
        .quantity(1)
        .unitPrice(BigDecimal.valueOf(10000))
        .linePrice(BigDecimal.valueOf(10000))
        .build();

    OrderLineItem menu2 = OrderLineItem.builder()
        .menuId(UUIDUtil.shortUUID())
        .name("콜라")
        .quantity(2)
        .unitPrice(BigDecimal.valueOf(2000))
        .linePrice(BigDecimal.valueOf(4000))
        .build();

    RemoteWaitingOrderValidateRequest request = createOrderMenuValidateRequest(totalPrice,
        List.of(menu1, menu2));

    // then
    mockMvc.perform(
        post("/internal/api/v1/shops/{shopIds}/orders/validation", SHOP_ID)
            .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andDo(
            document("remote-waiting-order-menu-validate",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("shopIds").description("B2C의 shopSeq")
                ),
                requestFields(
                    fieldWithPath("order").type(JsonFieldType.OBJECT)
                        .description("선 주문 내역"),
                    fieldWithPath("order.totalPrice").type(JsonFieldType.NUMBER)
                        .description("총 주문 금액"),
                    fieldWithPath("order.orderLineItems").type(JsonFieldType.ARRAY)
                        .description("선주문 메뉴 목록"),
                    fieldWithPath("order.orderLineItems[].menuId").type(JsonFieldType.STRING)
                        .description("메뉴 short UUID"),
                    fieldWithPath("order.orderLineItems[].name").type(JsonFieldType.STRING)
                        .description("메뉴 이름"),
                    fieldWithPath("order.orderLineItems[].unitPrice").type(JsonFieldType.NUMBER)
                        .description("메뉴 단위 가격"),
                    fieldWithPath("order.orderLineItems[].linePrice").type(JsonFieldType.NUMBER)
                        .description("메뉴 총 가격"),
                    fieldWithPath("order.orderLineItems[].quantity").type(JsonFieldType.NUMBER)
                        .description("메뉴 수량")
                ),
                responseFields(
                    beneathPath("data").withSubsectionId("data"),
                    fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                        .description("성공 여부")
                )
            ));
  }

  private RemoteWaitingOrderMenuResponse createOrderMenuResponse(List<CategoryDto> categories) {
    return RemoteWaitingOrderMenuResponse.builder()
        .categories(categories)
        .build();
  }

  private RemoteWaitingOrderValidateRequest createOrderMenuValidateRequest(
      BigDecimal totalPrice, List<OrderLineItem> orderLineItems) {
    return RemoteWaitingOrderValidateRequest.builder()
        .order(RemoteOrderRequest.builder()
            .totalPrice(totalPrice)
            .orderLineItems(orderLineItems)
            .build()
        )
        .build();
  }
}
