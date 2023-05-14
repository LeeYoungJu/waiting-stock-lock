package co.wadcorp.waiting.api.test.waiting.register;

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
import co.wadcorp.waiting.api.controller.waiting.register.RegisterWaitingIntroController;
import co.wadcorp.waiting.api.model.settings.response.RegisterSettingsResponse;
import co.wadcorp.waiting.api.model.settings.vo.HomeSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.OptionSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.OptionSettingsVO.AdditionalOption;
import co.wadcorp.waiting.api.model.settings.vo.OptionSettingsVO.PersonOptionSetting;
import co.wadcorp.waiting.api.model.settings.vo.OrderSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.PrecautionSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.PrecautionVO;
import co.wadcorp.waiting.api.model.settings.vo.SeatOptionSettingVO;
import co.wadcorp.waiting.api.model.waiting.response.RegisterCurrentStatusResponse;
import co.wadcorp.waiting.data.domain.shop.operation.status.OperationStatus;
import co.wadcorp.waiting.api.model.waiting.vo.ShopOperationInfoVO;
import co.wadcorp.waiting.api.model.waiting.vo.WaitingCurrentStatusVO;
import co.wadcorp.waiting.api.model.waiting.vo.WaitingCurrentStatusVO.SeatsCurrentStatus;
import co.wadcorp.waiting.api.service.waiting.WaitingRegisterIntroApiService;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsData;
import co.wadcorp.waiting.data.enums.WaitingModeType;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingCurrentStatusDto.SeatOption;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
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

public class RegisterWaitingIntroControllerTest extends RestDocsSupport {

  private final WaitingRegisterIntroApiService waitingRegisterIntroApiService = mock(
      WaitingRegisterIntroApiService.class);

  @Override
  public Object init() {
    return new RegisterWaitingIntroController(waitingRegisterIntroApiService);
  }


  @Test
  @DisplayName("웨이팅 현황 조회")
  void currentStatus() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String ctmAuth = "Bearer accessToken";

    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
    ShopOperationInfoVO operationInfo = createShopOperationInfo(operationDate);
    WaitingCurrentStatusVO currentStatus = createCurrentStatus();

    RegisterCurrentStatusResponse response = RegisterCurrentStatusResponse.builder()
        .operationInfo(operationInfo)
        .currentStatus(currentStatus)
        .build();

    when(waitingRegisterIntroApiService.getTableCurrentStatus(any(), any(LocalDate.class),
        any(ZonedDateTime.class))).thenReturn(response);
    when(waitingRegisterIntroApiService.getDefaultCurrentStatus(any(), any(LocalDate.class),
        any(ZonedDateTime.class))).thenReturn(response);

