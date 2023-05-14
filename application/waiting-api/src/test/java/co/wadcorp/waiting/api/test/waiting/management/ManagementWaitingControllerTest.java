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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.controller.waiting.management.ManagementWaitingController;
import co.wadcorp.waiting.api.model.waiting.response.WaitingResponse;
import co.wadcorp.waiting.api.model.waiting.vo.WaitingVO;
import co.wadcorp.waiting.api.service.waiting.WaitingApiService;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.data.domain.waiting.PersonOption;
import co.wadcorp.waiting.data.domain.waiting.PersonOption.AdditionalOption;
import co.wadcorp.waiting.data.domain.waiting.PersonOptionsData;
import co.wadcorp.waiting.data.domain.waiting.RegisterChannel;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

class ManagementWaitingControllerTest extends RestDocsSupport {

  private final WaitingApiService service = mock(WaitingApiService.class);

  @Override
  public Object init() {
    return new ManagementWaitingController(service);
  }

  @Test
  @DisplayName("웨이팅 단건 조회")
  void waitingList() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String WAITING_ID = "WAITING_ID";
    final String ctmAuth = "Bearer accessToken";

    LocalDate operationDate = LocalDate.of(2023, 2, 1);

    WaitingVO waiting = createWaitingList(SHOP_ID, WAITING_ID, operationDate);

    WaitingResponse response = WaitingResponse.builder()
        .waiting(waiting)
        .build();

    // when
    when(service.getWaitingBy(any(String.class))).thenReturn(response);

    mockMvc.perform(
            RestDocumentationRequestBuilders.get(
                    "/api/v1/shops/{shopId}/management/waiting/{waitingId}",
                    SHOP_ID, WAITING_ID
                )
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-CTM-AUTH", ctmAuth))
        .andExpect(status().isOk())
        .andDo(document("management-waiting",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디"),
                parameterWithName("waitingId").description("웨이팅 아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),

                fieldWithPath("waitingId").type(JsonFieldType.STRING)
                    .description("웨이팅 아이디"),
                fieldWithPath("shopId").type(JsonFieldType.STRING).description("매장 아이디"),
                fieldWithPath("registerChannel").type(JsonFieldType.STRING)
                    .description("등록 체널: WAITING_APP(현장), WAITING_MANAGER(수기), CATCH_APP(원격)"),
                fieldWithPath("registerChannelText").type(JsonFieldType.STRING)
                    .description("등록 체널 텍스트 - 현장, 수기, 원격"),
                fieldWithPath("operationDate").type(JsonFieldType.STRING)
                    .description("영업일"),
                fieldWithPath("customerSeq").type(JsonFieldType.NUMBER)
                    .description("고객 시퀀스").optional(),
                fieldWithPath("customerPhoneNumber").type(JsonFieldType.STRING)
                    .description("고객 연락처").optional(),
                fieldWithPath("customerName").type(JsonFieldType.STRING)
                    .description("고객 이름").optional(),
                fieldWithPath("sittingCount").type(JsonFieldType.NUMBER)
                    .description("착석 횟수"),
                fieldWithPath("waitingNumber").type(JsonFieldType.NUMBER)
                    .description("웨이팅 채번"),
                fieldWithPath("waitingOrder").type(JsonFieldType.NUMBER)
                    .description("웨이팅 순번"),
                fieldWithPath("waitingStatus").type(JsonFieldType.STRING)
                    .description("웨이팅 상태"),
                fieldWithPath("waitingDetailStatus").type(JsonFieldType.STRING)
                    .description("웨이팅 상세 상태"),
                fieldWithPath("waitingMemo").type(JsonFieldType.STRING)
                    .description("웨이팅 메모"),
                fieldWithPath("seatOptionName").type(JsonFieldType.STRING)
                    .description("좌석 옵션 이름"),
                fieldWithPath("totalSeatCount").type(JsonFieldType.NUMBER)
                    .description("총 좌석수"),
                fieldWithPath("personOptionText").type(JsonFieldType.STRING)
                    .description("인원 옵션 설정 정보 - 텍스트"),
                fieldWithPath("personOptions").type(JsonFieldType.ARRAY)
                    .description("인원 옵션 설정 정보"),
                fieldWithPath("personOptions[].name").type(JsonFieldType.STRING)
                    .description("인원 옵션 - 이름 "),
                fieldWithPath("personOptions[].count").type(JsonFieldType.NUMBER)
                    .description("인원 옵션 - 수"),
                fieldWithPath("personOptions[].additionalOptions").type(
                    JsonFieldType.ARRAY).description("추가 인원 옵션").optional(),
                fieldWithPath("personOptions[].additionalOptions[].name").type(
                    JsonFieldType.STRING).description("추가 인원 옵션 - 이름").optional(),
                fieldWithPath("personOptions[].additionalOptions[].count").type(
                    JsonFieldType.NUMBER).description("추가 인원 옵션 - 수").optional(),
                fieldWithPath("expectedSittingDateTime").type(JsonFieldType.STRING)
                    .description("예상 착석 시각").optional(),
                fieldWithPath("waitingCompleteDateTime").type(JsonFieldType.STRING)
                    .description("웨이팅 완료(착석, 취소) 시각").optional(),
                fieldWithPath("calledDateTime").type(JsonFieldType.STRING)
                    .description("호출 시간").optional(),
                fieldWithPath("lastCalledDateTime").type(JsonFieldType.STRING)
                    .description("마지막 호출 시간").optional(),
                fieldWithPath("callCount").type(JsonFieldType.NUMBER)
                    .description("호출 횟수"),
                fieldWithPath("isSentReadyToEnterAlarm").type(JsonFieldType.BOOLEAN)
                    .description("입장 준비 알림 전송 여부"),
                fieldWithPath("regDateTime").type(JsonFieldType.STRING)
                    .description("등록일"),
                fieldWithPath("order").type(JsonFieldType.OBJECT)
                    .description("주문 정보").optional(),
                fieldWithPath("order.id").type(JsonFieldType.STRING)
                    .description("주문 short UUID"),
                fieldWithPath("order.orderLineItems[].menuId").type(JsonFieldType.STRING)
                    .description("메뉴 short UUID"),
                fieldWithPath("order.orderLineItems[].name").type(JsonFieldType.STRING)
                    .description("메뉴 이름"),
                fieldWithPath("order.orderLineItems[].quantity").type(JsonFieldType.NUMBER)
                    .description("메뉴 수량")
            )
        ));
  }

