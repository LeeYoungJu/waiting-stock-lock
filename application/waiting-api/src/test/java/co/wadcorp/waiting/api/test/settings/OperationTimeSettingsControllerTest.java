package co.wadcorp.waiting.api.test.settings;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentRequest;
import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentResponse;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.controller.settings.OperationSettingsController;
import co.wadcorp.waiting.api.model.settings.request.OperationTimeSettingsRequest;
import co.wadcorp.waiting.api.model.settings.response.OperationTimeSettingsResponse;
import co.wadcorp.waiting.api.model.settings.vo.OperationTimeForDayVO;
import co.wadcorp.waiting.api.model.settings.vo.OperationTimeSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.PauseReasonVO;
import co.wadcorp.waiting.api.service.settings.OperationTimeSettingsApiService;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.data.config.CustomObjectMapper;
import co.wadcorp.waiting.data.domain.settings.DefaultOperationTimeSettingDataFactory;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

public class OperationTimeSettingsControllerTest extends RestDocsSupport {

  private OperationTimeSettingsApiService operationTimeSettingsApiService = mock(OperationTimeSettingsApiService.class);

  private CustomObjectMapper objectMapper = new CustomObjectMapper();

  @Override
  public Object init() {
    return new OperationSettingsController(operationTimeSettingsApiService);
  }

