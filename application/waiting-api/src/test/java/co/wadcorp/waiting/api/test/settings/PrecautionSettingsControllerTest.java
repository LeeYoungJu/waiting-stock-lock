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

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.controller.settings.PrecautionSettingsController;
import co.wadcorp.waiting.api.model.settings.request.PrecautionSettingsRequest;
import co.wadcorp.waiting.api.model.settings.response.PrecautionSettingsResponse;
import co.wadcorp.waiting.api.model.settings.vo.PrecautionVO;
import co.wadcorp.waiting.api.service.settings.PrecautionSettingsApiService;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

public class PrecautionSettingsControllerTest extends RestDocsSupport {

  private final PrecautionSettingsApiService precautionSettingsApiService = mock(PrecautionSettingsApiService.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object init() {
    return new PrecautionSettingsController(precautionSettingsApiService);
  }

  @Test
  @DisplayName("웨이팅_홈_설정_조회")
  public void getHomeSettingsTest() throws Exception {

    // given
    final String SHOP_ID = "SHOP_UUID";

    List<PrecautionVO> precautions = getPrecautions();
    PrecautionSettingsResponse response = PrecautionSettingsResponse.builder()
        .messagePrecaution("알림톡 메시지 공지사항")
        .isUsedPrecautions(true)
        .precautions(precautions)
        .build();

    // when
    when(precautionSettingsApiService.getPrecautionSettings(any())).thenReturn(response);

    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/settings/waiting-precaution",
                    SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("precaution-settings-get",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("messagePrecaution").type(JsonFieldType.STRING)
                    .description("알림톡 내 유의사항 내용"),
                fieldWithPath("isUsedPrecautions").type(JsonFieldType.BOOLEAN)
                    .description("유의사항 사용 여부"),
                fieldWithPath("precautions").type(JsonFieldType.ARRAY).description("유의사항"),
                fieldWithPath("precautions[].id").type(JsonFieldType.STRING).description("유의사항 아이디"),
                fieldWithPath("precautions[].content").type(JsonFieldType.STRING).description("유의사항 내용")
            )
        ));
  }

  @Test
  @DisplayName("웨이팅_홈_설정_등록")
  public void saveHomeSettingsTest() throws Exception {

    // given
    final String SHOP_ID = "SHOP_UUID";
    List<PrecautionVO> precautions = getPrecautions();
    PrecautionSettingsRequest request = PrecautionSettingsRequest.builder()
        .messagePrecaution("알림톡 메시지 공지사항")
        .isUsedPrecautions(true)
        .precautions(precautions)
        .build();

    PrecautionSettingsResponse response = PrecautionSettingsResponse.builder()
        .messagePrecaution("알림톡 메시지 공지사항")
        .isUsedPrecautions(true)
        .precautions(precautions)
        .build();

    // when
    when(precautionSettingsApiService.savePrecautionSettings(any(), any(), any())).thenReturn(response);

    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/settings/waiting-precaution",
                    SHOP_ID)
                .header("X-REQUEST-ID", "deviceId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("precaution-settings-post",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            requestFields(
                fieldWithPath("messagePrecaution").type(JsonFieldType.STRING)
                    .description("알림톡 내 유의사항 내용"),
                fieldWithPath("isUsedPrecautions").type(JsonFieldType.BOOLEAN)
                    .description("유의사항 사용 여부"),
                fieldWithPath("precautions").type(JsonFieldType.ARRAY).description("유의사항"),
                fieldWithPath("precautions[].id").type(JsonFieldType.STRING).description("유의사항 아이디"),
                fieldWithPath("precautions[].content").type(JsonFieldType.STRING).description("유의사항 내용")

            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("messagePrecaution").type(JsonFieldType.STRING)
                    .description("알림톡 내 유의사항 내용"),
                fieldWithPath("isUsedPrecautions").type(JsonFieldType.BOOLEAN)
                    .description("유의사항 사용 여부"),
                fieldWithPath("precautions").type(JsonFieldType.ARRAY).description("유의사항"),
                fieldWithPath("precautions[].id").type(JsonFieldType.STRING).description("유의사항 아이디"),
                fieldWithPath("precautions[].content").type(JsonFieldType.STRING).description("유의사항 내용")
            )
        ));
  }


  private static List<PrecautionVO> getPrecautions() {
    return List.of(
        PrecautionVO.builder()
            .id(UUIDUtil.shortUUID())
            .content("유의사항 첫번째").build(),
        PrecautionVO.builder()
            .id(UUIDUtil.shortUUID())
            .content("유의사항 두번째").build()
    );
  }

}
