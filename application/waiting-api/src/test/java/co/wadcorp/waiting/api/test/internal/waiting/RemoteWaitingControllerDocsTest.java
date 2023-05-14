package co.wadcorp.waiting.api.test.internal.waiting;

import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentRequest;
import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.internal.controller.waiting.RemoteWaitingController;
import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteOrderRequest;
import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteWaitingListRequest;
import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteWaitingRegisterRequest;
import co.wadcorp.waiting.api.internal.service.waiting.RemoteWaitingApiService;
import co.wadcorp.waiting.api.internal.service.waiting.RemoteWaitingCancelApiService;
import co.wadcorp.waiting.api.internal.service.waiting.RemoteWaitingPutOffApiService;
import co.wadcorp.waiting.api.internal.service.waiting.RemoteWaitingRegisterApiService;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteWaitingRegisterServiceRequest.AdditionalOptionVO;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteWaitingRegisterServiceRequest.PersonOptionVO;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.AdditionalOptionDto;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.CreatedOrderDto;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.PersonOptionDto;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteListWaitingResponse;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteOtherWaitingListResponse;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingListOrderMenuDto;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingRegisterResponse;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingResponse;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.TableDto;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.data.domain.waiting.RegisterChannel;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

class RemoteWaitingControllerDocsTest extends RestDocsSupport {

  private final RemoteWaitingApiService remoteWaitingApiService = mock(RemoteWaitingApiService.class);
  private final RemoteWaitingRegisterApiService remoteWaitingRegisterApiService = mock(RemoteWaitingRegisterApiService.class);
  private final RemoteWaitingCancelApiService remoteWaitingCancelApiService = mock(RemoteWaitingCancelApiService.class);
  private final RemoteWaitingPutOffApiService remoteWaitingPutOffApiService = mock(RemoteWaitingPutOffApiService.class);

  @Override
  public Object init() {
    return new RemoteWaitingController(remoteWaitingApiService, remoteWaitingRegisterApiService, remoteWaitingCancelApiService, remoteWaitingPutOffApiService);
  }

