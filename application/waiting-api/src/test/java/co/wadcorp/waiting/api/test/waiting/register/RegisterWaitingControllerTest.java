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
import co.wadcorp.waiting.api.controller.waiting.register.RegisterWaitingController;
import co.wadcorp.waiting.api.controller.waiting.register.dto.request.OrderDto;
import co.wadcorp.waiting.api.controller.waiting.register.dto.request.OrderDto.OrderLineItemDto;
import co.wadcorp.waiting.api.controller.waiting.register.dto.request.WaitingRegisterRequest;
import co.wadcorp.waiting.api.model.waiting.response.MyWaitingInfoResponse;
import co.wadcorp.waiting.api.model.waiting.response.WaitingRegisterResponse;
import co.wadcorp.waiting.api.model.waiting.vo.PersonOptionVO;
import co.wadcorp.waiting.api.model.waiting.vo.PersonOptionVO.AdditionalOption;
import co.wadcorp.waiting.api.model.waiting.vo.SeatOptionVO;
import co.wadcorp.waiting.api.model.waiting.vo.TermsCustomerVO;
import co.wadcorp.waiting.api.service.waiting.WaitingRegisterApiService;
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

public class RegisterWaitingControllerTest extends RestDocsSupport {

  private final WaitingRegisterApiService waitingRegisterApiService = mock(
      WaitingRegisterApiService.class);

  @Override
  public Object init() {
    return new RegisterWaitingController(waitingRegisterApiService);
  }

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final String SHOP_ID = "SHOP_UUID";
  private final String PHONE = "010-1234-5678";

  @Test
  @DisplayName("웨이팅_등록_전_전화번호로_검증")
  public void validWaitingBeforeRegisterTest() throws Exception {

    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/register/waiting/validation",
                    SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("customerPhone", PHONE))
        .andExpect(status().isOk())
        .andDo(document("waiting-register-validation",
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            queryParameters(
                parameterWithName("customerPhone").description("고객 전화번호")
            )
        ));
  }

  @Test
  @DisplayName("웨이팅_등록")
  public void registerWaitingTest() throws Exception {
    // given
    List<PersonOptionVO> personOptions = List.of(
        PersonOptionVO.builder().id(UUIDUtil.shortUUID()).name("성인").count(3)
            .additionalOptions(List.of()).build(),
        PersonOptionVO.builder().id(UUIDUtil.shortUUID()).name("유아").count(2)
            .additionalOptions(List.of(
                AdditionalOption.builder()
                    .id(UUIDUtil.shortUUID()).name("유아용 의자").count(1).build())).build());

    List<TermsCustomerVO> termsCustomer = List.of(
        TermsCustomerVO.builder().seq(1).isAgree(true).build(),
        TermsCustomerVO.builder().seq(2).isAgree(true).build());

    WaitingRegisterRequest request = WaitingRegisterRequest.builder()
        .customerPhone("01012345678")
        .totalPersonCount(5)
        .personOptions(personOptions)
        .seatOption(SeatOptionVO.builder().id(UUIDUtil.shortUUID())
            .name("포장").build())
        .termsCustomer(termsCustomer)
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

    WaitingRegisterResponse response = WaitingRegisterResponse.builder()
        .waitingId(UUIDUtil.shortUUID())
        .waitingNumber(101)
        .build();

    // when
    when(waitingRegisterApiService.registerWaiting(any(), any(LocalDate.class), any(), any())).thenReturn(response);

    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/register/waiting", SHOP_ID)
                .header("X-REQUEST-ID", "deviceId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("waiting-register",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            requestFields(
                fieldWithPath("customerPhone").type(JsonFieldType.STRING).description("고객 전화번호"),
                fieldWithPath("totalPersonCount").type(JsonFieldType.NUMBER)
                    .description("총 입장 인원(착석 + 비착석)"),
                fieldWithPath("personOptions[]").type(JsonFieldType.ARRAY).description("인원 옵션 정보"),
                fieldWithPath("personOptions[].id").type(JsonFieldType.STRING)
                    .description("인원 옵션 아이디"),
                fieldWithPath("personOptions[].name").type(JsonFieldType.STRING)
                    .description("인원 옵션 정보 - 옵션명"),
                fieldWithPath("personOptions[].count").type(JsonFieldType.NUMBER)
                    .description("인원 옵션 정보 - 인원수"),
                fieldWithPath("personOptions[].additionalOptions[]").type(JsonFieldType.ARRAY)
                    .description("인원 옵션 정보 - 부가옵션(ex. 유아용 의자)"),
                fieldWithPath("personOptions[].additionalOptions[].id").type(JsonFieldType.STRING)
                    .description("부가 옵션 아이디").optional(),
                fieldWithPath("personOptions[].additionalOptions[].name").type(JsonFieldType.STRING)
                    .description("부가 옵션 명").optional(),
                fieldWithPath("personOptions[].additionalOptions[].count").type(
                    JsonFieldType.NUMBER).description("부가 옵션 수").optional(),
                fieldWithPath("seatOption").type(JsonFieldType.OBJECT).description("좌석 옵션"),
                fieldWithPath("seatOption.id").type(JsonFieldType.STRING).description("좌석 옵션 아이디"),
                fieldWithPath("seatOption.name").type(JsonFieldType.STRING).description("좌석 옵션명"),
                fieldWithPath("termsCustomer[]").type(JsonFieldType.ARRAY)
                    .description("웨이팅 약관동의 리스트"),
                fieldWithPath("termsCustomer[].seq").type(JsonFieldType.NUMBER)
                    .description("웨이팅 약관 시퀀스"),
                fieldWithPath("termsCustomer[].isAgree").type(JsonFieldType.BOOLEAN)
                    .description("웨이팅 약관 동의여부"),
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
                    .description("메뉴 수량")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("waitingId").type(JsonFieldType.STRING).description("웨이팅 아이디"),
                fieldWithPath("waitingNumber").type(JsonFieldType.NUMBER).description("웨이팅 번호(채번)")
            )));
  }

  @Test
  @DisplayName("내_웨이팅 상세 조회")
  public void getCustomerWaitingInfoTest() throws Exception {
    // given
    MyWaitingInfoResponse response = MyWaitingInfoResponse.builder()
        .waitingNumber(202)
        .totalPersonCount(3)
        .lastPhoneNumber("1234")
        .regDateTime("2023-01-17T16:24:35.781257+09:00")
        .build();

    // when
    when(waitingRegisterApiService.getShopCustomerWaitingInfo(any(), any())).thenReturn(response);

    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/register/waiting", SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("customerPhone", PHONE))
        .andExpect(status().isOk())
        .andDo(document("register-my-waiting",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            queryParameters(
                parameterWithName("customerPhone").description("고객 전화번호")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("waitingNumber").type(JsonFieldType.NUMBER).description("웨이팅 채번"),
                fieldWithPath("lastPhoneNumber").type(JsonFieldType.STRING).description("전화번호 뒷자리"),
                fieldWithPath("totalPersonCount").type(JsonFieldType.NUMBER).description("총 입장인원"),
                fieldWithPath("regDateTime").type(JsonFieldType.STRING).description("등록일자")
            )
        ));
  }

}
