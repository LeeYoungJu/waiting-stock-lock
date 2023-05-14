package co.wadcorp.waiting.api.test.waiting.web;

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
import co.wadcorp.waiting.api.controller.waiting.web.WaitingUndoWebController;
import co.wadcorp.waiting.api.model.waiting.response.WaitingCanUndoListResponse;
import co.wadcorp.waiting.api.model.waiting.vo.CanUndoWaitingVO;
import co.wadcorp.waiting.api.service.waiting.WaitingUndoValidateApiService;
import co.wadcorp.waiting.api.service.waiting.web.WaitingCanUndoListWebService;
import co.wadcorp.waiting.api.service.waiting.web.WaitingChangeStatusWebService;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.data.domain.waiting.RegisterChannel;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

class WaitingUndoWebControllerTest extends RestDocsSupport {

  private final WaitingChangeStatusWebService waitingChangeStatusWebService = mock(
      WaitingChangeStatusWebService.class);
  private final WaitingCanUndoListWebService waitingCanUndoListWebService = mock(
      WaitingCanUndoListWebService.class);
  private final WaitingUndoValidateApiService waitingUndoValidateApiService = mock(
      WaitingUndoValidateApiService.class);

  @Override
  public Object init() {
    return new WaitingUndoWebController(
        waitingChangeStatusWebService, waitingCanUndoListWebService, waitingUndoValidateApiService
    );
  }

  @Test
  @DisplayName("웨이팅 웹 되돌리기 가능 목록")
  public void undoWaitingWebList() throws Exception {
    // given
    final String WAITING_ID = "WAITING_UUID";

    WaitingCanUndoListResponse response = WaitingCanUndoListResponse.builder()
        .canUndoDateTime(ZonedDateTimeUtils.nowOfSeoul())
        .waitingList(List.of(
            CanUndoWaitingVO.builder()
                .shopName("매장1")
                .waitingId(UUIDUtil.shortUUID())
                .registerChannel(RegisterChannel.WAITING_APP)
                .waitingNumber(101)
                .waitingOrder(2)
                .expectedWaitingPeriod(10)
                .seatOptionName("착석")
                .regDateTime(ZonedDateTimeUtils.nowOfSeoul())
                .canUndo(true)
                .build(),

            CanUndoWaitingVO.builder()
                .shopName("매장2")
                .waitingId(UUIDUtil.shortUUID())
                .registerChannel(RegisterChannel.WAITING_APP)
                .waitingNumber(101)
                .waitingOrder(2)
                .expectedWaitingPeriod(10)
                .seatOptionName("착석")
                .regDateTime(ZonedDateTimeUtils.nowOfSeoul())
                .canUndo(false)
                .build()

        ))
        .build();

    // when
    when(waitingCanUndoListWebService.getCanUndoList(any(), any())).thenReturn(response);

    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/web/v1/waiting/{waitingId}/undo-waiting", WAITING_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("web-waiting-undo-waiting",
            getDocumentResponse(),
            pathParameters(
                parameterWithName("waitingId").description("웨이팅 아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("canUndoDateTime").type(JsonFieldType.STRING)
                    .description("되돌리기 가능 시간").optional(),
                fieldWithPath("waitingList").type(JsonFieldType.ARRAY).description("되돌리기 웨이팅 목록"),
                fieldWithPath("waitingList[].shopName").type(JsonFieldType.STRING)
                    .description("매장 이름"),
                fieldWithPath("waitingList[].waitingId").type(JsonFieldType.STRING)
                    .description("웨이팅 아이디"),
                fieldWithPath("waitingList[].registerChannel").type(JsonFieldType.STRING)
                    .description("등록 채널 - WAITING_APP(현장), WAITING_MANAGER(수기), CATCH_APP(캐치테이블)"),
                fieldWithPath("waitingList[].waitingNumber").type(JsonFieldType.NUMBER)
                    .description("채번"),
                fieldWithPath("waitingList[].waitingOrder").type(JsonFieldType.NUMBER)
                    .description("취소 전 순서"),
                fieldWithPath("waitingList[].expectedWaitingPeriod").type(JsonFieldType.NUMBER)
                    .description("취소 전 남은 시간"),
                fieldWithPath("waitingList[].maxExpressionWaitingPeriod").type(JsonFieldType.NUMBER)
                    .description("최대 표현 가능한 웨이팅 시간"),
                fieldWithPath("waitingList[].seatOptionName").type(JsonFieldType.STRING)
                    .description("좌석 이름"),
                fieldWithPath("waitingList[].regDateTime").type(JsonFieldType.STRING)
                    .description("웨이팅  등록 시간"),
                fieldWithPath("waitingList[].canUndo").type(JsonFieldType.BOOLEAN)
                    .description("되돌리기 가능 여부")
            ))
        );
  }

  @Test
  @DisplayName("웨이팅 웹 되돌리기")
  public void undoWaitingWebTest() throws Exception {
    // given
    final String WAITING_ID = "WAITING_UUID";

    // when
    waitingChangeStatusWebService.undo(any(), any(LocalDate.class));

    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/web/v1/waiting/{waitingId}/undo", WAITING_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("web-waiting-undo",
            getDocumentResponse(),
            pathParameters(
                parameterWithName("waitingId").description("웨이팅 아이디")
            )));
  }

}