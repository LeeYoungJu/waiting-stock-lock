package co.wadcorp.waiting.api.test.settings;

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

import co.wadcorp.waiting.api.controller.settings.AlarmSettingsController;
import co.wadcorp.waiting.api.model.settings.request.AlarmSettingsRequest;
import co.wadcorp.waiting.api.model.settings.response.AlarmSettingsResponse;
import co.wadcorp.waiting.api.model.settings.vo.AlarmSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.AlarmSettingsVO.AlarmSettingsVOBuilder;
import co.wadcorp.waiting.api.service.settings.AlarmSettingsApiService;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.data.domain.settings.AlarmSettingsEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

public class AlarmSettingsControllerTest extends RestDocsSupport {

  private final AlarmSettingsApiService alarmSettingsApiService = mock(AlarmSettingsApiService.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object init() {
    return new AlarmSettingsController(alarmSettingsApiService);
  }

  @Test
  @DisplayName("웨이팅 알림 설정 조회")
  void getOptionSettings() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    AlarmSettingsResponse response = defaultResponse();

    // when
    when(alarmSettingsApiService.getWaitingAlarmSettings(any())).thenReturn(response);

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/settings/waiting-alarm", SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("alarm-settings-get",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("autoCancelPeriod").type(JsonFieldType.NUMBER).description("호출 경과 시간 (1 ~ 10)"),
                fieldWithPath("isUsedAutoCancel").type(JsonFieldType.BOOLEAN).description("자동 취소 설정 사용 여부"),
                fieldWithPath("autoAlarmOrdering").type(JsonFieldType.NUMBER).description("자동 발송 설정 (1 ~ 99)"),
                fieldWithPath("isAutoEnterAlarm").type(JsonFieldType.BOOLEAN).description("입장안내 자동 발송 여부")
            )
        ));
  }

  @Test
  @DisplayName("웨이팅 알림 설정 저장")
  void saveOptionSettings() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    AlarmSettingsResponse response = defaultResponse();
    AlarmSettingsRequest request = defaultRequest();

    // when
    when(alarmSettingsApiService.save(any(), any(), any())).thenReturn(response);
    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/settings/waiting-alarm", SHOP_ID)
                .header("X-REQUEST-ID", "deviceId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("alarm-settings-post",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            requestFields(
                fieldWithPath("autoCancelPeriod").type(JsonFieldType.NUMBER).description("호출 경과 시간 (1 ~ 10)"),
                fieldWithPath("isUsedAutoCancel").type(JsonFieldType.BOOLEAN).description("자동 취소 설정 사용 여부"),
                fieldWithPath("autoAlarmOrdering").type(JsonFieldType.NUMBER).description("자동 발송 설정 (1 ~ 99)"),
                fieldWithPath("isAutoEnterAlarm").type(JsonFieldType.BOOLEAN).description("입장안내 자동 발송 여부")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("autoCancelPeriod").type(JsonFieldType.NUMBER).description("호출 경과 시간 (1 ~ 10)"),
                fieldWithPath("isUsedAutoCancel").type(JsonFieldType.BOOLEAN).description("자동 취소 설정 사용 여부"),
                fieldWithPath("autoAlarmOrdering").type(JsonFieldType.NUMBER).description("자동 발송 설정 (1 ~ 99)"),
                fieldWithPath("isAutoEnterAlarm").type(JsonFieldType.BOOLEAN).description("입장안내 자동 발송 여부")
            )
        ));
  }

  private AlarmSettingsRequest defaultRequest() {
    return new AlarmSettingsRequest(3, true, 1, true);
  }


  private AlarmSettingsResponse defaultResponse() {
    return new AlarmSettingsResponse(AlarmSettingsVO.builder().autoAlarmOrdering(3)
        .isUsedAutoCancel(true).autoCancelPeriod(1).isAutoEnterAlarm(true).build());
  }

}
