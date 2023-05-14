package co.wadcorp.waiting.api.test.waiting.web;

import static co.wadcorp.waiting.api.support.ExpectedWaitingPeriodConstant.MAX_EXPRESSION_WAITING_PERIOD_CONSTANT;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.controller.waiting.web.WaitingWebController;
import co.wadcorp.waiting.api.controller.waiting.web.dto.response.OrderDto;
import co.wadcorp.waiting.api.controller.waiting.web.dto.response.OrderDto.OrderLineItemDto;
import co.wadcorp.waiting.api.controller.waiting.web.dto.response.WaitingWebResponse;
import co.wadcorp.waiting.api.controller.waiting.web.dto.response.WebCustomerWaitingListResponse;
import co.wadcorp.waiting.api.controller.waiting.web.dto.response.WebCustomerWaitingListResponse.WebCustomerWaiting;
import co.wadcorp.waiting.api.model.settings.vo.PrecautionVO;
import co.wadcorp.waiting.api.model.waiting.vo.WebPersonOptionVO;
import co.wadcorp.waiting.api.model.waiting.vo.WebPersonOptionVO.AdditionalOption;
import co.wadcorp.waiting.api.service.waiting.web.WaitingWebService;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

class WaitingWebControllerTest extends RestDocsSupport {

  private final WaitingWebService waitingWebService = mock(WaitingWebService.class);

  @Override
  public Object init() {
    return new WaitingWebController(waitingWebService);
  }