    mockMvc.perform(
            RestDocumentationRequestBuilders.get(
                    "/api/v1/shops/{shopId}/register/waiting/current-status", SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-CTM-AUTH", ctmAuth)
                .queryParam("modeType", "TABLE"))
        .andExpect(status().isOk())
        .andDo(document("register-current-status",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            queryParameters(
                parameterWithName("modeType").description("운영 모드 - DEFAULT, TABLE")
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
                fieldWithPath("currentStatus.seatsCurrentStatuses[].id").type(
                    JsonFieldType.STRING).description("좌석 아이디"),
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
        .seatsCurrentStatuses(List.of(SeatsCurrentStatus.builder()
                .id(UUIDUtil.shortUUID())
                .seatOptionName("테이블1")
                .seatOption(
                    SeatOption.builder().minSeatCount(1).maxSeatCount(5).isPickup(false).build())
                .teamCount(1)
                .peopleCount(2)
                .expectedWaitingTime(10)
                .isUsedExpectedWaitingPeriod(true)
                .build(),
            SeatsCurrentStatus.builder()
                .id(UUIDUtil.shortUUID())
                .seatOptionName("테이블2")
                .seatOption(
                    SeatOption.builder().minSeatCount(1).maxSeatCount(5).isPickup(false).build())
                .teamCount(0)
                .peopleCount(0)
                .expectedWaitingTime(0)
                .isUsedExpectedWaitingPeriod(true)
                .build()))
        .build();
  }


  @Test
  @DisplayName("등록_필요_설정_조회")
  public void getAllRegisterSettingsTest() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    RegisterSettingsResponse response = RegisterSettingsResponse.builder()
        .optionSettings(getDefaultOptionSettingsVO())
        .homeSettings(getDefaultHomeSettingsVO())
        .precautionSettings(getDefaultPrecautionSettingsVO())
        .orderSettings(
            OrderSettingsVO.toDto(OrderSettingsData.builder().isPossibleOrder(true).build())
        )
        .build();

    // when
    when(waitingRegisterIntroApiService.getAllRegisterSettings(any())).thenReturn(response);
    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/register/waiting/settings",
                    SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("register-settings",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("optionSettings").type(JsonFieldType.OBJECT).description("옵션설정"),
                fieldWithPath("optionSettings.isUsedPersonOptionSetting").type(
                    JsonFieldType.BOOLEAN).description("인원옵션 사용여부"),
                fieldWithPath("optionSettings.personOptionSettings[]").type(JsonFieldType.ARRAY)
                    .description("인원옵션"),
                fieldWithPath("optionSettings.personOptionSettings[].id").type(JsonFieldType.STRING)
                    .description("인원 옵션 UUID"),
                fieldWithPath("optionSettings.personOptionSettings[].name").type(
                    JsonFieldType.STRING).description("인원옵션 이름"),
                fieldWithPath("optionSettings.personOptionSettings[].isDisplayed").type(
                    JsonFieldType.BOOLEAN).description("인원옵션 노출 여부"),
                fieldWithPath("optionSettings.personOptionSettings[].isSeat").type(
                    JsonFieldType.BOOLEAN).description("인원옵션 착석인원 여부"),
                fieldWithPath("optionSettings.personOptionSettings[].isDefault").type(
                        JsonFieldType.BOOLEAN)
                    .description("인원 옵션 기본 옵션 여부 (기본 옵션이 true라면 옵션 이름 변경 이나 삭제가 불가능)"),
                fieldWithPath("optionSettings.personOptionSettings[].canModify").type(
                    JsonFieldType.BOOLEAN).description("인원 옵션 노출 여부, 착석 인원 여부 설정 가능 여부"),
                fieldWithPath("optionSettings.personOptionSettings[].additionalOptions").type(
                    JsonFieldType.ARRAY).description("인원 옵션 설졍 - 부가 옵션").optional(),
                fieldWithPath("optionSettings.personOptionSettings[].additionalOptions[].id").type(
                    JsonFieldType.STRING).description("부가 옵션 UUID"),
                fieldWithPath(
                    "optionSettings.personOptionSettings[].additionalOptions[].name").type(
                    JsonFieldType.STRING).description("부가 옵션 이름"),
                fieldWithPath(
                    "optionSettings.personOptionSettings[].additionalOptions[].isDisplayed").type(
                    JsonFieldType.BOOLEAN).description("부가 옵션 노출 여부"),
                fieldWithPath("homeSettings").type(JsonFieldType.OBJECT).description("홈 설정"),
                fieldWithPath("homeSettings.waitingModeType").type(JsonFieldType.STRING)
                    .description("웨이팅 타입"),
                fieldWithPath("homeSettings.defaultModeSettings").type(JsonFieldType.OBJECT)
                    .description("기본모드 설정"),
                fieldWithPath("homeSettings.defaultModeSettings.id").type(JsonFieldType.STRING)
                    .description("기본모드 UUID"),
                fieldWithPath("homeSettings.defaultModeSettings.name").type(JsonFieldType.STRING)
                    .description("기본모드 좌석옵션명"),
                fieldWithPath("homeSettings.defaultModeSettings.minSeatCount").type(
                    JsonFieldType.NUMBER).description("기본모드 최소 착석인원"),
                fieldWithPath("homeSettings.defaultModeSettings.maxSeatCount").type(
                    JsonFieldType.NUMBER).description("기본모드 최대 착석인원"),
                fieldWithPath("homeSettings.defaultModeSettings.expectedWaitingPeriod").type(
                    JsonFieldType.NUMBER).description("예상 웨이팅 시간"),
                fieldWithPath("homeSettings.defaultModeSettings.isUsedExpectedWaitingPeriod").type(
                    JsonFieldType.BOOLEAN).description("예상 웨이팅 시간 사용여부"),
                fieldWithPath("homeSettings.defaultModeSettings.isDefault").type(
                    JsonFieldType.BOOLEAN).description("기본값 여부(기본모드는 항상 true)"),
                fieldWithPath("homeSettings.defaultModeSettings.isPickup").type(
                    JsonFieldType.BOOLEAN).description("포장 여부(true-인원수 비활성화)"),
                fieldWithPath("homeSettings.tableModeSettings").type(JsonFieldType.ARRAY)
                    .description("테이블모드 설정"),
                fieldWithPath("homeSettings.tableModeSettings[].id").type(JsonFieldType.STRING)
                    .description("테이블모드 UUID"),
                fieldWithPath("homeSettings.tableModeSettings[].name").type(JsonFieldType.STRING)
                    .description("테이블별 좌석옵션명"),
                fieldWithPath("homeSettings.tableModeSettings[].minSeatCount").type(
                    JsonFieldType.NUMBER).description("테이블별 최소 착석인원"),
                fieldWithPath("homeSettings.tableModeSettings[].maxSeatCount").type(
                    JsonFieldType.NUMBER).description("테이블별 최대 착석인원"),
                fieldWithPath("homeSettings.tableModeSettings[].expectedWaitingPeriod").type(
                    JsonFieldType.NUMBER).description("테이블별 예상 웨이팅 시간"),
                fieldWithPath("homeSettings.tableModeSettings[].isUsedExpectedWaitingPeriod").type(
                    JsonFieldType.BOOLEAN).description("테이블별 예상 웨이팅 시간 사용여부"),
                fieldWithPath("homeSettings.tableModeSettings[].isDefault").type(
                    JsonFieldType.BOOLEAN).description("기본값 여부(true-삭제버튼 비활성화)"),
                fieldWithPath("homeSettings.tableModeSettings[].isPickup").type(
                    JsonFieldType.BOOLEAN).description("포장 여부(true-인원수 비활성화)"),
                fieldWithPath("precautionSettings").type(JsonFieldType.OBJECT)
                    .description("유의 사항 설정"),
                fieldWithPath("precautionSettings.messagePrecaution").type(JsonFieldType.STRING)
                    .description("알림톡 내 유의사항 내용"),
                fieldWithPath("precautionSettings.isUsedPrecautions").type(JsonFieldType.BOOLEAN)
                    .description("유의사항 사용 여부"),
                fieldWithPath("precautionSettings.precautions").type(JsonFieldType.ARRAY)
                    .description("앱 내 유의사항"),
                fieldWithPath("precautionSettings.precautions[].id").type(JsonFieldType.STRING)
                    .description("유의사항 아이디"),
                fieldWithPath("precautionSettings.precautions[].content").type(JsonFieldType.STRING)
                    .description("유의사항 내용"),
                fieldWithPath("orderSettings.isPossibleOrder").type(JsonFieldType.BOOLEAN).description("선주문 사용 여부")
            )));

  }

