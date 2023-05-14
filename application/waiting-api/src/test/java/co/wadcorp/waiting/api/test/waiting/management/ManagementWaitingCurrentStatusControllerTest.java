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
import co.wadcorp.waiting.api.controller.waiting.management.ManagementWaitingCurrentStatusController;
import co.wadcorp.waiting.api.model.waiting.vo.ShopOperationInfoVO;
import co.wadcorp.waiting.api.model.waiting.vo.WaitingCurrentStatusVO;
import co.wadcorp.waiting.api.service.waiting.management.ManagementWaitingCurrentStatusApiService;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementWaitingCurrentStatusResponse;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.data.domain.shop.operation.status.OperationStatus;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingCurrentStatusDto.SeatOption;
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

class ManagementWaitingCurrentStatusControllerTest extends RestDocsSupport {

  private final ManagementWaitingCurrentStatusApiService service = mock(ManagementWaitingCurrentStatusApiService.class);

  @Override
  public Object init() {
    return new ManagementWaitingCurrentStatusController(service);
  }

  @Test
  @DisplayName("웨이팅 현황, 운영 정보")
  void waitingList() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String ctmAuth = "Bearer accessToken";

    LocalDate operationDate = LocalDate.of(2023, 2, 1);

    ShopOperationInfoVO operationInfo = createShopOperationInfo(operationDate);

    WaitingCurrentStatusVO currentStatus = createCurrentStatus();

    ManagementWaitingCurrentStatusResponse response = ManagementWaitingCurrentStatusResponse.builder()
        .operationInfo(operationInfo)
        .currentStatus(currentStatus)
        .build();

    // when
    when(service.getCurrentStatus(any(), any(LocalDate.class), any(ZonedDateTime.class))).thenReturn(response);

    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/management/waiting/current-status", SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-CTM-AUTH", ctmAuth))
        .andExpect(status().isOk())
        .andDo(document("management-waiting-current-status",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
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
                    JsonFieldType.BOOLEAN).description("웨이팅 예상 대기 시간 사용 여부")
            )
        ));
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
}