  private WaitingVO createWaitingList(String shopId, String waitingId, LocalDate operationDate) {
    PersonOptionsData personOptionsData = createPersonOptionData();

    return WaitingVO.builder()
        .waitingId(waitingId)
        .shopId(shopId)
        .operationDate(operationDate)
        .customerSeq(1)
        .registerChannel(RegisterChannel.WAITING_APP)
        .customerPhoneNumber("010-1234-5678")
        .customerName("아무개")
        .sittingCount(5)
        .waitingNumber(1)
        .waitingOrder(1)
        .waitingStatus(WaitingStatus.WAITING)
        .waitingDetailStatus(WaitingDetailStatus.WAITING)
        .waitingMemo("메모")
        .seatOptionName("홀")
        .totalSeatCount(3)
        .personOptionText(personOptionsData.getPersonOptionText())
        .personOptions(personOptionsData.getPersonOptions())
        .expectedSittingDateTime(ZonedDateTimeUtils.nowOfSeoul())
        .waitingCompleteDateTime(null)
        .isSentReadyToEnterAlarm(true)
        .regDateTime(ZonedDateTimeUtils.nowOfSeoul())
        .order(WaitingVO.Order.builder()
            .id(UUIDUtil.shortUUID())
            .orderLineItems(List.of(
                WaitingVO.OrderLineItem.builder()
                    .menuId(UUIDUtil.shortUUID())
                    .name("치즈돈가스")
                    .quantity(1)
                    .build()
            ))
            .build())
        .build();
  }


  private static PersonOptionsData createPersonOptionData() {
    return PersonOptionsData.builder()
        .personOptions(List.of(PersonOption.builder()
                .name("성인").count(2).build(),
            PersonOption.builder()
                .name("유아").count(1)
                .additionalOptions(List.of(AdditionalOption.builder()
                    .name("유아용 의자")
                    .count(1)
                    .build()))
                .build())).build();
  }
}