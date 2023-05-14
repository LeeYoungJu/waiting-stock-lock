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
import co.wadcorp.waiting.api.controller.waiting.management.ManagementWaitingListController;
import co.wadcorp.waiting.api.model.waiting.response.WaitingInfoResponse;
import co.wadcorp.waiting.api.model.waiting.response.WaitingListResponse;
import co.wadcorp.waiting.api.model.waiting.vo.PageVO;
import co.wadcorp.waiting.api.model.waiting.vo.ShopOperationInfoVO;
import co.wadcorp.waiting.api.model.waiting.vo.WaitingCurrentStatusVO;
import co.wadcorp.waiting.api.model.waiting.vo.WaitingVO;
import co.wadcorp.waiting.api.service.waiting.management.ManagementWaitingInfoApiService;
import co.wadcorp.waiting.api.service.waiting.management.ManagementWaitingListApiService;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.data.domain.shop.operation.status.OperationStatus;
import co.wadcorp.waiting.data.domain.waiting.PersonOption;
import co.wadcorp.waiting.data.domain.waiting.PersonOptionsData;
import co.wadcorp.waiting.data.domain.waiting.RegisterChannel;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingCurrentStatusDto.SeatOption;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

class ManagementWaitingListControllerTest extends RestDocsSupport {

  private final ManagementWaitingInfoApiService managementWaitingInfoApiService = mock(
      ManagementWaitingInfoApiService.class);
  private final ManagementWaitingListApiService managementWaitingListApiService = mock(
      ManagementWaitingListApiService.class);

  @Override
  public Object init() {
    return new ManagementWaitingListController(
        managementWaitingInfoApiService, managementWaitingListApiService
    );
  }

  @Test
  @DisplayName("웨이팅 목록")
  void waitingInfo() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String ctmAuth = "Bearer accessToken";

    LocalDate operationDate = LocalDate.of(2023, 2, 1);

    List<WaitingVO> waitingList = List.of(createWaitingList(SHOP_ID, operationDate));

    ShopOperationInfoVO operationInfo = createShopOperationInfo(operationDate);

    WaitingCurrentStatusVO currentStatus = createCurrentStatus();

    WaitingInfoResponse response = WaitingInfoResponse.builder()
        .page(new PageVO(waitingList.size(), 1, 10))
        .waiting(waitingList)
        .operationInfo(operationInfo)
        .currentStatus(currentStatus)
        .build();