  @DisplayName("원격 웨이팅 등록")
  @Test
  void registerWaitingTest() throws Exception {
    // given
    RemoteWaitingRegisterRequest request = RemoteWaitingRegisterRequest.builder()
        .tableId("shortUUID")
        .totalPersonCount(2)
        .personOptions(List.of(PersonOptionVO.builder()
            .id("personOptionId")
            .count(1)
            .additionalOptions(List.of(AdditionalOptionVO.builder()
                .id("additionalOptionId")
                .count(1)
                .build()
            ))
            .build()
        ))
        .phoneNumber("010-0000-0000")
        .tableId("tableId")
        .order(RemoteOrderRequest.builder()
            .totalPrice(BigDecimal.valueOf(14000))
            .orderLineItems(List.of(
                RemoteOrderRequest.OrderLineItem.builder()
                    .menuId(UUIDUtil.shortUUID())
                    .name("돈까스")
                    .quantity(1)
                    .unitPrice(BigDecimal.valueOf(10000))
                    .linePrice(BigDecimal.valueOf(10000))
                    .build(),
                RemoteOrderRequest.OrderLineItem.builder()
                    .menuId(UUIDUtil.shortUUID())
                    .name("콜라")
                    .quantity(2)
                    .unitPrice(BigDecimal.valueOf(2000))
                    .linePrice(BigDecimal.valueOf(4000))
                    .build()
            ))
            .build())
        .build();

    RemoteWaitingRegisterResponse response = RemoteWaitingRegisterResponse.builder()
        .id("shortUUID")
        .shopId(1L)
        .shopName("매장명")
        .registerChannel(RegisterChannel.CATCH_APP)
        .operationDate(LocalDate.of(2023, 2, 28))
        .customerPhoneNumber("010-0000-0000")
        .waitingNumber(806)
        .waitingOrder(3)
        .waitingRegisteredOrder(6)
        .waitingStatus(WaitingStatus.WAITING)
        .waitingDetailStatus(WaitingDetailStatus.WAITING)
        .totalPersonCount(2)
        .expectedSittingDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 10, 0)))
        .waitingCompleteDateTime(null)
        .personOptions(List.of(PersonOptionDto.builder()
            .name("유아")
            .count(2)
            .additionalOptions(List.of(AdditionalOptionDto.builder()
                .name("유아용의자")
                .count(1)
                .build()
            ))
            .build()
        ))
        .table(TableDto.builder()
            .id("tableId")
            .name("홀")
            .isTakeOut(false)
            .build()
        )
        .order(CreatedOrderDto.builder()
            .id(UUIDUtil.shortUUID())
            .totalPrice(BigDecimal.valueOf(14000))
            .orderLineItems(
                List.of(
                    CreatedOrderDto.OrderLineItem.builder()
                        .menuId(UUIDUtil.shortUUID())
                        .name("돈까스")
                        .quantity(1)
                        .unitPrice(BigDecimal.valueOf(10000))
                        .linePrice(BigDecimal.valueOf(10000))
                        .build(),
                    CreatedOrderDto.OrderLineItem.builder()
                        .menuId(UUIDUtil.shortUUID())
                        .name("콜라")
                        .quantity(2)
                        .unitPrice(BigDecimal.valueOf(2000))
                        .linePrice(BigDecimal.valueOf(4000))
                        .build()
                )
            )
            .build()
        )
        .regDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 9, 30)))
        .build();

    // when
    when(remoteWaitingRegisterApiService.register(any(ChannelShopIdMapping.class), any(LocalDate.class), any(ZonedDateTime.class), any()))
        .thenReturn(response);

    // then
    mockMvc.perform(post("/internal/api/v1/shops/{shopIds}/waiting", "SHOP_ID")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andDo(document("remote-waiting-register",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopIds").description("B2C의 shopSeq")
            ),
            requestFields(
                fieldWithPath("tableId").type(JsonFieldType.STRING)
                    .description("테이블 ID"),
                fieldWithPath("totalPersonCount").type(JsonFieldType.NUMBER)
                    .description("구성원 총 인원"),
                fieldWithPath("personOptions[]").type(JsonFieldType.ARRAY)
                    .optional()
                    .description("구성원 정보"),
                fieldWithPath("personOptions[].id").type(JsonFieldType.STRING)
                    .optional()
                    .description("구성원 ID"),
                fieldWithPath("personOptions[].count").type(JsonFieldType.NUMBER)
                    .optional()
                    .description("구성원 인원 수"),
                fieldWithPath("personOptions[].additionalOptions[]").type(JsonFieldType.ARRAY)
                    .optional()
                    .description("구성원 정보"),
                fieldWithPath("personOptions[].additionalOptions[].id").type(JsonFieldType.STRING)
                    .optional()
                    .description("구성원 정보"),
                fieldWithPath("personOptions[].additionalOptions[].count")
                    .optional()
                    .type(JsonFieldType.NUMBER)
                    .description("구성원 정보"),
                fieldWithPath("phoneNumber").type(JsonFieldType.STRING)
                    .description("연락처 (010-0000-0000)"),
                fieldWithPath("extra").type(JsonFieldType.OBJECT)
                    .optional()
                    .description("요청자 추가 값"),
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
                fieldWithPath("id").type(JsonFieldType.STRING)
                    .description("웨이팅 ID"),
                fieldWithPath("shopId").type(JsonFieldType.NUMBER)
                    .description("B2C 매장 Seq"),
                fieldWithPath("shopName").type(JsonFieldType.STRING)
                    .description("매장 이름"),
                fieldWithPath("registerChannel").type(JsonFieldType.STRING)
                    .description("등록 채널"),
                fieldWithPath("operationDate").type(JsonFieldType.STRING)
                    .description("운영 날짜"),
                fieldWithPath("customerPhoneNumber").type(JsonFieldType.STRING)
                    .description("고객 연락처 (010-0000-0000)"),
                fieldWithPath("waitingNumber").type(JsonFieldType.NUMBER)
                    .description("웨이팅 채번 (고객이 발급받는 번호)"),
                fieldWithPath("waitingOrder").type(JsonFieldType.NUMBER)
                    .description("웨이팅 순서 (대기열 순서)"),
                fieldWithPath("waitingRegisteredOrder").type(JsonFieldType.NUMBER)
                    .description("웨이팅 등록 순서"),
                fieldWithPath("waitingStatus").type(JsonFieldType.STRING)
                    .description("웨이팅 상태"),
                fieldWithPath("waitingDetailStatus").type(JsonFieldType.STRING)
                    .description("웨이팅 상세 상태"),
                fieldWithPath("totalPersonCount").type(JsonFieldType.NUMBER)
                    .description("총 인원 수"),
                fieldWithPath("expectedSittingDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("착석 예상시간"),
                fieldWithPath("waitingCompleteDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("[착석, 취소] 완료 시간"),
                fieldWithPath("personOptions[]").type(JsonFieldType.ARRAY)
                    .description("인원 옵션 설정 정보 (데이터가 없다면 빈 배열로 전달)"),
                fieldWithPath("personOptions[].name").type(JsonFieldType.STRING)
                    .description("인원 옵션 - 이름"),
                fieldWithPath("personOptions[].count").type(JsonFieldType.NUMBER)
                    .description("인원 옵션 - 수"),
                fieldWithPath("personOptions[].additionalOptions[]").type(JsonFieldType.ARRAY)
                    .description("추가 인원 옵션 (데이터가 없다면 빈 배열로 전달)"),
                fieldWithPath("personOptions[].additionalOptions[].name").type(JsonFieldType.STRING)
                    .description("추가 인원 옵션 - 이름"),
                fieldWithPath("personOptions[].additionalOptions[].count")
                    .type(JsonFieldType.NUMBER)
                    .description("추가 인원 옵션 - 수"),
                fieldWithPath("table").type(JsonFieldType.OBJECT)
                    .description("테이블"),
                fieldWithPath("table.id").type(JsonFieldType.STRING)
                    .description("테이블 ID"),
                fieldWithPath("table.name").type(JsonFieldType.STRING)
                    .description("테이블 이름"),
                fieldWithPath("table.isTakeOut").type(JsonFieldType.BOOLEAN)
                    .description("테이블 포장 여부"),
                fieldWithPath("regDateTime").type(JsonFieldType.STRING)
                    .description("등록 시간"),
                fieldWithPath("order").type(JsonFieldType.OBJECT)
                    .description("선 주문 내역"),
                fieldWithPath("order.id").type(JsonFieldType.STRING)
                    .description("선 주문 ID"),
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
            )
        ));
  }

  @DisplayName("원격 웨이팅 취소")
  @Test
  void cancelWaitingTest() throws Exception {
    // given
    RemoteWaitingResponse response = RemoteWaitingResponse.builder()
        .id("shortUUID")
        .shopId(1L)
        .shopName("매장명")
        .registerChannel(RegisterChannel.CATCH_APP)
        .operationDate(LocalDate.of(2023, 2, 28))
        .customerPhoneNumber("010-0000-0000")
        .waitingNumber(806)
        .waitingOrder(3)
        .waitingRegisteredOrder(6)
        .waitingStatus(WaitingStatus.CANCEL)
        .waitingDetailStatus(WaitingDetailStatus.CANCEL_BY_SHOP)
        .totalPersonCount(2)
        .expectedSittingDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 10, 0)))
        .waitingCompleteDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 9, 55)))
        .personOptions(List.of(PersonOptionDto.builder()
            .name("유아")
            .count(2)
            .additionalOptions(List.of(AdditionalOptionDto.builder()
                .name("유아용의자")
                .count(1)
                .build()
            ))
            .build()
        ))
        .table(TableDto.builder()
            .id("tableId")
            .name("홀")
            .isTakeOut(false)
            .build()
        )
        .regDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 9, 30)))
        .build();

    // when
    when(remoteWaitingCancelApiService.cancel(any(ChannelShopIdMapping.class), any(String.class), any(LocalDate.class), any(ZonedDateTime.class)))
        .thenReturn(response);

    // then
    mockMvc.perform(
            post("/internal/api/v1/shops/{shopIds}/waiting/{waitingId}/cancel", "SHOP_ID", "WAITING_ID")
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andDo(document("remote-waiting-cancel",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopIds").description("B2C의 shopSeq"),
                parameterWithName("waitingId").description("웨이팅 ID")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("id").type(JsonFieldType.STRING)
                    .description("웨이팅 ID"),
                fieldWithPath("shopId").type(JsonFieldType.NUMBER)
                    .description("B2C 매장 Seq"),
                fieldWithPath("shopName").type(JsonFieldType.STRING)
                    .description("매장 이름"),
                fieldWithPath("registerChannel").type(JsonFieldType.STRING)
                    .description("등록 채널"),
                fieldWithPath("operationDate").type(JsonFieldType.STRING)
                    .description("운영 날짜"),
                fieldWithPath("customerPhoneNumber").type(JsonFieldType.STRING)
                    .description("고객 연락처 (010-0000-0000)"),
                fieldWithPath("waitingNumber").type(JsonFieldType.NUMBER)
                    .description("웨이팅 채번 (고객이 발급받는 번호)"),
                fieldWithPath("waitingOrder").type(JsonFieldType.NUMBER)
                    .description("웨이팅 순서 (대기열 순서)"),
                fieldWithPath("waitingRegisteredOrder").type(JsonFieldType.NUMBER)
                    .description("웨이팅 등록 순서"),
                fieldWithPath("waitingStatus").type(JsonFieldType.STRING)
                    .description("웨이팅 상태"),
                fieldWithPath("waitingDetailStatus").type(JsonFieldType.STRING)
                    .description("웨이팅 상세 상태"),
                fieldWithPath("totalPersonCount").type(JsonFieldType.NUMBER)
                    .description("총 인원 수"),
                fieldWithPath("expectedSittingDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("착석 예상시간"),
                fieldWithPath("waitingCompleteDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("[착석, 취소] 완료 시간"),
                fieldWithPath("personOptions[]").type(JsonFieldType.ARRAY)
                    .description("인원 옵션 설정 정보 (데이터가 없다면 빈 배열로 전달)"),
                fieldWithPath("personOptions[].name").type(JsonFieldType.STRING)
                    .description("인원 옵션 - 이름"),
                fieldWithPath("personOptions[].count").type(JsonFieldType.NUMBER)
                    .description("인원 옵션 - 수"),
                fieldWithPath("personOptions[].additionalOptions[]").type(JsonFieldType.ARRAY)
                    .description("추가 인원 옵션 (데이터가 없다면 빈 배열로 전달)"),
                fieldWithPath("personOptions[].additionalOptions[].name").type(JsonFieldType.STRING)
                    .description("추가 인원 옵션 - 이름"),
                fieldWithPath("personOptions[].additionalOptions[].count")
                    .type(JsonFieldType.NUMBER)
                    .description("추가 인원 옵션 - 수"),
                fieldWithPath("table").type(JsonFieldType.OBJECT)
                    .description("테이블"),
                fieldWithPath("table.id").type(JsonFieldType.STRING)
                    .description("테이블 ID"),
                fieldWithPath("table.name").type(JsonFieldType.STRING)
                    .description("테이블 이름"),
                fieldWithPath("table.isTakeOut").type(JsonFieldType.BOOLEAN)
                    .description("테이블 포장 여부"),
                fieldWithPath("regDateTime").type(JsonFieldType.STRING)
                    .description("등록 시간")
            )
        ));
  }

  @DisplayName("원격 웨이팅 미루기")
  @Test
  void putOffWaitingTest() throws Exception {
    // given
    RemoteWaitingResponse response = RemoteWaitingResponse.builder()
        .id("shortUUID")
        .shopId(1L)
        .shopName("매장명")
        .registerChannel(RegisterChannel.CATCH_APP)
        .operationDate(LocalDate.of(2023, 2, 28))
        .customerPhoneNumber("010-0000-0000")
        .waitingNumber(806)
        .waitingOrder(3)
        .waitingRegisteredOrder(6)
        .waitingStatus(WaitingStatus.WAITING)
        .waitingDetailStatus(WaitingDetailStatus.PUT_OFF)
        .totalPersonCount(2)
        .expectedSittingDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 10, 0)))
        .waitingCompleteDateTime(null)
        .personOptions(List.of(PersonOptionDto.builder()
            .name("유아")
            .count(2)
            .additionalOptions(List.of(AdditionalOptionDto.builder()
                .name("유아용의자")
                .count(1)
                .build()
            ))
            .build()
        ))
        .table(TableDto.builder()
            .id("tableId")
            .name("홀")
            .isTakeOut(false)
            .build()
        )
        .regDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 9, 30)))
        .build();

    // when
    when(remoteWaitingPutOffApiService.putOff(any(ChannelShopIdMapping.class), any(String.class), any(LocalDate.class), any(ZonedDateTime.class)))
        .thenReturn(response);

    // then
    mockMvc.perform(
            post("/internal/api/v1/shops/{shopIds}/waiting/{waitingId}/put-off", "SHOP_ID",
                "WAITING_ID")
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andDo(document("remote-waiting-put-off",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopIds").description("B2C의 shopSeq"),
                parameterWithName("waitingId").description("웨이팅 ID")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("id").type(JsonFieldType.STRING)
                    .description("웨이팅 ID"),
                fieldWithPath("shopId").type(JsonFieldType.NUMBER)
                    .description("B2C 매장 Seq"),
                fieldWithPath("shopName").type(JsonFieldType.STRING)
                    .description("매장 이름"),
                fieldWithPath("registerChannel").type(JsonFieldType.STRING)
                    .description("등록 채널"),
                fieldWithPath("operationDate").type(JsonFieldType.STRING)
                    .description("운영 날짜"),
                fieldWithPath("customerPhoneNumber").type(JsonFieldType.STRING)
                    .description("고객 연락처 (010-0000-0000)"),
                fieldWithPath("waitingNumber").type(JsonFieldType.NUMBER)
                    .description("웨이팅 채번 (고객이 발급받는 번호)"),
                fieldWithPath("waitingOrder").type(JsonFieldType.NUMBER)
                    .description("웨이팅 순서 (대기열 순서)"),
                fieldWithPath("waitingRegisteredOrder").type(JsonFieldType.NUMBER)
                    .description("웨이팅 등록 순서"),
                fieldWithPath("waitingStatus").type(JsonFieldType.STRING)
                    .description("웨이팅 상태"),
                fieldWithPath("waitingDetailStatus").type(JsonFieldType.STRING)
                    .description("웨이팅 상세 상태"),
                fieldWithPath("totalPersonCount").type(JsonFieldType.NUMBER)
                    .description("총 인원 수"),
                fieldWithPath("expectedSittingDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("착석 예상시간"),
                fieldWithPath("waitingCompleteDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("[착석, 취소] 완료 시간"),
                fieldWithPath("personOptions[]").type(JsonFieldType.ARRAY)
                    .description("인원 옵션 설정 정보 (데이터가 없다면 빈 배열로 전달)"),
                fieldWithPath("personOptions[].name").type(JsonFieldType.STRING)
                    .description("인원 옵션 - 이름"),
                fieldWithPath("personOptions[].count").type(JsonFieldType.NUMBER)
                    .description("인원 옵션 - 수"),
                fieldWithPath("personOptions[].additionalOptions[]").type(JsonFieldType.ARRAY)
                    .description("추가 인원 옵션 (데이터가 없다면 빈 배열로 전달)"),
                fieldWithPath("personOptions[].additionalOptions[].name").type(JsonFieldType.STRING)
                    .description("추가 인원 옵션 - 이름"),
                fieldWithPath("personOptions[].additionalOptions[].count")
                    .type(JsonFieldType.NUMBER)
                    .description("추가 인원 옵션 - 수"),
                fieldWithPath("table").type(JsonFieldType.OBJECT)
                    .description("테이블"),
                fieldWithPath("table.id").type(JsonFieldType.STRING)
                    .description("테이블 ID"),
                fieldWithPath("table.name").type(JsonFieldType.STRING)
                    .description("테이블 이름"),
                fieldWithPath("table.isTakeOut").type(JsonFieldType.BOOLEAN)
                    .description("테이블 포장 여부"),
                fieldWithPath("regDateTime").type(JsonFieldType.STRING)
                    .description("등록 시간")
            )
        ));
  }

  @DisplayName("원격 웨이팅 목록")
  @Test
  void findWaitingsTest() throws Exception {
    // given
    RemoteWaitingListRequest request = RemoteWaitingListRequest.builder()
        .waitingIds(List.of(
            "shortUUID"
        ))
        .operationDate("2023-02-28")
        .build();

    List<RemoteListWaitingResponse> response = List.of(RemoteListWaitingResponse.builder()
        .id("shortUUID")
        .shopName("매장명")
        .registerChannel(RegisterChannel.CATCH_APP)
        .operationDate(LocalDate.of(2023, 2, 28))
        .customerPhoneNumber("010-0000-0000")
        .waitingNumber(806)
        .waitingOrder(3)
        .waitingRegisteredOrder(6)
        .waitingStatus(WaitingStatus.WAITING)
        .waitingDetailStatus(WaitingDetailStatus.WAITING)
        .totalPersonCount(2)
        .expectedSittingDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 10, 0)))
        .waitingCompleteDateTime(null)
        .personOptions(List.of(PersonOptionDto.builder()
            .name("유아")
            .count(2)
            .additionalOptions(List.of(AdditionalOptionDto.builder()
                .name("유아용의자")
                .count(1)
                .build()
            ))
            .build()
        ))
        .table(TableDto.builder()
            .id("tableId")
            .name("홀")
            .isTakeOut(false)
            .build()
        )
        .order(RemoteWaitingListOrderMenuDto.builder()
            .id(UUIDUtil.shortUUID())
            .totalPrice(BigDecimal.valueOf(14000))
            .orderLineItems(
                List.of(
                    RemoteWaitingListOrderMenuDto.OrderLineItem.builder()
                        .menuId(UUIDUtil.shortUUID())
                        .name("돈까스")
                        .quantity(1)
                        .unitPrice(BigDecimal.valueOf(10000))
                        .linePrice(BigDecimal.valueOf(10000))
                        .build(),
                    RemoteWaitingListOrderMenuDto.OrderLineItem.builder()
                        .menuId(UUIDUtil.shortUUID())
                        .name("콜라")
                        .quantity(2)
                        .unitPrice(BigDecimal.valueOf(2000))
                        .linePrice(BigDecimal.valueOf(4000))
                        .build()
                )
            )
            .build()
        )
        .regDateTime(ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 28, 9, 30)))
        .build()
    );

    // when
    when(remoteWaitingApiService.findWaitings(any(), any(ZonedDateTime.class)))
        .thenReturn(response);

    // then
    mockMvc.perform(post("/internal/api/v1/waiting/list")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andDo(document("remote-waiting-list",
            getDocumentRequest(),
            getDocumentResponse(),
            requestFields(
                fieldWithPath("waitingIds").type(JsonFieldType.ARRAY)
                    .description("웨이팅 ID 목록"),
                fieldWithPath("operationDate").type(JsonFieldType.STRING)
                    .optional()
                    .description("운영일자(없다면 현재 운영일자)")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("id").type(JsonFieldType.STRING)
                    .description("웨이팅 ID"),
                fieldWithPath("shopName").type(JsonFieldType.STRING)
                    .description("매장 이름"),
                fieldWithPath("registerChannel").type(JsonFieldType.STRING)
                    .description("등록 채널"),
                fieldWithPath("operationDate").type(JsonFieldType.STRING)
                    .description("운영 날짜"),
                fieldWithPath("customerPhoneNumber").type(JsonFieldType.STRING)
                    .description("고객 연락처 (010-0000-0000)"),
                fieldWithPath("waitingNumber").type(JsonFieldType.NUMBER)
                    .description("웨이팅 채번 (고객이 발급받는 번호)"),
                fieldWithPath("waitingOrder").type(JsonFieldType.NUMBER)
                    .description("웨이팅 순서 (대기열 순서)"),
                fieldWithPath("waitingRegisteredOrder").type(JsonFieldType.NUMBER)
                    .description("웨이팅 등록 순서"),
                fieldWithPath("waitingStatus").type(JsonFieldType.STRING)
                    .description("웨이팅 상태"),
                fieldWithPath("waitingDetailStatus").type(JsonFieldType.STRING)
                    .description("웨이팅 상세 상태"),
                fieldWithPath("totalPersonCount").type(JsonFieldType.NUMBER)
                    .description("총 인원 수"),
                fieldWithPath("expectedSittingDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("착석 예상시간"),
                fieldWithPath("waitingCompleteDateTime").type(JsonFieldType.STRING)
                    .optional()
                    .description("[착석, 취소] 완료 시간"),
                fieldWithPath("personOptions[]").type(JsonFieldType.ARRAY)
                    .description("인원 옵션 설정 정보 (데이터가 없다면 빈 배열로 전달)"),
                fieldWithPath("personOptions[].name").type(JsonFieldType.STRING)
                    .description("인원 옵션 - 이름"),
                fieldWithPath("personOptions[].count").type(JsonFieldType.NUMBER)
                    .description("인원 옵션 - 수"),
                fieldWithPath("personOptions[].additionalOptions[]").type(JsonFieldType.ARRAY)
                    .description("추가 인원 옵션 (데이터가 없다면 빈 배열로 전달)"),
                fieldWithPath("personOptions[].additionalOptions[].name").type(JsonFieldType.STRING)
                    .description("추가 인원 옵션 - 이름"),
                fieldWithPath("personOptions[].additionalOptions[].count")
                    .type(JsonFieldType.NUMBER)
                    .description("추가 인원 옵션 - 수"),
                fieldWithPath("table").type(JsonFieldType.OBJECT)
                    .description("테이블"),
                fieldWithPath("table.id").type(JsonFieldType.STRING)
                    .description("테이블 ID"),
                fieldWithPath("table.name").type(JsonFieldType.STRING)
                    .description("테이블 이름"),
                fieldWithPath("table.isTakeOut").type(JsonFieldType.BOOLEAN)
                    .description("테이블 포장 여부"),
                fieldWithPath("regDateTime").type(JsonFieldType.STRING)
                    .description("등록 시간"),
                fieldWithPath("order").type(JsonFieldType.OBJECT)
                    .description("선 주문 내역"),
                fieldWithPath("order.id").type(JsonFieldType.STRING)
                    .description("선 주문 ID"),
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
            )
        ));
  }

  @DisplayName("원격 웨이팅 등록 전 중복/3회초과 여부 검증")
  @Test
  void checkValidationBeforeRegisterWaitingTest() throws Exception {
    mockMvc.perform(get("/internal/api/v1/shops/{shopIds}/register/waiting/validation", "SHOP_ID")
            .queryParam("customerPhone", "010-0000-0000")
            .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
        )
        .andExpect(status().isOk())
        .andDo(document("remote-waiting-check-validation",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopIds").description("B2C의 shopSeq")
            ),
            queryParameters(
                parameterWithName("customerPhone")
                    .description("고객 전화번호")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                    .description("성공 여부")
            )
        ));
  }

  @DisplayName("원격 웨이팅 타매장 웨이팅 목록 조회")
  @Test
  void findAllWaitingsOfOtherShops() throws Exception {
    // given
    List<RemoteOtherWaitingListResponse> response = List.of(RemoteOtherWaitingListResponse.builder()
        .shopId(1L)
        .waitingId(UUIDUtil.shortUUID())
        .shopName("매장명")
        .waitingOrder(3)
        .expectedWaitingPeriod(5)
        .regDateTime(ZonedDateTime.now().minusMinutes(30))
        .isTakeOut(true)
        .build()
    );

    // when
    when(remoteWaitingApiService.findAllWaitingsOfOtherShops(any(ChannelShopIdMapping.class), any(LocalDate.class), any()))
        .thenReturn(response);

    // then
    mockMvc.perform(get("/internal/api/v1/shops/{shopIds}/register/waiting/others", "SHOP_ID")
            .queryParam("customerPhone", "010-0000-0000")
            .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
        )
        .andExpect(status().isOk())
        .andDo(document("remote-waiting-other-shops",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopIds").description("B2C의 shopSeq")
            ),
            queryParameters(
                parameterWithName("customerPhone")
                    .description("고객 전화번호")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("shopId").type(JsonFieldType.NUMBER)
                    .description("매장 ID"),
                fieldWithPath("waitingId").type(JsonFieldType.STRING)
                    .description("웨이팅 ID"),
                fieldWithPath("shopName").type(JsonFieldType.STRING)
                    .description("매장명"),
                fieldWithPath("waitingOrder").type(JsonFieldType.NUMBER)
                    .description("웨이팅 순서 (대기열 순서)"),
                fieldWithPath("expectedWaitingPeriod").type(JsonFieldType.NUMBER)
                    .description("예상 대기시간").optional(),
                fieldWithPath("maxExpressionWaitingPeriod").type(JsonFieldType.NUMBER)
                    .description("최대 표현 가능한 웨이팅 시간"),
                fieldWithPath("isTakeOut").type(JsonFieldType.BOOLEAN)
                    .description("포장 여부"),
                fieldWithPath("regDateTime").type(JsonFieldType.STRING)
                    .description("등록 시간")
            )
        ));
  }

}
