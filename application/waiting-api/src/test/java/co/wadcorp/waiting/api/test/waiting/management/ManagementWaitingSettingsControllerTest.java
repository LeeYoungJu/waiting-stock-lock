package co.wadcorp.waiting.api.test.waiting.management;

import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentRequest;
import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.controller.waiting.management.ManagementWaitingSettingsController;
import co.wadcorp.waiting.api.model.settings.response.ManagementSettingsResponse;
import co.wadcorp.waiting.api.model.settings.vo.AlarmSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.HomeSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.OperationTimeForDayVO;
import co.wadcorp.waiting.api.model.settings.vo.OperationTimeSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.OptionSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.OptionSettingsVO.AdditionalOption;
import co.wadcorp.waiting.api.model.settings.vo.OptionSettingsVO.PersonOptionSetting;
import co.wadcorp.waiting.api.model.settings.vo.OrderSettingsManagementVO;
import co.wadcorp.waiting.api.model.settings.vo.PauseReasonVO;
import co.wadcorp.waiting.api.model.settings.vo.SeatOptionSettingVO;
import co.wadcorp.waiting.api.service.waiting.management.ManagementWaitingSettingsApiService;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.data.domain.settings.DefaultOperationTimeSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsData;
import co.wadcorp.waiting.data.enums.WaitingModeType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

class ManagementWaitingSettingsControllerTest extends RestDocsSupport {

  private final ManagementWaitingSettingsApiService managementWaitingSettingsApiService = mock(
      ManagementWaitingSettingsApiService.class);

  @Override
  public Object init() {
    return new ManagementWaitingSettingsController(managementWaitingSettingsApiService);
  }

  @Test
  @DisplayName("관리모드_필요_설정_조회")
  void getAllManagementSettingsTest() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    ManagementSettingsResponse response = ManagementSettingsResponse.builder()
        .optionSettings(getDefaultOptionSettingsVO())
        .homeSettings(getDefaultHomeSettingsVO())
        .operationTimeSettings(getOperationTimeSettingsVO())
        .alarmSettings(getAlarmSettingsVO())
        .orderSettings(OrderSettingsManagementVO.toDto(
            OrderSettingsData.builder().isPossibleOrder(true).build(), 1
        ))
        .build();

    // when
    when(managementWaitingSettingsApiService.getAllManagementSettings(any(), any(LocalDate.class)))
        .thenReturn(response);

