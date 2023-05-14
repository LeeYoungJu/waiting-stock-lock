package co.wadcorp.waiting.api.test.waiting.register;


import static co.wadcorp.waiting.api.support.ExpectedWaitingPeriodConstant.MAX_EXPRESSION_WAITING_PERIOD_CONSTANT;
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

import co.wadcorp.waiting.api.controller.waiting.register.RegisterWaitingListController;
import co.wadcorp.waiting.api.model.waiting.response.OtherWaitingListResponse;
import co.wadcorp.waiting.api.model.waiting.response.RegisterWaitingListResponse;
import co.wadcorp.waiting.api.model.waiting.response.RegisterWaitingListResponse.RegisterWaitingDto;
import co.wadcorp.waiting.api.model.waiting.vo.PageVO;
import co.wadcorp.waiting.api.service.waiting.WaitingRegisterApiService;
import co.wadcorp.waiting.api.service.waiting.WaitingRegisterOtherWaitingService;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;


public class RegisterWaitingListControllerTest extends RestDocsSupport {

  private final WaitingRegisterApiService waitingRegisterApiService = mock(
      WaitingRegisterApiService.class);
  private final WaitingRegisterOtherWaitingService waitingRegisterOtherWaitingService = mock(
      WaitingRegisterOtherWaitingService.class);

  @Override
  public Object init() {
    return new RegisterWaitingListController(waitingRegisterApiService,
        waitingRegisterOtherWaitingService);
  }


  @Test
  @DisplayName("타매장_웨이팅_목록_조회")
  public void getAllWaitingOfOtherShopTest() throws Exception {
    final String SHOP_ID = "SHOP_UUID";
    final String WAITING_ID = "WAITING_UUID";
    final String PHONE = "010-1234-5678";

    // given
    List<OtherWaitingListResponse> response = List.of(
        OtherWaitingListResponse.builder()
            .shopId(SHOP_ID)
            .waitingId(WAITING_ID)
            .shopName("매장명")
            .waitingOrder(13)
            .expectedWaitingPeriod(130)
            .maxExpressionWaitingPeriod(MAX_EXPRESSION_WAITING_PERIOD_CONSTANT)
            .regDateTime("2023-01-17T16:24:35.781257+09:00")
            .build()
    );

    // when
    when(waitingRegisterOtherWaitingService.getAllWaitingOfOtherShopByCustomerPhone(
        any(),
        any(),
        any(LocalDate.class)
    )).thenReturn(response);

    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/register/waiting/others",
                    SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("customerPhone", PHONE))
        .andExpect(status().isOk())
        .andDo(document("other-waiting-list",
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            queryParameters(
                parameterWithName("customerPhone").description("고객 전화번호")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("waitingId").type(JsonFieldType.STRING).description("웨이팅 아이디"),
                fieldWithPath("shopId").type(JsonFieldType.STRING).description("매장아이디"),
                fieldWithPath("shopName").type(JsonFieldType.STRING).description("매장명"),
                fieldWithPath("waitingOrder").type(JsonFieldType.NUMBER).description("N번째"),
                fieldWithPath("expectedWaitingPeriod").type(JsonFieldType.NUMBER)
                    .description("예상 대기시간").optional(),
                fieldWithPath("maxExpressionWaitingPeriod").type(JsonFieldType.NUMBER)
                    .description("최대 표현 가능한 웨이팅 시간"),
                fieldWithPath("regDateTime").type(JsonFieldType.STRING).description("등록일자")
            )));
  }

  @Test
  @DisplayName("등록_웨이팅_목록_조회")
  public void getAllWaitingTest() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";

    List<RegisterWaitingDto> waitingList = List.of(RegisterWaitingDto.builder()
        .waitingNumber(301)
        .seatOptionName("테이블1")
        .totalPersonCount(2)
        .regDateTime(ZonedDateTimeUtils.nowOfSeoul())
        .build());

    RegisterWaitingListResponse response = RegisterWaitingListResponse.builder()
        .page(new PageVO(waitingList.size(), 1, 10))
        .waiting(waitingList)
        .build();

    // when
    when(waitingRegisterApiService.getAllWaiting(any(), any(), any(LocalDate.class)))
        .thenReturn(response);

    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/register/waiting/list",
                    SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("page", "1")
                .queryParam("limit", "10"))
        .andExpect(status().isOk())
        .andDo(document("register-waiting-list",
            getDocumentRequest(),
            getDocumentResponse(),
            queryParameters(
                parameterWithName("page").description("페이지 번호 (기본값-1)").optional(),
                parameterWithName("limit").description("페이지 크기 (기본값-10)").optional()
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("waiting").type(JsonFieldType.ARRAY).description("웨이팅 목록"),
                fieldWithPath("waiting[].seatOptionName").type(JsonFieldType.STRING)
                    .description("좌석 옵션 이름"),
                fieldWithPath("waiting[].waitingNumber").type(JsonFieldType.NUMBER)
                    .description("웨이팅 채번"),
                fieldWithPath("waiting[].totalPersonCount").type(JsonFieldType.NUMBER)
                    .description("총 인원수"),
                fieldWithPath("waiting[].regDateTime").type(JsonFieldType.STRING)
                    .description("등록일자"),
                fieldWithPath("totalCount").type(JsonFieldType.NUMBER).description("웨이팅 총 개수"),
                fieldWithPath("page").type(JsonFieldType.NUMBER).description("페이지 번호"),
                fieldWithPath("limit").type(JsonFieldType.NUMBER).description("페이지 크기")
            )));

  }
}