  @Test
  @DisplayName("웨이팅_운영시간설정_조회")
  public void getOperationTimeSettings() throws Exception {

    // given
    final String SHOP_ID = "SHOP_UUID";

    OperationTimeSettingsVO vo = OperationTimeSettingsVO.builder()
        .operationTimeForDays(getOperationTimeForDays())
        .isUsedAutoPause(true)
        .autoPauseStartTime(LocalTime.of(10, 0))
        .autoPauseEndTime(LocalTime.of(20, 0))
        .pauseReasons(List.of(
            PauseReasonVO.builder().id(UUIDUtil.shortUUID()).isDefault(true).reason("잠시만 기다려주세요 :D").build()))
        .build();

    OperationTimeSettingsResponse response = OperationTimeSettingsResponse.builder()
        .operationTimeSettings(vo)
        .build();

    // when
    when(operationTimeSettingsApiService.getOperationTimeSettings(any())).thenReturn(response);

    mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/settings/waiting-operation-time", SHOP_ID)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("operation-time-settings-get",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("operationTimeForDays").type(JsonFieldType.ARRAY).description("요일별 웨이팅 운영시간"),
                fieldWithPath("operationTimeForDays[].day").type(JsonFieldType.STRING).description("요일 (MONDAY~SUNDAY)"),
                fieldWithPath("operationTimeForDays[].operationStartTime").description("운영 시작시간 (HH:mm)"),
                fieldWithPath("operationTimeForDays[].operationEndTime").description("운영 종료시간 (HH:mm)"),
                fieldWithPath("operationTimeForDays[].isClosedDay").type(JsonFieldType.BOOLEAN).description("휴무일 여부"),
                fieldWithPath("isUsedAutoPause").type(JsonFieldType.BOOLEAN).description("일시정지 사용여부"),
                fieldWithPath("autoPauseStartTime").description("일시정지 시작시간 (HH:mm)"),
                fieldWithPath("autoPauseEndTime").description("일시정지 종료시간 (HH:mm)"),
                fieldWithPath("pauseReasons[]").type(JsonFieldType.ARRAY).description("일시정지 안내문구"),
                fieldWithPath("pauseReasons[].id").type(JsonFieldType.STRING).description("일시정지 UUID"),
                fieldWithPath("pauseReasons[].isDefault").type(JsonFieldType.BOOLEAN).description("일시정지 기본 값 여부").optional(),
                fieldWithPath("pauseReasons[].reason").type(JsonFieldType.STRING).description("일시정지 안내문구").optional()
            )
        ));
  }

  @Test
  @DisplayName("웨이팅_운영시간설정_수정")
  public void saveOperationTimeSettings() throws Exception {

    // given
    final String SHOP_ID = "SHOP_UUID";
    OperationTimeSettingsRequest request = OperationTimeSettingsRequest.builder()
        .operationTimeForDays(getOperationTimeForDays())
        .isUsedAutoPause(true)
        .autoPauseStartTime(LocalTime.of(10, 0))
        .autoPauseEndTime(LocalTime.of(20, 0))
        .pauseReasons(List.of(
            PauseReasonVO.builder().id(UUIDUtil.shortUUID()).isDefault(true).reason("잠시만 기다려주세요 :D").build()))
        .build();

    OperationTimeSettingsVO vo = OperationTimeSettingsVO.builder()
        .operationTimeForDays(getOperationTimeForDays())
        .isUsedAutoPause(true)
        .autoPauseStartTime(LocalTime.of(10, 0))
        .autoPauseEndTime(LocalTime.of(20, 0))
        .pauseReasons(List.of(
            PauseReasonVO.builder().id(UUIDUtil.shortUUID()).isDefault(true).reason("잠시만 기다려주세요 :D").build()))
        .build();

    OperationTimeSettingsResponse response = OperationTimeSettingsResponse.builder()
        .operationTimeSettings(vo)
        .build();

    // when
    when(operationTimeSettingsApiService.saveOperationTimeSettings(any(), any(), any())).thenReturn(response);

    mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/settings/waiting-operation-time", SHOP_ID)
            .header("X-REQUEST-ID", "deviceId")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("operation-time-settings-save",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            requestFields(
                fieldWithPath("operationTimeForDays").type(JsonFieldType.ARRAY).description("요일별 웨이팅 운영시간"),
                fieldWithPath("operationTimeForDays[].day").type(JsonFieldType.STRING).description("요일 (MONDAY~SUNDAY)"),
                fieldWithPath("operationTimeForDays[].operationStartTime").type(JsonFieldType.STRING).description("운영 시작시간 (HH:mm)"),
                fieldWithPath("operationTimeForDays[].operationEndTime").type(JsonFieldType.STRING).description("운영 종료시간 (HH:mm)"),
                fieldWithPath("operationTimeForDays[].isClosedDay").type(JsonFieldType.BOOLEAN).description("휴무일 여부"),
                fieldWithPath("isUsedAutoPause").type(JsonFieldType.BOOLEAN).description("일시정지 사용여부"),
                fieldWithPath("autoPauseStartTime").type(JsonFieldType.STRING).description("일시정지 시작시간 (HH:mm)"),
                fieldWithPath("autoPauseEndTime").type(JsonFieldType.STRING).description("일시정지 종료시간 (HH:mm)"),
                fieldWithPath("pauseReasons[]").type(JsonFieldType.ARRAY).description("일시정지 안내문구"),
                fieldWithPath("pauseReasons[].id").type(JsonFieldType.STRING).description("일시정지 UUID"),
                fieldWithPath("pauseReasons[].isDefault").type(JsonFieldType.BOOLEAN).description("일시정지 기본 값 여부").optional(),
                fieldWithPath("pauseReasons[].reason").type(JsonFieldType.STRING).description("일시정지 안내문구").optional()
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("operationTimeForDays").type(JsonFieldType.ARRAY).description("요일별 웨이팅 운영시간"),
                fieldWithPath("operationTimeForDays[].day").type(JsonFieldType.STRING).description("요일 (MONDAY~SUNDAY)"),
                fieldWithPath("operationTimeForDays[].operationStartTime").type(JsonFieldType.STRING).description("운영 시작시간 (HH:mm)"),
                fieldWithPath("operationTimeForDays[].operationEndTime").type(JsonFieldType.STRING).description("운영 종료시간 (HH:mm)"),
                fieldWithPath("operationTimeForDays[].isClosedDay").type(JsonFieldType.BOOLEAN).description("휴무일 여부"),
                fieldWithPath("isUsedAutoPause").type(JsonFieldType.BOOLEAN).description("일시정지 사용여부"),
                fieldWithPath("autoPauseStartTime").type(JsonFieldType.STRING).description("일시정지 시작시간 (HH:mm)"),
                fieldWithPath("autoPauseEndTime").type(JsonFieldType.STRING).description("일시정지 종료시간 (HH:mm)"),
                fieldWithPath("pauseReasons[]").type(JsonFieldType.ARRAY).description("일시정지 안내문구"),
                fieldWithPath("pauseReasons[].id").type(JsonFieldType.STRING).description("일시정지 UUID"),
                fieldWithPath("pauseReasons[].isDefault").type(JsonFieldType.BOOLEAN).description("일시정지 기본 값 여부").optional(),
                fieldWithPath("pauseReasons[].reason").type(JsonFieldType.STRING).description("일시정지 안내문구").optional()
            )
        ));

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
}
