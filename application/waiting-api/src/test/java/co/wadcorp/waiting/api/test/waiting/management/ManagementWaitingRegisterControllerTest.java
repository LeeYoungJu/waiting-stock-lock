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
import co.wadcorp.waiting.api.controller.waiting.management.ManagementWaitingRegisterController;
import co.wadcorp.waiting.api.controller.waiting.management.dto.request.OrderDto;
import co.wadcorp.waiting.api.controller.waiting.management.dto.request.OrderDto.OrderLineItemDto;
import co.wadcorp.waiting.api.controller.waiting.management.dto.request.WaitingRegisterByManagerRequest;
import co.wadcorp.waiting.api.model.waiting.response.WaitingRegisterByManagerResponse;
import co.wadcorp.waiting.api.model.waiting.vo.PersonOptionVO;
import co.wadcorp.waiting.api.model.waiting.vo.PersonOptionVO.AdditionalOption;
import co.wadcorp.waiting.api.model.waiting.vo.SeatOptionVO;
import co.wadcorp.waiting.api.model.waiting.vo.TermsCustomerVO;
import co.wadcorp.waiting.api.service.waiting.management.ManagementWaitingRegisterApiService;
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

public class ManagementWaitingRegisterControllerTest extends RestDocsSupport {

  private final ManagementWaitingRegisterApiService managementWaitingRegisterApiService = mock(
      ManagementWaitingRegisterApiService.class);

  @Override
  public Object init() {
    return new ManagementWaitingRegisterController(managementWaitingRegisterApiService);
  }

  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final String SHOP_ID = "SHOP_UUID";
  private static final String PHONE = "010-1234-5678";
  private static final String NAME = "아무개";

  @Test
  @DisplayName("웨이팅_수기_등록")
  public void registerWaitingTest() throws Exception {
    // given
    List<PersonOptionVO> personOptions = List.of(
        PersonOptionVO.builder().id(UUIDUtil.shortUUID()).name("성인").count(3)
            .additionalOptions(List.of()).build(),
        PersonOptionVO.builder().id(UUIDUtil.shortUUID()).name("유아").count(2)
            .additionalOptions(List.of(AdditionalOption.builder()
                .id(UUIDUtil.shortUUID())
                .name("유아용 의자").count(1).build()))
            .build());

    List<TermsCustomerVO> termsCustomer = List.of(
        TermsCustomerVO.builder().seq(1).isAgree(true).build(),
        TermsCustomerVO.builder().seq(2).isAgree(true).build());

    WaitingRegisterByManagerRequest request = WaitingRegisterByManagerRequest.builder()
        .phoneNumber(PHONE)
        .name(NAME)
        .totalPersonCount(5)
        .personOptions(personOptions)
        .seatOption(
            SeatOptionVO.builder().id(UUIDUtil.shortUUID()).name("착석").build())
        .termsCustomer(termsCustomer)
        .order(
            OrderDto.builder()
                .totalPrice(BigDecimal.valueOf(12000))
                .orderLineItems(List.of(
                    OrderLineItemDto.builder()
                        .menuId(UUIDUtil.shortUUID())
                        .name("치즈돈가스")
                        .quantity(1)
                        .unitPrice(BigDecimal.valueOf(12000))
                        .linePrice(BigDecimal.valueOf(12000))
                        .build()
                ))
                .build())
        .build();

    WaitingRegisterByManagerResponse response = WaitingRegisterByManagerResponse.builder()
        .waitingId(UUIDUtil.shortUUID())
        .waitingNumber(101)
        .build();

    // when
    when(managementWaitingRegisterApiService.registerByManager(any(), any(LocalDate.class), any(),
        any())).thenReturn(response);

    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/management/waiting", SHOP_ID)
                .header("X-REQUEST-ID", "deviceId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("management-waiting-register",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            requestFields(
                fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("고객 연락처")
                    .optional(),
                fieldWithPath("name").type(JsonFieldType.STRING).description("고객 이름").optional(),
                fieldWithPath("totalPersonCount").type(JsonFieldType.NUMBER)
                    .description("총 입장 인원(착석 + 비착석)"),
                fieldWithPath("personOptions[]").type(JsonFieldType.ARRAY).description("인원 옵션 정보"),
                fieldWithPath("personOptions[].id").type(JsonFieldType.STRING)
                    .description("인원 옵션 정보 - 아이디"),
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
                    .description("선주문 메뉴 목록").optional(),
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
}