    // when
    when(managementWaitingInfoApiService.getWaitingList(any(), any(LocalDate.class),
        any(ZonedDateTime.class), any())).thenReturn(response);

    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/management/waiting", SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-CTM-AUTH", ctmAuth)
                .queryParam("modeType", "TABLE")
                .queryParam("waitingStatus", "WAITING")
                .queryParam("seatOptionId", "XVxg4bLoTcGO5qtrhqZ57A,tZCcLfzXQyGI6hKo8tVLrw")
                .queryParam("page", "1")
                .queryParam("limit", "100"))
        .andExpect(status().isOk())
        .andDo(document("management-waiting-info",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            queryParameters(
                parameterWithName("modeType").description(
                    "운영 모드: DEFAULT(기본모드), TABLE(테이블모드) - 기본 값: DEFAULT, optional").optional(),
                parameterWithName("seatOptionId").description("좌석 옵션 아이디 (shortUUID) - optional")
                    .optional(),
                parameterWithName("waitingStatus").description(
                        "웨이팅 상태 필터: WAITING(웨이팅 중), SITTING(착석), CANCEL(취소) - 기본값: WAITING, optional")
                    .optional(),
                parameterWithName("page").description("페이지 - 기본값 1, optional").optional(),
                parameterWithName("limit").description("Limit - 기본값 100, optional").optional()
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),

                fieldWithPath("operationInfo").type(JsonFieldType.OBJECT).description("웨이팅 운영 정보"),
                fieldWithPath("operationInfo.operationDate").type(JsonFieldType.STRING)
                    .description("영업일"),
                fieldWithPath("operationInfo.operationStatus").type(JsonFieldType.STRING)
                    .description("운영 상태"),
                fieldWithPath("operationInfo.operationStartDateTime").type(JsonFieldType.STRING)
                    .description("운영 시작 시각").optional(),
                fieldWithPath("operationInfo.operationEndDateTime").type(JsonFieldType.STRING)
                    .description("운영 종료 시각").optional(),
                fieldWithPath("operationInfo.pauseStartDateTime").type(JsonFieldType.STRING)
                    .description("일시 중지 시작 시간").optional(),
                fieldWithPath("operationInfo.pauseEndDateTime").type(JsonFieldType.STRING)
                    .description("일시 중지 종료 시간").optional(),
                fieldWithPath("operationInfo.pauseReasonId").type(JsonFieldType.STRING)
                    .description("일시 중지 안내 아이디").optional(),
                fieldWithPath("operationInfo.pauseReason").type(JsonFieldType.STRING)
                    .description("일시 중지 안내문구").optional(),

                fieldWithPath("currentStatus").type(JsonFieldType.OBJECT).description("웨이팅 현황 정보"),
                fieldWithPath("currentStatus.teamCount").type(JsonFieldType.NUMBER)
                    .description("웨이팅 총 팀수"),
                fieldWithPath("currentStatus.peopleCount").type(JsonFieldType.NUMBER)
                    .description("웨이팅 총 인원수"),
                fieldWithPath("currentStatus.seatsCurrentStatuses").type(JsonFieldType.ARRAY)
                    .description("웨이팅 좌석 별 현황"),
                fieldWithPath("currentStatus.seatsCurrentStatuses[].id").type(JsonFieldType.STRING)
                    .description("좌석 아이디"),
                fieldWithPath("currentStatus.seatsCurrentStatuses[].seatOptionName").type(
                    JsonFieldType.STRING).description("좌석 이름"),
                fieldWithPath("currentStatus.seatsCurrentStatuses[].seatOption").type(
                    JsonFieldType.OBJECT).description("좌석 옵션"),
                fieldWithPath("currentStatus.seatsCurrentStatuses[].seatOption.minSeatCount").type(
                    JsonFieldType.NUMBER).description("좌석 옵션-최소착석인원"),
                fieldWithPath("currentStatus.seatsCurrentStatuses[].seatOption.maxSeatCount").type(
                    JsonFieldType.NUMBER).description("좌석 옵션-최대착석인원"),
                fieldWithPath("currentStatus.seatsCurrentStatuses[].seatOption.isPickup").type(
                    JsonFieldType.BOOLEAN).description("좌석 옵션-포장여부"),
                fieldWithPath("currentStatus.seatsCurrentStatuses[].teamCount").type(
                    JsonFieldType.NUMBER).description("웨이팅 좌석 팀 수"),
                fieldWithPath("currentStatus.seatsCurrentStatuses[].peopleCount").type(
                    JsonFieldType.NUMBER).description("웨이팅 좌석 인원수"),
                fieldWithPath("currentStatus.seatsCurrentStatuses[].expectedWaitingTime").type(
                    JsonFieldType.NUMBER).description("웨이팅 대기 예상 시간"),
                fieldWithPath(
                    "currentStatus.seatsCurrentStatuses[].isUsedExpectedWaitingPeriod").type(
                    JsonFieldType.BOOLEAN).description("웨이팅 예상 대기 시간 사용 여부"),

                fieldWithPath("waiting").type(JsonFieldType.ARRAY).description("웨이팅 목록"),
                fieldWithPath("waiting[].waitingId").type(JsonFieldType.STRING)
                    .description("웨이팅 아이디"),
                fieldWithPath("waiting[].shopId").type(JsonFieldType.STRING).description("매장 아이디"),
                fieldWithPath("waiting[].registerChannel").type(JsonFieldType.STRING)
                    .description("등록 체널: WAITING_APP(현장), WAITING_MANAGER(수기), CATCH_APP(원격)"),
                fieldWithPath("waiting[].registerChannelText").type(JsonFieldType.STRING)
                    .description("등록 체널 텍스트 - 현장, 수기, 원격"),
                fieldWithPath("waiting[].operationDate").type(JsonFieldType.STRING)
                    .description("영업일"),
                fieldWithPath("waiting[].customerSeq").type(JsonFieldType.NUMBER)
                    .description("고객 시퀀스").optional(),
                fieldWithPath("waiting[].customerPhoneNumber").type(JsonFieldType.STRING)
                    .description("고객 연락처").optional(),
                fieldWithPath("waiting[].customerName").type(JsonFieldType.STRING)
                    .description("고객 이름").optional(),
                fieldWithPath("waiting[].sittingCount").type(JsonFieldType.NUMBER)
                    .description("착석 횟수"),
                fieldWithPath("waiting[].waitingNumber").type(JsonFieldType.NUMBER)
                    .description("웨이팅 채번"),
                fieldWithPath("waiting[].waitingOrder").type(JsonFieldType.NUMBER)
                    .description("웨이팅 순번"),
                fieldWithPath("waiting[].waitingStatus").type(JsonFieldType.STRING)
                    .description("웨이팅 상태"),
                fieldWithPath("waiting[].waitingDetailStatus").type(JsonFieldType.STRING)
                    .description("웨이팅 상세 상태"),
                fieldWithPath("waiting[].waitingMemo").type(JsonFieldType.STRING)
                    .description("웨이팅 메모"),
                fieldWithPath("waiting[].seatOptionName").type(JsonFieldType.STRING)
                    .description("좌석 옵션 이름"),
                fieldWithPath("waiting[].totalSeatCount").type(JsonFieldType.NUMBER)
                    .description("총 좌석수"),
                fieldWithPath("waiting[].personOptionText").type(JsonFieldType.STRING)
                    .description("인원 옵션 설정 정보 - 텍스트"),
                fieldWithPath("waiting[].personOptions").type(JsonFieldType.ARRAY)
                    .description("인원 옵션 설정 정보"),
                fieldWithPath("waiting[].personOptions[].name").type(JsonFieldType.STRING)
                    .description("인원 옵션 - 이름 "),
                fieldWithPath("waiting[].personOptions[].count").type(JsonFieldType.NUMBER)
                    .description("인원 옵션 - 수"),
                fieldWithPath("waiting[].personOptions[].additionalOptions").type(
                    JsonFieldType.ARRAY).description("추가 인원 옵션").optional(),
                fieldWithPath("waiting[].personOptions[].additionalOptions[].name").type(
                    JsonFieldType.STRING).description("추가 인원 옵션 - 이름").optional(),
                fieldWithPath("waiting[].personOptions[].additionalOptions[].count").type(
                    JsonFieldType.NUMBER).description("추가 인원 옵션 - 수").optional(),
                fieldWithPath("waiting[].expectedSittingDateTime").type(JsonFieldType.STRING)
                    .description("예상 착석 시각").optional(),
                fieldWithPath("waiting[].waitingCompleteDateTime").type(JsonFieldType.STRING)
                    .description("웨이팅 완료(착석, 취소) 시각").optional(),
                fieldWithPath("waiting[].calledDateTime").type(JsonFieldType.STRING)
                    .description("호출 시간").optional(),
                fieldWithPath("waiting[].lastCalledDateTime").type(JsonFieldType.STRING)
                    .description("마지막 호출 시간").optional(),
                fieldWithPath("waiting[].callCount").type(JsonFieldType.NUMBER)
                    .description("호출 횟수"),
                fieldWithPath("waiting[].isSentReadyToEnterAlarm").type(JsonFieldType.BOOLEAN)
                    .description("입장 준비 알림 전송 여부"),
                fieldWithPath("waiting[].regDateTime").type(JsonFieldType.STRING)
                    .description("등록일"),
                fieldWithPath("waiting[].order").type(JsonFieldType.OBJECT)
                    .description("주문 정보"),
                fieldWithPath("waiting[].order.id").type(JsonFieldType.STRING)
                    .description("주문 short UUID"),
                fieldWithPath("waiting[].order.orderLineItems[].menuId").type(JsonFieldType.STRING)
                    .description("메뉴 short UUID"),
                fieldWithPath("waiting[].order.orderLineItems[].name").type(JsonFieldType.STRING)
                    .description("메뉴 이름"),
                fieldWithPath("waiting[].order.orderLineItems[].quantity").type(
                        JsonFieldType.NUMBER)
                    .description("메뉴 수량"),
                fieldWithPath("totalCount").type(JsonFieldType.NUMBER).description("웨이팅 총 개수"),
                fieldWithPath("page").type(JsonFieldType.NUMBER).description("페이지 번호"),
                fieldWithPath("limit").type(JsonFieldType.NUMBER).description("사이즈")
            )
        ));
  }


  @Test
  @DisplayName("웨이팅 목록")
  void waitingList() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String ctmAuth = "Bearer accessToken";

    LocalDate operationDate = LocalDate.of(2023, 2, 1);

    List<WaitingVO> waitingList = List.of(createWaitingList(SHOP_ID, operationDate));

    WaitingListResponse response = WaitingListResponse.builder()
        .page(new PageVO(waitingList.size(), 1, 10))
        .waiting(waitingList)
        .build();

    // when
    when(managementWaitingListApiService.getWaitingList(any(), any(LocalDate.class), any())).thenReturn(response);

    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/management/waiting/list", SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-CTM-AUTH", ctmAuth)
                .queryParam("modeType", "TABLE")
                .queryParam("waitingStatus", "WAITING")
                .queryParam("seatOptionId", "XVxg4bLoTcGO5qtrhqZ57A,tZCcLfzXQyGI6hKo8tVLrw")
                .queryParam("page", "1")
                .queryParam("limit", "100"))
        .andExpect(status().isOk())
        .andDo(document("management-waiting-list",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            queryParameters(
                parameterWithName("modeType").description(
                    "운영 모드: DEFAULT(기본모드), TABLE(테이블모드) - 기본 값: DEFAULT, optional").optional(),
                parameterWithName("seatOptionId").description("좌석 옵션 아이디 (shortUUID) - optional")
                    .optional(),
                parameterWithName("waitingStatus").description(
                        "웨이팅 상태 필터: WAITING(웨이팅 중), SITTING(착석), CANCEL(취소) - 기본값: WAITING, optional")
                    .optional(),
                parameterWithName("page").description("페이지 - 기본값 1, optional").optional(),
                parameterWithName("limit").description("Limit - 기본값 100, optional").optional()
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),

                fieldWithPath("waiting").type(JsonFieldType.ARRAY).description("웨이팅 목록"),
                fieldWithPath("waiting[].waitingId").type(JsonFieldType.STRING)
                    .description("웨이팅 아이디"),
                fieldWithPath("waiting[].shopId").type(JsonFieldType.STRING).description("매장 아이디"),
                fieldWithPath("waiting[].registerChannel").type(JsonFieldType.STRING)
                    .description("등록 체널: WAITING_APP(현장), WAITING_MANAGER(수기), CATCH_APP(원격)"),
                fieldWithPath("waiting[].registerChannelText").type(JsonFieldType.STRING)
                    .description("등록 체널 텍스트 - 현장, 수기, 원격"),
                fieldWithPath("waiting[].operationDate").type(JsonFieldType.STRING)
                    .description("영업일"),
                fieldWithPath("waiting[].customerSeq").type(JsonFieldType.NUMBER)
                    .description("고객 시퀀스").optional(),
                fieldWithPath("waiting[].customerPhoneNumber").type(JsonFieldType.STRING)
                    .description("고객 연락처").optional(),
                fieldWithPath("waiting[].customerName").type(JsonFieldType.STRING)
                    .description("고객 이름").optional(),
                fieldWithPath("waiting[].sittingCount").type(JsonFieldType.NUMBER)
                    .description("착석 횟수"),
                fieldWithPath("waiting[].waitingNumber").type(JsonFieldType.NUMBER)
                    .description("웨이팅 채번"),
                fieldWithPath("waiting[].waitingOrder").type(JsonFieldType.NUMBER)
                    .description("웨이팅 순번"),
                fieldWithPath("waiting[].waitingStatus").type(JsonFieldType.STRING)
                    .description("웨이팅 상태"),
                fieldWithPath("waiting[].waitingDetailStatus").type(JsonFieldType.STRING)
                    .description("웨이팅 상세 상태"),
                fieldWithPath("waiting[].waitingMemo").type(JsonFieldType.STRING)
                    .description("웨이팅 메모"),
                fieldWithPath("waiting[].seatOptionName").type(JsonFieldType.STRING)
                    .description("좌석 옵션 이름"),
                fieldWithPath("waiting[].totalSeatCount").type(JsonFieldType.NUMBER)
                    .description("총 좌석수"),
                fieldWithPath("waiting[].personOptionText").type(JsonFieldType.STRING)
                    .description("인원 옵션 설정 정보 - 텍스트"),
                fieldWithPath("waiting[].personOptions").type(JsonFieldType.ARRAY)
                    .description("인원 옵션 설정 정보"),
                fieldWithPath("waiting[].personOptions[].name").type(JsonFieldType.STRING)
                    .description("인원 옵션 - 이름 "),
                fieldWithPath("waiting[].personOptions[].count").type(JsonFieldType.NUMBER)
                    .description("인원 옵션 - 수"),
                fieldWithPath("waiting[].personOptions[].additionalOptions").type(
                    JsonFieldType.ARRAY).description("추가 인원 옵션").optional(),
                fieldWithPath("waiting[].personOptions[].additionalOptions[].name").type(
                    JsonFieldType.STRING).description("추가 인원 옵션 - 이름").optional(),
                fieldWithPath("waiting[].personOptions[].additionalOptions[].count").type(
                    JsonFieldType.NUMBER).description("추가 인원 옵션 - 수").optional(),
                fieldWithPath("waiting[].expectedSittingDateTime").type(JsonFieldType.STRING)
                    .description("예상 착석 시각").optional(),
                fieldWithPath("waiting[].waitingCompleteDateTime").type(JsonFieldType.STRING)
                    .description("웨이팅 완료(착석, 취소) 시각").optional(),
                fieldWithPath("waiting[].calledDateTime").type(JsonFieldType.STRING)
                    .description("호출 시간").optional(),
                fieldWithPath("waiting[].lastCalledDateTime").type(JsonFieldType.STRING)
                    .description("마지막 호출 시간").optional(),
                fieldWithPath("waiting[].callCount").type(JsonFieldType.NUMBER)
                    .description("호출 횟수"),
                fieldWithPath("waiting[].isSentReadyToEnterAlarm").type(JsonFieldType.BOOLEAN)
                    .description("입장 준비 알림 전송 여부"),
                fieldWithPath("waiting[].regDateTime").type(JsonFieldType.STRING)
                    .description("등록일"),
                fieldWithPath("waiting[].order").type(JsonFieldType.OBJECT)
                    .description("주문 정보"),
                fieldWithPath("waiting[].order.id").type(JsonFieldType.STRING)
                    .description("주문 short UUID"),
                fieldWithPath("waiting[].order.orderLineItems[].menuId").type(JsonFieldType.STRING)
                    .description("메뉴 short UUID"),
                fieldWithPath("waiting[].order.orderLineItems[].name").type(JsonFieldType.STRING)
                    .description("메뉴 이름"),
                fieldWithPath("waiting[].order.orderLineItems[].quantity").type(
                        JsonFieldType.NUMBER)
                    .description("메뉴 수량"),
                fieldWithPath("totalCount").type(JsonFieldType.NUMBER).description("웨이팅 총 개수"),
                fieldWithPath("page").type(JsonFieldType.NUMBER).description("페이지 번호"),
                fieldWithPath("limit").type(JsonFieldType.NUMBER).description("사이즈")
            )
        ));
  }


  private WaitingVO createWaitingList(String shopId, LocalDate operationDate) {
    PersonOptionsData personOptionsData = createPersonOptionData();

    return WaitingVO.builder()
        .waitingId(UUIDUtil.shortUUID())
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
        .waitingMemo("단골손님, 알레르기, 소음주의, 40대 남")
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

  private static ShopOperationInfoVO createShopOperationInfo(LocalDate operationDate) {
    return ShopOperationInfoVO.builder()
        .operationDate(operationDate)
        .operationStatus(OperationStatus.OPEN)
        .operationStartDateTime(
            ZonedDateTime.of(operationDate, LocalTime.now(), ZoneId.of("Asia/Seoul")))
        .operationEndDateTime(
            ZonedDateTime.of(operationDate, LocalTime.now(), ZoneId.of("Asia/Seoul")))
        .build();
  }

  private static WaitingCurrentStatusVO createCurrentStatus() {
    return WaitingCurrentStatusVO.builder()
        .teamCount(1)
        .peopleCount(2)
        .seatsCurrentStatuses(List.of(WaitingCurrentStatusVO.SeatsCurrentStatus.builder()
                .id(UUIDUtil.shortUUID())
                .seatOptionName("테이블1")
                .teamCount(1)
                .peopleCount(2)
                .expectedWaitingTime(10)
                .isUsedExpectedWaitingPeriod(true)
                .seatOption(SeatOption.builder()
                    .minSeatCount(1)
                    .maxSeatCount(10)
                    .isPickup(false)
                    .build())
                .build(),
            WaitingCurrentStatusVO.SeatsCurrentStatus.builder()
                .id(UUIDUtil.shortUUID())
                .seatOptionName("테이블2")
                .teamCount(0)
                .peopleCount(0)
                .expectedWaitingTime(0)
                .isUsedExpectedWaitingPeriod(true)
                .seatOption(SeatOption.builder()
                    .minSeatCount(1)
                    .maxSeatCount(10)
                    .isPickup(true)
                    .build())
                .build()))
        .build();
  }

  private static PersonOptionsData createPersonOptionData() {
    return PersonOptionsData.builder()
        .personOptions(List.of(PersonOption.builder()
                .name("성인").count(2).build(),
            PersonOption.builder()
                .name("유아").count(1).build())).build();
  }
}