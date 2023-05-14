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

import co.wadcorp.libs.datetime.ISO8601;
import co.wadcorp.waiting.api.controller.waiting.management.ManagementWaitingHistoryController;
import co.wadcorp.waiting.api.model.waiting.response.WaitingHistoriesResponse;
import co.wadcorp.waiting.api.model.waiting.response.WaitingHistoriesResponse.Waiting;
import co.wadcorp.waiting.api.model.waiting.response.WaitingHistoriesResponse.WaitingHistory;
import co.wadcorp.waiting.api.service.waiting.WaitingHistoryApiService;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.data.domain.waiting.RegisterChannel;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

public class ManagementWaitingHistoryControllerTest extends RestDocsSupport {

  private final WaitingHistoryApiService service = mock(WaitingHistoryApiService.class);

  @Override
  public Object init() {
    return new ManagementWaitingHistoryController(service);
  }

  @Test
  @DisplayName("웨이팅 관리 - 이력 보기")
  void waitingHistories() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String WAITING_ID = "WAITING_ID";

    LocalDate operationDate = LocalDate.now();
    boolean isSendAlimtalk = true;

    Waiting waiting = new Waiting(WAITING_ID, SHOP_ID, RegisterChannel.WAITING_APP, operationDate.toString(), 901, "010-1234-5678", "아무개", 4,
        9, "성인5/유아4/홀");

    List<WaitingHistory> histories = List.of(
        new WaitingHistory(WaitingStatus.WAITING.name(), WaitingStatus.WAITING.getValue(), WaitingDetailStatus.WAITING.name(), WaitingDetailStatus.WAITING.getValue(),
            isSendAlimtalk, ISO8601.format(ZonedDateTimeUtils.nowOfSeoul())),
        new WaitingHistory(WaitingStatus.WAITING.name(), WaitingStatus.WAITING.getValue(), WaitingDetailStatus.CALL.name(), WaitingDetailStatus.CALL.getValue(),
            isSendAlimtalk, ISO8601.format(ZonedDateTimeUtils.nowOfSeoul())),
        new WaitingHistory(WaitingStatus.SITTING.name(), WaitingStatus.SITTING.getValue(), WaitingDetailStatus.SITTING.name(), WaitingDetailStatus.SITTING.getValue(),
            isSendAlimtalk, ISO8601.format(ZonedDateTimeUtils.nowOfSeoul()))
    );

    WaitingHistoriesResponse response = new WaitingHistoriesResponse(waiting, histories);

    // when
    when(service.getWaitingHistories(any(), any())).thenReturn(response);

    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/management/waiting/{waitingId}/histories", SHOP_ID, WAITING_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("management-waiting-histories",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디"),
                parameterWithName("waitingId").description("웨이팅 아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("waitingId").type(JsonFieldType.STRING).description("웨이팅 아이디"),
                fieldWithPath("shopId").type(JsonFieldType.STRING).description("매장 아이디"),
                fieldWithPath("registerChannelText").type(JsonFieldType.STRING).description("등록 체널 텍스트 - 현장, 수기, 원격"),
                fieldWithPath("operationDate").type(JsonFieldType.STRING).description("영업일"),
                fieldWithPath("waitingNumber").type(JsonFieldType.NUMBER).description("웨이팅 번호"),
                fieldWithPath("customerPhone").type(JsonFieldType.STRING).description("고객 연락처").optional(),
                fieldWithPath("customerName").type(JsonFieldType.STRING).description("고객 이름").optional(),
                fieldWithPath("visitCount").type(JsonFieldType.NUMBER).description("방문 횟수"),
                fieldWithPath("totalPersonCount").type(JsonFieldType.NUMBER).description("총 인원").optional(),
                fieldWithPath("personOptionText").type(JsonFieldType.STRING).description("인원 옵션 설정 정보 - 텍스트"),
                fieldWithPath("waitingHistories").type(JsonFieldType.ARRAY).description("웨이팅 히스토리 내역"),
                fieldWithPath("waitingHistories[].waitingStatus").type(JsonFieldType.STRING).description("웨이팅 상태 - WAITING, SITTING, CANCEL"),
                fieldWithPath("waitingHistories[].waitingStatusText").type(JsonFieldType.STRING).description("웨이팅 상태 값 - 웨이팅중, 착석, 취소"),
                fieldWithPath("waitingHistories[].waitingDetailStatus").type(JsonFieldType.STRING).description("웨이팅 상세 상태"),
                fieldWithPath("waitingHistories[].waitingDetailStatusText").type(JsonFieldType.STRING).description("웨이팅 상세 상태 값"),
                fieldWithPath("waitingHistories[].isSendAlimtalk").type(JsonFieldType.BOOLEAN).description("알림톡 발송 상태"),
                fieldWithPath("waitingHistories[].regDateTime").type(JsonFieldType.STRING).description("등록일")
            )
        ));
  }
}
