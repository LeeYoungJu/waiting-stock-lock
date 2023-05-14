package co.wadcorp.waiting.api.test.internal.table;

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
import co.wadcorp.waiting.api.internal.controller.table.RemoteTableController;
import co.wadcorp.waiting.api.internal.service.table.RemoteTableApiService;
import co.wadcorp.waiting.api.internal.service.table.dto.response.RemoteTableSettingResponse;
import co.wadcorp.waiting.api.internal.service.table.dto.response.RemoteTableSettingResponse.ModeSettingsVO;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.data.enums.WaitingModeType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

class RemoteTableControllerDocsTest extends RestDocsSupport {

  private final RemoteTableApiService remoteTableApiService = mock(RemoteTableApiService.class);

  @Override
  public Object init() {
    return new RemoteTableController(remoteTableApiService);
  }

  @DisplayName("원격 웨이팅 테이블 정보 목록")
  @Test
  void getTableSettingsTest() throws Exception {
    // given
    List<RemoteTableSettingResponse> response = List.of(RemoteTableSettingResponse.builder()
        .shopId(1L)
        .waitingModeType(WaitingModeType.DEFAULT)
        .defaultModeSettings(ModeSettingsVO.builder()
            .id(UUIDUtil.shortUUID())
            .name("착석")
            .minSeatCount(1)
            .maxSeatCount(4)
            .expectedWaitingPeriod(10)
            .isUsedExpectedWaitingPeriod(true)
            .isTakeOut(false)
            .build()
        )
        .tableModeSettings(List.of(
            ModeSettingsVO.builder()
                .id(UUIDUtil.shortUUID())
                .name("테이블1")
                .minSeatCount(1)
                .maxSeatCount(4)
                .expectedWaitingPeriod(10)
                .isUsedExpectedWaitingPeriod(true)
                .isTakeOut(false)
                .build(),
            ModeSettingsVO.builder()
                .id(UUIDUtil.shortUUID())
                .name("테이블2")
                .minSeatCount(1)
                .maxSeatCount(4)
                .expectedWaitingPeriod(10)
                .isUsedExpectedWaitingPeriod(true)
                .isTakeOut(false)
                .build()
        ))
        .build()
    );

    // when
    when(remoteTableApiService.findTableSettings(any()))
        .thenReturn(response);

    // then
    mockMvc.perform(get("/internal/api/v1/shops/{shopIds}/tables", "CSV_SHOP_IDS")
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
        )
        .andExpect(status().isOk())
        .andDo(document("remote-table-settings",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopIds").description("B2C의 shopSeq CSV (1,2,3)")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("shopId").type(JsonFieldType.NUMBER)
                    .description("B2C의 shopSeq"),
                fieldWithPath("waitingModeType").type(JsonFieldType.STRING)
                    .description("테이블 모드 정보 (DEFAULT, TABLE)"),
                fieldWithPath("defaultModeSettings").type(JsonFieldType.OBJECT)
                    .description("기본모드(DEFAULT)에서 사용하는 테이블 정보"),
                fieldWithPath("defaultModeSettings.id").type(JsonFieldType.STRING)
                    .description("테이블 ID"),
                fieldWithPath("defaultModeSettings.name").type(JsonFieldType.STRING)
                    .description("테이블 이름"),
                fieldWithPath("defaultModeSettings.minSeatCount").type(JsonFieldType.NUMBER)
                    .description("테이블 최소 착석 수"),
                fieldWithPath("defaultModeSettings.maxSeatCount").type(JsonFieldType.NUMBER)
                    .description("테이블 최대 착석 수"),
                fieldWithPath("defaultModeSettings.expectedWaitingPeriod")
                    .type(JsonFieldType.NUMBER)
                    .description("예상 대기 시간 (팀당 시간, 분 단위)"),
                fieldWithPath("defaultModeSettings.isUsedExpectedWaitingPeriod")
                    .type(JsonFieldType.BOOLEAN)
                    .description("예상 대기 시간 사용 여부"),
                fieldWithPath("defaultModeSettings.isTakeOut").type(JsonFieldType.BOOLEAN)
                    .description("포장 여부"),
                fieldWithPath("tableModeSettings").type(JsonFieldType.ARRAY)
                    .description("테이블 모드(TABLE)에서 사용하는 테이블 정보"),
                fieldWithPath("tableModeSettings[].id").type(JsonFieldType.STRING)
                    .description("테이블 ID"),
                fieldWithPath("tableModeSettings[].name").type(JsonFieldType.STRING)
                    .description("테이블 이름"),
                fieldWithPath("tableModeSettings[].minSeatCount").type(JsonFieldType.NUMBER)
                    .description("테이블 최소 착석 수"),
                fieldWithPath("tableModeSettings[].maxSeatCount").type(JsonFieldType.NUMBER)
                    .description("테이블 최대 착석 수"),
                fieldWithPath("tableModeSettings[].expectedWaitingPeriod")
                    .type(JsonFieldType.NUMBER)
                    .description("예상 대기 시간 (팀당 시간, 분 단위)"),
                fieldWithPath("tableModeSettings[].isUsedExpectedWaitingPeriod")
                    .type(JsonFieldType.BOOLEAN)
                    .description("예상 대기 시간 사용 여부"),
                fieldWithPath("tableModeSettings[].isTakeOut").type(JsonFieldType.BOOLEAN)
                    .description("포장 여부")
            )
        ));
  }

}