  @Test
  @DisplayName("웨이팅_웹_현황_조회")
  void getWaitingWebInfoTest() throws Exception {
    // given
    final String waitingId = "WAITING_UUID";
    WaitingWebResponse response = WaitingWebResponse.builder()
        .shopName("매장이름")
        .shopAddress("서울특별시")
        .shopTelNumber("010-0000-0000")
        .waitingStatus(WaitingStatus.WAITING)
        .waitingDetailStatus(WaitingDetailStatus.WAITING)
        .waitingNumber(101)
        .waitingOrder(1)
        .expectedWaitingPeriod(0)
        .seatOptionName("포장")
        .regDateTime("2023-01-20T08:30:46.943186+09:00")
        .maxExpressionWaitingPeriod(MAX_EXPRESSION_WAITING_PERIOD_CONSTANT)
        .totalPersonCount(3)
        .personOptions(List.of(
            WebPersonOptionVO.builder().name("성인").count(2).additionalOptions(List.of()).build(),
            WebPersonOptionVO.builder().name("유아").count(1).additionalOptions(
                    List.of(
                        AdditionalOption.builder().name("유아용의자").count(0).build()))
                .build()
        ))
        .seatOptionName("홀")
        .message("순서가 다가오고 있어요!")
        .precautions(List.of(
            PrecautionVO.builder().id(UUIDUtil.shortUUID()).content("유의사항1").build(),
            PrecautionVO.builder().id(UUIDUtil.shortUUID()).content("유의사항2").build()
        ))
        .canPutOffCount(2)
        .order(OrderDto.builder()
            .totalPrice(BigDecimal.valueOf(16000))
            .orderLineItems(List.of(
                OrderLineItemDto.builder()
                    .menuId(UUIDUtil.shortUUID())
                    .name("치즈돈가스")
                    .quantity(1)
                    .unitPrice(BigDecimal.valueOf(12000))
                    .linePrice(BigDecimal.valueOf(12000))
                    .build(),
                OrderLineItemDto.builder()
                    .menuId(UUIDUtil.shortUUID())
                    .name("콜라")
                    .quantity(2)
                    .unitPrice(BigDecimal.valueOf(2000))
                    .linePrice(BigDecimal.valueOf(4000))
                    .build()
            ))
            .build())
        .build();

    // when
    when(waitingWebService.getWaitingInfo(any(), any(LocalDate.class))).thenReturn(response);

    mockMvc.perform(RestDocumentationRequestBuilders.get("/web/v1/waiting/{waitingId}", waitingId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("web-waiting-info",
            getDocumentResponse(),
            pathParameters(
                parameterWithName("waitingId").description("웨이팅 아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("shopName").type(JsonFieldType.STRING).description("매장 이름"),
                fieldWithPath("shopAddress").type(JsonFieldType.STRING).description("매장 주소"),
                fieldWithPath("shopTelNumber").type(JsonFieldType.STRING).description("매장 연락처"),
                fieldWithPath("waitingStatus").type(JsonFieldType.STRING)
                    .description("웨이팅 상태"),
                fieldWithPath("waitingDetailStatus").type(JsonFieldType.STRING)
                    .description("웨이팅 상세 상태"),
                fieldWithPath("waitingNumber").type(JsonFieldType.NUMBER).description("웨이팅 채번"),
                fieldWithPath("waitingOrder").type(JsonFieldType.NUMBER)
                    .description("실시간 웨이팅 순서(N번째)"),
                fieldWithPath("expectedWaitingPeriod").type(JsonFieldType.NUMBER)
                    .description("예상 대기시간").optional(),
                fieldWithPath("maxExpressionWaitingPeriod").type(JsonFieldType.NUMBER)
                    .description("최대 표현 가능한 웨이팅 시간"),
                fieldWithPath("regDateTime").type(JsonFieldType.STRING).description("등록일자"),
                fieldWithPath("totalPersonCount").type(JsonFieldType.NUMBER).description("총 입장인원"),
                fieldWithPath("personOptions").type(JsonFieldType.ARRAY)
                    .description("인원 옵션 정보"),
                fieldWithPath("personOptions[].name").type(JsonFieldType.STRING)
                    .description("인원 옵션 정보 - 옵션명"),
                fieldWithPath("personOptions[].count").type(JsonFieldType.NUMBER)
                    .description("인원 옵션 정보 - 인원수"),
                fieldWithPath("personOptions[].additionalOptions[]").type(JsonFieldType.ARRAY)
                    .description("인원 옵션 정보 - 부가옵션(ex. 유아용 의자)"),
                fieldWithPath("personOptions[].additionalOptions[].name").type(JsonFieldType.STRING)
                    .description("부가옵션명").optional(),
                fieldWithPath("personOptions[].additionalOptions[].count").type(
                    JsonFieldType.NUMBER).description("부가옵션 수").optional(),
                fieldWithPath("seatOptionName").type(JsonFieldType.STRING).description("좌석 옵션명")
                    .optional(),
                fieldWithPath("precautions").type(JsonFieldType.ARRAY).description("유의사항"),
                fieldWithPath("precautions[].id").type(JsonFieldType.STRING)
                    .description("유의사항 아이디"),
                fieldWithPath("precautions[].content").type(JsonFieldType.STRING)
                    .description("유의사항 내용"),
                fieldWithPath("canPutOffCount").type(JsonFieldType.NUMBER).description("미루기 가능 횟수"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                fieldWithPath("order").type(JsonFieldType.OBJECT)
                    .description("선 주문 내역").optional(),
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
                    .description("메뉴 수량"),
                fieldWithPath("disablePutOff").type(JsonFieldType.BOOLEAN)
                    .description("미루기 off 매장 여부")
            )));
  }

  @Test
  @DisplayName("웨이팅_웹_목록_조회")
  public void getAllOtherWaitingByCustomerTest() throws Exception {
    // given
    final String waitingId = "WAITING_UUID";
    WebCustomerWaitingListResponse response = WebCustomerWaitingListResponse.builder()
        .customerPhone("010-1234-5678")
        .waitingList(List.of(WebCustomerWaiting.builder()
            .waitingId(UUIDUtil.shortUUID())
            .shopName("매장명")
            .waitingOrder(12)
            .expectedWaitingPeriod(48)
            .maxExpressionWaitingPeriod(MAX_EXPRESSION_WAITING_PERIOD_CONSTANT)
            .regDateTime("2023-01-17T16:24:35.781257+09:00")
            .build()))
        .build();

    // when
    when(
        waitingWebService.getAllCustomerWaitingByWaitingId(any(), any(LocalDate.class))).thenReturn(
        response);

    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/web/v1/waiting/{waitingId}/list", waitingId)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("web-waiting-list",
            getDocumentResponse(),
            pathParameters(
                parameterWithName("waitingId").description("웨이팅 아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("customerPhone").type(JsonFieldType.STRING).description("고객 연락처"),
                fieldWithPath("waitingList").type(JsonFieldType.ARRAY).description("내 웨이팅 목록"),
                fieldWithPath("waitingList[].waitingId").type(JsonFieldType.STRING)
                    .description("웨이팅 아이디"),
                fieldWithPath("waitingList[].shopName").type(JsonFieldType.STRING)
                    .description("매장명"),
                fieldWithPath("waitingList[].seatOptionName").type(JsonFieldType.STRING)
                    .description("테이블모드 좌석명 (기본모드는 정보없음)").optional(),
                fieldWithPath("waitingList[].waitingOrder").type(JsonFieldType.NUMBER)
                    .description("N번째"),
                fieldWithPath("waitingList[].expectedWaitingPeriod").type(JsonFieldType.NUMBER)
                    .description("예상 대기시간"),
                fieldWithPath("waitingList[].maxExpressionWaitingPeriod").type(JsonFieldType.NUMBER)
                    .description("최대 표현 가능한 웨이팅 시간"),
                fieldWithPath("waitingList[].regDateTime").type(JsonFieldType.STRING)
                    .description("등록일자")
            )));
  }

}
