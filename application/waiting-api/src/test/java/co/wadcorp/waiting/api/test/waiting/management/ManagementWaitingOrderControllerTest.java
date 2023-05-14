package co.wadcorp.waiting.api.test.waiting.management;

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
import co.wadcorp.waiting.api.controller.waiting.management.ManagementWaitingOrderController;
import co.wadcorp.waiting.api.service.waiting.management.ManagementUpdateWaitingOrderApiService;
import co.wadcorp.waiting.api.service.waiting.management.ManagementWaitingOrderApiService;
import co.wadcorp.waiting.api.service.waiting.management.dto.request.UpdateWaitingOrderServiceRequest;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementWaitingOrderResponse;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementWaitingOrderResponse.OrderDto;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementWaitingOrderResponse.OrderDto.OrderLineItemDto;
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

class ManagementWaitingOrderControllerTest extends RestDocsSupport {

  private final ManagementWaitingOrderApiService managementWaitingOrderApiService = mock(ManagementWaitingOrderApiService.class);
  private final ManagementUpdateWaitingOrderApiService managementUpdateWaitingOrderApiService = mock(ManagementUpdateWaitingOrderApiService.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object init() {
    return new ManagementWaitingOrderController(managementWaitingOrderApiService, managementUpdateWaitingOrderApiService);
  }

  @Test
  @DisplayName("대시보드 - 주문정보 변경 조회 (모달)")
  void getOrder() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String ORDER_ID = "ORDER_ID";

    // when
    when(managementWaitingOrderApiService.getOrder(any(), any(), any(LocalDate.class))).thenReturn(defaultResponse());

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/management/orders/{orderId}",
                    SHOP_ID, ORDER_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("management-waiting-order-get",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장 아이디 shortUUID"),
                parameterWithName("orderId").description("주문 아이디 shortUUID")
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
                fieldWithPath("categories[].menus[].remainingQuantity").type(JsonFieldType.NUMBER)
                    .description("메뉴 남은 재고").optional(),
                fieldWithPath("order").type(JsonFieldType.OBJECT)
                    .description("선 주문 내역").optional(),
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
                    .description("메뉴 수량"),
                fieldWithPath("order.orderLineItems[].isUsedDailyStock").type(JsonFieldType.BOOLEAN)
                    .description("일별 재고 사용 여부"),
                fieldWithPath("order.orderLineItems[].remainingQuantity").type(JsonFieldType.NUMBER)
                    .description("메뉴 남은 재고").optional(),
                fieldWithPath("order.orderLineItems[].isDeletedMenu").type(JsonFieldType.BOOLEAN)
                    .description("메뉴 삭제 여부")
            )
        ));
  }

  @Test
  @DisplayName("대시보드 - 주문정보 변경 수정 (모달)")
  void updateOrder() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String ORDER_ID = "ORDER_ID";

    UpdateWaitingOrderServiceRequest request = UpdateWaitingOrderServiceRequest.builder()
        .order(UpdateWaitingOrderServiceRequest.OrderDto.builder()
            .orderLineItems(
                List.of(
                    UpdateWaitingOrderServiceRequest.OrderLineItemDto.builder()
                        .menuId(UUIDUtil.shortUUID())
                        .name("치즈돈가스")
                        .quantity(1)
                        .unitPrice(BigDecimal.valueOf(12000))
                        .build(),
                    UpdateWaitingOrderServiceRequest.OrderLineItemDto.builder()
                        .menuId(UUIDUtil.shortUUID())
                        .name("콜라")
                        .quantity(2)
                        .unitPrice(BigDecimal.valueOf(2000))
                        .build()
                )
            )
            .build())
        .build();

    // when
    when(managementUpdateWaitingOrderApiService.updateOrder(
        any(),
        any(),
        any(UpdateWaitingOrderServiceRequest.class),
        any())
    ).thenReturn(defaultResponse());

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/management/orders/{orderId}",
                    SHOP_ID, ORDER_ID)
                .header("X-REQUEST-ID", "deviceId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("management-waiting-order-update",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장 아이디 shortUUID"),
                parameterWithName("orderId").description("주문 아이디 shortUUID")
            ),
            requestFields(
                fieldWithPath("order").type(JsonFieldType.OBJECT)
                    .description("선 주문 내역"),
                fieldWithPath("order.orderLineItems").type(JsonFieldType.ARRAY)
                    .description("선주문 메뉴 목록"),
                fieldWithPath("order.orderLineItems[].menuId").type(JsonFieldType.STRING)
                    .description("메뉴 short UUID"),
                fieldWithPath("order.orderLineItems[].name").type(JsonFieldType.STRING)
                    .description("메뉴 이름"),
                fieldWithPath("order.orderLineItems[].unitPrice").type(JsonFieldType.NUMBER)
                    .description("메뉴 단위 가격"),
                fieldWithPath("order.orderLineItems[].quantity").type(JsonFieldType.NUMBER)
                    .description("메뉴 수량")
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
                fieldWithPath("categories[].menus[].remainingQuantity").type(JsonFieldType.NUMBER)
                    .description("메뉴 남은 재고").optional(),
                fieldWithPath("order").type(JsonFieldType.OBJECT)
                    .description("선 주문 내역").optional(),
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
                    .description("메뉴 수량"),
                fieldWithPath("order.orderLineItems[].isUsedDailyStock").type(JsonFieldType.BOOLEAN)
                    .description("일별 재고 사용 여부"),
                fieldWithPath("order.orderLineItems[].remainingQuantity").type(JsonFieldType.NUMBER)
                    .description("메뉴 남은 재고").optional(),
                fieldWithPath("order.orderLineItems[].isDeletedMenu").type(JsonFieldType.BOOLEAN)
                    .description("메뉴 삭제 여부")
            )
        ));
  }


  public ManagementWaitingOrderResponse defaultResponse() {
    return ManagementWaitingOrderResponse.builder()
        .categories(
            List.of(
                ManagementWaitingOrderResponse.OrderCategory.builder()
                    .id(UUIDUtil.shortUUID())
                    .name("음식")
                    .ordering(1)
                    .menus(
                        List.of(
                            ManagementWaitingOrderResponse.OrderMenu.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("돈까스")
                                .ordering(1)
                                .unitPrice(new BigDecimal(10000))
                                .isUsedDailyStock(true)
                                .remainingQuantity(100)
                                .build(),

                            ManagementWaitingOrderResponse.OrderMenu.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("치즈돈까스")
                                .ordering(1)
                                .unitPrice(new BigDecimal(12000))
                                .isUsedDailyStock(true)
                                .remainingQuantity(100)
                                .build(),

                            ManagementWaitingOrderResponse.OrderMenu.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("양념돈까스")
                                .ordering(1)
                                .unitPrice(new BigDecimal(11000))
                                .isUsedDailyStock(true)
                                .remainingQuantity(100)
                                .build()
                        )
                    )
                    .build(),
                ManagementWaitingOrderResponse.OrderCategory.builder()
                    .id(UUIDUtil.shortUUID())
                    .name("음료")
                    .ordering(2)
                    .menus(
                        List.of(
                            ManagementWaitingOrderResponse.OrderMenu.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("콜라")
                                .ordering(1)
                                .unitPrice(new BigDecimal(2000))
                                .isUsedDailyStock(true)
                                .remainingQuantity(1000)
                                .build(),

                            ManagementWaitingOrderResponse.OrderMenu.builder()
                                .id(UUIDUtil.shortUUID())
                                .name("사이다")
                                .ordering(2)
                                .unitPrice(new BigDecimal(2000))
                                .isUsedDailyStock(true)
                                .remainingQuantity(1500)
                                .build()
                        )
                    )
                    .build()
            )
        )
        .order(OrderDto.builder()
            .orderLineItems(
                List.of(
                    OrderLineItemDto.builder()
                        .menuId(UUIDUtil.shortUUID())
                        .name("치즈돈가스")
                        .quantity(1)
                        .unitPrice(BigDecimal.valueOf(12000))
                        .linePrice(BigDecimal.valueOf(12000))
                        .isUsedDailyStock(true)
                        .remainingQuantity(50)
                        .isDeletedMenu(false)
                        .build(),
                    OrderLineItemDto.builder()
                        .menuId(UUIDUtil.shortUUID())
                        .name("콜라")
                        .quantity(2)
                        .unitPrice(BigDecimal.valueOf(2000))
                        .linePrice(BigDecimal.valueOf(4000))
                        .isUsedDailyStock(true)
                        .remainingQuantity(200)
                        .isDeletedMenu(false)
                        .build()
                )
            )
            .build())
        .build();
  }
}