    mockMvc.perform(
            get("/api/v1/shops/{shopId}/management/waiting/settings", SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("management-settings",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("optionSettings").type(JsonFieldType.OBJECT).description("옵션설정"),
                fieldWithPath("optionSettings.isUsedPersonOptionSetting")
                    .type(JsonFieldType.BOOLEAN)
                    .description("인원옵션 사용여부"),
                fieldWithPath("optionSettings.personOptionSettings[]").type(JsonFieldType.ARRAY)
                    .description("인원옵션"),
                fieldWithPath("optionSettings.personOptionSettings[].id").type(JsonFieldType.STRING)
                    .description("인원 옵션 UUID"),
                fieldWithPath("optionSettings.personOptionSettings[].name")
                    .type(JsonFieldType.STRING)
                    .description("인원옵션 이름"),
                fieldWithPath("optionSettings.personOptionSettings[].isDisplayed")
                    .type(JsonFieldType.BOOLEAN)
                    .description("인원옵션 노출 여부"),
                fieldWithPath("optionSettings.personOptionSettings[].isSeat")
                    .type(JsonFieldType.BOOLEAN)
                    .description("인원옵션 착석인원 여부"),
                fieldWithPath("optionSettings.personOptionSettings[].isDefault")
                    .type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 기본 옵션 여부 (기본 옵션이 true라면 옵션 이름 변경 이나 삭제가 불가능)"),
                fieldWithPath("optionSettings.personOptionSettings[].canModify")
                    .type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 노출 여부, 착석 인원 여부 설정 가능 여부"),
                fieldWithPath("optionSettings.personOptionSettings[].additionalOptions")
                    .type(JsonFieldType.ARRAY)
                    .description("인원 옵션 설졍 - 부가 옵션").optional(),
                fieldWithPath("optionSettings.personOptionSettings[].additionalOptions[].id")
                    .type(JsonFieldType.STRING)
                    .description("부가 옵션 UUID"),
                fieldWithPath("optionSettings.personOptionSettings[].additionalOptions[].name")
                    .type(JsonFieldType.STRING)
                    .description("부가 옵션 이름"),
                fieldWithPath(
                    "optionSettings.personOptionSettings[].additionalOptions[].isDisplayed"
                )
                    .type(JsonFieldType.BOOLEAN)
                    .description("부가 옵션 노출 여부"),
                fieldWithPath("homeSettings.waitingModeType").type(JsonFieldType.STRING)
                    .description("웨이팅 타입"),
                fieldWithPath("homeSettings.defaultModeSettings").type(JsonFieldType.OBJECT)
                    .description("기본모드 설정"),
                fieldWithPath("homeSettings.defaultModeSettings.id").type(JsonFieldType.STRING)
                    .description("기본모드 UUID"),
                fieldWithPath("homeSettings.defaultModeSettings.name").type(JsonFieldType.STRING)
                    .description("기본모드 좌석옵션명"),
                fieldWithPath("homeSettings.defaultModeSettings.minSeatCount")
                    .type(JsonFieldType.NUMBER)
                    .description("기본모드 최소 착석인원"),
                fieldWithPath("homeSettings.defaultModeSettings.maxSeatCount")
                    .type(JsonFieldType.NUMBER)
                    .description("기본모드 최대 착석인원"),
                fieldWithPath("homeSettings.defaultModeSettings.expectedWaitingPeriod")
                    .type(JsonFieldType.NUMBER)
                    .description("테이블별 예상 웨이팅 시간"),
                fieldWithPath("homeSettings.defaultModeSettings.isUsedExpectedWaitingPeriod")
                    .type(JsonFieldType.BOOLEAN)
                    .description("예상 웨이팅 시간 사용여부"),
                fieldWithPath("homeSettings.defaultModeSettings.isDefault")
                    .type(JsonFieldType.BOOLEAN)
                    .description("기본값 여부(기본모드는 항상 true)"),
                fieldWithPath("homeSettings.defaultModeSettings.isPickup")
                    .type(JsonFieldType.BOOLEAN)
                    .description("포장 여부(true-인원수 비활성화)"),
                fieldWithPath("homeSettings.tableModeSettings")
                    .type(JsonFieldType.ARRAY)
                    .description("테이블모드 설정"),
                fieldWithPath("homeSettings.tableModeSettings[].id")
                    .type(JsonFieldType.STRING)
                    .description("테이블모드 UUID"),
                fieldWithPath("homeSettings.tableModeSettings[].name")
                    .type(JsonFieldType.STRING)
                    .description("테이블별 좌석옵션명"),
                fieldWithPath("homeSettings.tableModeSettings[].minSeatCount")
                    .type(JsonFieldType.NUMBER)
                    .description("테이블별 최소 착석인원"),
                fieldWithPath("homeSettings.tableModeSettings[].maxSeatCount")
                    .type(JsonFieldType.NUMBER)
                    .description("테이블별 최대 착석인원"),
                fieldWithPath("homeSettings.tableModeSettings[].expectedWaitingPeriod")
                    .type(JsonFieldType.NUMBER)
                    .description("테이블별 예상 웨이팅 시간"),
                fieldWithPath("homeSettings.tableModeSettings[].isUsedExpectedWaitingPeriod")
                    .type(JsonFieldType.BOOLEAN)
                    .description("테이블별 예상 웨이팅 시간 사용여부"),
                fieldWithPath("homeSettings.tableModeSettings[].isDefault")
                    .type(JsonFieldType.BOOLEAN)
                    .description("기본값 여부(true-삭제버튼 비활성화)"),
                fieldWithPath("homeSettings.tableModeSettings[].isPickup")
                    .type(JsonFieldType.BOOLEAN)
                    .description("포장 여부(true-인원수 비활성화)"),
                fieldWithPath("operationTimeSettings.operationTimeForDays")
                    .type(JsonFieldType.ARRAY)
                    .description("요일별 웨이팅 운영시간"),
                fieldWithPath("operationTimeSettings.operationTimeForDays[].day")
                    .type(JsonFieldType.STRING)
                    .description("요일 (MONDAY~SUNDAY)"),
                fieldWithPath("operationTimeSettings.operationTimeForDays[].operationStartTime")
                    .type(JsonFieldType.STRING)
                    .description("운영 시작시간 (HH:mm)"),
                fieldWithPath("operationTimeSettings.operationTimeForDays[].operationEndTime")
                    .type(JsonFieldType.STRING)
                    .description("운영 종료시간 (HH:mm)"),
                fieldWithPath("operationTimeSettings.operationTimeForDays[].isClosedDay")
                    .type(JsonFieldType.BOOLEAN)
                    .description("휴무일 여부"),
                fieldWithPath("operationTimeSettings.isUsedAutoPause").type(JsonFieldType.BOOLEAN)
                    .description("일시정지 사용여부"),
                fieldWithPath("operationTimeSettings.autoPauseStartTime").type(JsonFieldType.STRING)
                    .description("일시정지 시작시간 (HH:mm)"),
                fieldWithPath("operationTimeSettings.autoPauseEndTime").type(JsonFieldType.STRING)
                    .description("일시정지 종료시간 (HH:mm)"),
                fieldWithPath("operationTimeSettings.pauseReasons[]").type(JsonFieldType.ARRAY)
                    .description("일시정지 안내문구"),
                fieldWithPath("operationTimeSettings.pauseReasons[].id").type(JsonFieldType.STRING)
                    .description("일시정지 UUID"),
                fieldWithPath("operationTimeSettings.pauseReasons[].isDefault")
                    .type(JsonFieldType.BOOLEAN)
                    .description("일시정지 기본 값 여부").optional(),
                fieldWithPath("operationTimeSettings.pauseReasons[].reason")
                    .type(JsonFieldType.STRING)
                    .description("일시정지 안내문구").optional(),
                fieldWithPath("alarmSettings.autoCancelPeriod").type(JsonFieldType.NUMBER)
                    .description("호출 경과 시간 (1 ~ 10)"),
                fieldWithPath("alarmSettings.isUsedAutoCancel").type(JsonFieldType.BOOLEAN)
                    .description("자동 취소 설정 사용 여부"),
                fieldWithPath("alarmSettings.autoAlarmOrdering").type(JsonFieldType.NUMBER)
                    .description("자동 발송 설정 (1 ~ 99)"),
                fieldWithPath("alarmSettings.isAutoEnterAlarm").type(JsonFieldType.BOOLEAN)
                    .description("입장안내 알림톡 발송 여부"),
                fieldWithPath("orderSettings.isPossibleOrder").type(JsonFieldType.BOOLEAN)
                    .description("선주문 사용 여부"),
                fieldWithPath("orderSettings.countOfMenusUnderStockThreshold")
                    .type(JsonFieldType.NUMBER)
                    .description("재고 임계값 이하인 메뉴의 수")
            )));
  }

  private AlarmSettingsVO getAlarmSettingsVO() {
    return AlarmSettingsVO.builder().autoAlarmOrdering(3)
        .isUsedAutoCancel(true).autoCancelPeriod(1).isAutoEnterAlarm(true).build();
  }

  private OperationTimeSettingsVO getOperationTimeSettingsVO() {
    return OperationTimeSettingsVO.builder()
        .operationTimeForDays(getOperationTimeForDays())
        .isUsedAutoPause(true)
        .autoPauseStartTime(LocalTime.of(10, 0))
        .autoPauseEndTime(LocalTime.of(20, 0))
        .pauseReasons(List.of(
            PauseReasonVO.builder().id(UUIDUtil.shortUUID()).isDefault(true).reason("잠시만 기다려주세요 :D")
                .build()))
        .build();
  }

  private List<OperationTimeForDayVO> getOperationTimeForDays() {
    return DefaultOperationTimeSettingDataFactory.create().getOperationTimeForDays().stream()
        .map(e -> OperationTimeForDayVO.builder()
            .day(e.getDay())
            .operationStartTime(e.getOperationStartTime())
            .operationEndTime(e.getOperationEndTime())
            .isClosedDay(e.getIsClosedDay())
            .build())
        .toList();
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
            .id(UUIDUtil.shortUUID())
            .name("성인")
            .isDisplayed(true)
            .isSeat(true)
            .isDefault(true)
            .canModify(false)
            .additionalOptions(List.of()).build(),
        PersonOptionSetting.builder()
            .id(UUIDUtil.shortUUID())
            .name("유아").
            isDisplayed(true)
            .isSeat(true)
            .isDefault(true)
            .canModify(true)
            .additionalOptions(List.of(AdditionalOption.builder().id(UUIDUtil.shortUUID())
                .name("유아용 의자").isDisplayed(true).build())).build()
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
