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

import co.wadcorp.waiting.api.controller.settings.OptionSettingsController;
import co.wadcorp.waiting.api.model.settings.request.OptionSettingsRequest;
import co.wadcorp.waiting.api.model.settings.response.OptionSettingsResponse;
import co.wadcorp.waiting.api.model.settings.vo.OptionSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.OptionSettingsVO.PersonOptionSetting;
import co.wadcorp.waiting.api.service.settings.OptionSettingsApiService;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.data.domain.settings.DefaultOptionSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

public class OptionSettingsControllerTest extends RestDocsSupport {

  private final OptionSettingsApiService optionSettingsApiService = mock(
      OptionSettingsApiService.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object init() {
    return new OptionSettingsController(optionSettingsApiService);
  }

  @Test
  @DisplayName("웨이팅 옵션 설정 조회")
  void getOptionSettings() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    OptionSettingsResponse response = defaultResponse();

    // when
    when(optionSettingsApiService.getWaitingOptionSettings(
        any(),
        any(LocalDate.class),
        any(ZonedDateTime.class)
    )).thenReturn(response);

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/settings/waiting-option",
                    SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("option-settings-get",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("isUsedPersonOptionSetting").type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 설정 사용 여부"),
                fieldWithPath("personOptionSettings").type(JsonFieldType.ARRAY)
                    .description("인원 옵션 설정"),
                fieldWithPath("personOptionSettings[].id").type(JsonFieldType.STRING)
                    .description("인원 옵션 UUID"),
                fieldWithPath("personOptionSettings[].name").type(JsonFieldType.STRING)
                    .description("인원 옵션 이름"),
                fieldWithPath("personOptionSettings[].isDisplayed").type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 노출 여부"),
                fieldWithPath("personOptionSettings[].isSeat").type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 착석인원 여부"),
                fieldWithPath("personOptionSettings[].isDefault").type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 기본 옵션 여부 (기본 옵션이 true라면 옵션 이름 변경 이나 삭제가 불가능)"),
                fieldWithPath("personOptionSettings[].canModify").type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 노출 여부, 착석 인원 여부 설정 가능 여부"),
                fieldWithPath("personOptionSettings[].additionalOptions").type(JsonFieldType.ARRAY)
                    .description("인원 옵션 설졍 - 부가 옵션").optional(),
                fieldWithPath("personOptionSettings[].additionalOptions[].id").type(
                    JsonFieldType.STRING).description("부가 옵션 UUID"),
                fieldWithPath(
                    "personOptionSettings[].additionalOptions[].name").type(
                    JsonFieldType.STRING).description("부가 옵션 이름"),
                fieldWithPath("personOptionSettings[].additionalOptions[].isDisplayed").type(
                    JsonFieldType.BOOLEAN).description("부가 옵션 노출 여부"),
                fieldWithPath("existsWaitingTeam").type(JsonFieldType.BOOLEAN)
                    .description("웨이팅중인 팀 존재여부 (true-설정수정불가)"),
                fieldWithPath("isOpenedOperation").type(JsonFieldType.BOOLEAN)
                    .description("웨이팅 접수 중 여부 (true-설정수정불가)")
            )
        ));
  }

  @Test
  @DisplayName("웨이팅 옵션 설정 저장")
  void saveOptionSettings() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    OptionSettingsResponse response = defaultResponse();
    OptionSettingsRequest request = defaultRequest();

    // when
    when(optionSettingsApiService.save(any(), any(), any(), any(LocalDate.class), any(ZonedDateTime.class))).thenReturn(response);
    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/settings/waiting-option",
                    SHOP_ID)
                .header("X-REQUEST-ID", "deviceId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("option-settings-post",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            requestFields(
                fieldWithPath("isUsedPersonOptionSetting").type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 설정 사용 여부"),
                fieldWithPath("personOptionSettings").type(JsonFieldType.ARRAY)
                    .description("인원 옵션 설정"),
                fieldWithPath("personOptionSettings[].id").type(JsonFieldType.STRING)
                    .description("인원 옵션 UUID"),
                fieldWithPath("personOptionSettings[].name").type(JsonFieldType.STRING)
                    .description("인원 옵션 이름"),
                fieldWithPath("personOptionSettings[].isDisplayed").type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 노출 여부"),
                fieldWithPath("personOptionSettings[].isSeat").type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 착석인원 여부"),
                fieldWithPath("personOptionSettings[].isDefault").type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 기본 옵션 여부 (기본 옵션이 true라면 옵션 이름 변경 이나 삭제가 불가능)"),
                fieldWithPath("personOptionSettings[].canModify").type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 노출 여부, 착석 인원 여부 설정 가능 여부"),
                fieldWithPath("personOptionSettings[].additionalOptions").type(JsonFieldType.ARRAY)
                    .description("인원 옵션 설졍 - 부가 옵션").optional(),
                fieldWithPath("personOptionSettings[].additionalOptions[].id").type(
                    JsonFieldType.STRING).description("부가 옵션 UUID"),
                fieldWithPath(
                    "personOptionSettings[].additionalOptions[].name").type(
                    JsonFieldType.STRING).description("부가 옵션 이름"),
                fieldWithPath("personOptionSettings[].additionalOptions[].isDisplayed").type(
                    JsonFieldType.BOOLEAN).description("부가 옵션 노출 여부")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("isUsedPersonOptionSetting").type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 설정 사용 여부"),
                fieldWithPath("personOptionSettings").type(JsonFieldType.ARRAY)
                    .description("인원 옵션 설정"),
                fieldWithPath("personOptionSettings[].id").type(JsonFieldType.STRING)
                    .description("인원 옵션 UUID"),
                fieldWithPath("personOptionSettings[].name").type(JsonFieldType.STRING)
                    .description("인원 옵션 이름"),
                fieldWithPath("personOptionSettings[].isDisplayed").type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 노출 여부"),
                fieldWithPath("personOptionSettings[].isSeat").type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 착석인원 여부"),
                fieldWithPath("personOptionSettings[].isDefault").type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 기본 옵션 여부 (기본 옵션이 true라면 옵션 이름 변경 이나 삭제가 불가능)"),
                fieldWithPath("personOptionSettings[].canModify").type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 노출 여부, 착석 인원 여부 설정 가능 여부"),
                fieldWithPath("personOptionSettings[].additionalOptions").type(JsonFieldType.ARRAY)
                    .description("인원 옵션 설졍 - 부가 옵션").optional(),
                fieldWithPath("personOptionSettings[].additionalOptions[].id").type(
                    JsonFieldType.STRING).description("부가 옵션 UUID"),
                fieldWithPath(
                    "personOptionSettings[].additionalOptions[].name").type(
                    JsonFieldType.STRING).description("부가 옵션 이름"),
                fieldWithPath("personOptionSettings[].additionalOptions[].isDisplayed").type(
                    JsonFieldType.BOOLEAN).description("부가 옵션 노출 여부"),
                fieldWithPath("existsWaitingTeam").type(JsonFieldType.BOOLEAN)
                    .description("웨이팅중인 팀 존재여부 (true-설정수정불가)"),
                fieldWithPath("isOpenedOperation").type(JsonFieldType.BOOLEAN)
                    .description("웨이팅 접수 중 여부 (true-설정수정불가)")
            )
        ));
  }

  private OptionSettingsResponse defaultResponse() {
    OptionSettingsData data = DefaultOptionSettingDataFactory.create();
    List<PersonOptionSetting> personOptionSettings = data.getPersonOptionSettings()
        .stream()
        .map(item -> new PersonOptionSetting(item.getId(), item.getName(),
            item.getIsDisplayed(),
            item.getIsSeat(), item.getIsDefault(), item.getCanModify(),
            getAdditionalOptionsByResponse(item.getAdditionalOptions())))
        .toList();

    return new OptionSettingsResponse(new OptionSettingsVO(true, personOptionSettings), false, false);
  }

  private static List<OptionSettingsVO.AdditionalOption> getAdditionalOptionsByResponse(
      List<OptionSettingsData.AdditionalOption> items) {
    return items.stream()
        .map(item -> new OptionSettingsVO.AdditionalOption(item.getId(),
            item.getName(), item.getIsDisplayed()))
        .toList();
  }


  private OptionSettingsRequest defaultRequest() {
    OptionSettingsData data = DefaultOptionSettingDataFactory.create();
    List<OptionSettingsRequest.PersonOptionSetting> personOptionSettings = data.getPersonOptionSettings()
        .stream()
        .map(item -> new OptionSettingsRequest.PersonOptionSetting(item.getId(),
            item.getName(), item.getIsDisplayed(),
            item.getIsSeat(), item.getIsDefault(), item.getCanModify(),
            getAdditionalOptionsByRequest(item.getAdditionalOptions())))
        .toList();

    return new OptionSettingsRequest(
        data.getIsUsedPersonOptionSetting(), personOptionSettings);
  }


  private static List<OptionSettingsRequest.AdditionalOption> getAdditionalOptionsByRequest(
      final List<OptionSettingsData.AdditionalOption> items) {
    return items.stream()
        .map(item -> new OptionSettingsRequest.AdditionalOption(item.getId(),
            item.getName(), item.getIsDisplayed()))
        .toList();
  }
}
