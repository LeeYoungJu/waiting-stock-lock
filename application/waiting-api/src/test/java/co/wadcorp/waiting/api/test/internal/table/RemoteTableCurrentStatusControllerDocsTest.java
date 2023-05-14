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
import co.wadcorp.waiting.api.internal.controller.table.RemoteTableCurrentStatusController;
import co.wadcorp.waiting.api.internal.service.table.RemoteTableCurrentStatusApiService;
import co.wadcorp.waiting.api.internal.service.table.dto.response.RemoteTableStatusResponse;
import co.wadcorp.waiting.api.internal.service.table.dto.response.RemoteTableStatusResponse.TableCurrentStatusVO;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

class RemoteTableCurrentStatusControllerDocsTest extends RestDocsSupport {

  private final RemoteTableCurrentStatusApiService remoteTableCurrentStatusApiService = mock(
      RemoteTableCurrentStatusApiService.class);

  @Override
  public Object init() {
    return new RemoteTableCurrentStatusController(remoteTableCurrentStatusApiService);
  }

  @DisplayName("원격 웨이팅 테이블 현황")
  @Test
  void getTableStatusTest() throws Exception {
    // given
    List<RemoteTableStatusResponse> response = List.of(RemoteTableStatusResponse.builder()
        .shopId(1L)
        .totalTeamCount(2)
        .currentStatus(List.of(
            TableCurrentStatusVO.builder()
                .tableId(UUIDUtil.shortUUID())
                .tableName("홀")
                .teamCount(3)
                .expectedWaitingTime(10)
                .isUsedExpectedWaitingPeriod(false)
                .isTakeOut(false)
                .build(),
            TableCurrentStatusVO.builder()
                .tableId(UUIDUtil.shortUUID())
                .tableName("룸")
                .teamCount(2)
                .expectedWaitingTime(10)
                .isUsedExpectedWaitingPeriod(false)
                .isTakeOut(false)
                .build()
        ))
        .build()
    );

    // when
    when(remoteTableCurrentStatusApiService.findTableCurrentStatus(any(), any(LocalDate.class)))
        .thenReturn(response);

    // then
    mockMvc.perform(get("/internal/api/v1/shops/{shopIds}/current-status", "CSV_SHOP_IDS")
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andDo(document("remote-table-status",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopIds").description("B2C의 shopSeq CSV (1,2,3)")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("shopId").type(JsonFieldType.NUMBER)
                    .description("B2C의 shopSeq"),
                fieldWithPath("totalTeamCount").type(JsonFieldType.NUMBER)
                    .description("매장 총 팀 수"),
                fieldWithPath("tableCurrentStatus[].tableId").type(JsonFieldType.STRING)
                    .description("테이블 ID"),
                fieldWithPath("tableCurrentStatus[].tableName").type(JsonFieldType.STRING)
                    .description("테이블 이름"),
                fieldWithPath("tableCurrentStatus[].teamCount").type(JsonFieldType.NUMBER)
                    .description("테이블 총 팀수"),
                fieldWithPath("tableCurrentStatus[].isUsedExpectedWaitingPeriod").type(JsonFieldType.BOOLEAN)
                    .description("테이블별 예상 웨이팅 시간 사용여부"),
                fieldWithPath("tableCurrentStatus[].expectedWaitingTime").type(JsonFieldType.NUMBER)
                    .optional()
                    .description("테이블 예상 대기 시간"),
                fieldWithPath("tableCurrentStatus[].isTakeOut").type(JsonFieldType.BOOLEAN)
                    .description("포장 여부")
            )
        ));
  }

}