  private PrecautionSettingsVO getDefaultPrecautionSettingsVO() {
    return PrecautionSettingsVO
        .builder()
        .messagePrecaution("알림톡 유의사항")
        .isUsedPrecautions(true)
        .precautions(List.of(PrecautionVO.builder()
                .id(UUIDUtil.shortUUID())
                .content("앱 내 유의사항1").build(),
            PrecautionVO.builder()
                .id(UUIDUtil.shortUUID())
                .content("앱 내 유의사항2").build()
        ))
        .build();
  }

  private HomeSettingsVO getDefaultHomeSettingsVO() {
    return HomeSettingsVO.builder()
        .waitingModeType(WaitingModeType.DEFAULT.name())
        .defaultModeSettings(getDefaultModeSettings())
        .tableModeSettings(getTableModeSettings())
        .build();
  }

  private OptionSettingsVO getDefaultOptionSettingsVO() {
    return new OptionSettingsVO(true, getDefaultPersonOptions());
  }

  private static List<PersonOptionSetting> getDefaultPersonOptions() {
    return List.of(
        PersonOptionSetting.builder()
            .id(UUIDUtil.shortUUID()).name("성인").isDisplayed(true).isSeat(true)
            .isDefault(true).canModify(false)
            .additionalOptions(List.of()).build(),
        PersonOptionSetting.builder()
            .id(UUIDUtil.shortUUID()).name("유아").isDisplayed(true).isSeat(true)
            .isDefault(true).canModify(true)
            .additionalOptions(List.of(
                AdditionalOption.builder().id(UUIDUtil.shortUUID()).name("유아용 의자")
                    .isDisplayed(true).build())).build()
    );
  }


  private static SeatOptionSettingVO getDefaultModeSettings() {
    return SeatOptionSettingVO.builder()
        .id(UUIDUtil.shortUUID())
        .name("착석")
        .minSeatCount(1)
        .maxSeatCount(4)
        .expectedWaitingPeriod(10)
        .isUsedExpectedWaitingPeriod(true)
        .isDefault(true)
        .isPickup(false)
        .build();
  }

  private static List<SeatOptionSettingVO> getTableModeSettings() {
    return List.of(SeatOptionSettingVO.builder()
            .id(UUIDUtil.shortUUID())
            .name("테이블1")
            .minSeatCount(1)
            .maxSeatCount(4)
            .expectedWaitingPeriod(10)
            .isUsedExpectedWaitingPeriod(true)
            .isDefault(true)
            .isPickup(false)
            .build(),
        SeatOptionSettingVO.builder()
            .id(UUIDUtil.shortUUID())
            .name("테이블2")
            .minSeatCount(1)
            .maxSeatCount(4)
            .expectedWaitingPeriod(10)
            .isUsedExpectedWaitingPeriod(true)
            .isDefault(true)
            .isPickup(false)
            .build());
  }

}
