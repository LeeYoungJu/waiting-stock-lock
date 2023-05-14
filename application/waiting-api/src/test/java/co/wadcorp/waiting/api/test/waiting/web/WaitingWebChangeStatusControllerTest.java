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
import co.wadcorp.waiting.api.controller.waiting.web.WaitingWebChangeStatusController;
import co.wadcorp.waiting.api.service.waiting.web.WaitingChangeStatusWebService;
import co.wadcorp.waiting.api.service.waiting.web.WebCancelResponse;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

public class WaitingWebChangeStatusControllerTest extends RestDocsSupport {

  private final WaitingChangeStatusWebService waitingChangeStatusWebService = mock(
      WaitingChangeStatusWebService.class);

  @Override
  public Object init() {
    return new WaitingWebChangeStatusController(waitingChangeStatusWebService);
  }


  @Test
  @DisplayName("웨이팅_웹_취소")
  public void cancelWaitingWebTest() throws Exception {
    // given
    final String WAITING_ID = "WAITING_UUID";
    WebCancelResponse response = WebCancelResponse.builder()
        .waitingId(UUIDUtil.shortUUID())
        .shopName("매장이름")
        .shopAddress("서울특별시")
        .shopTelNumber("010-1234-5678")
        .waitingStatus(WaitingStatus.WAITING)
        .waitingDetailStatus(WaitingDetailStatus.WAITING)
        .build();

    // when
    when(waitingChangeStatusWebService.cancel(any(), any(LocalDate.class))).thenReturn(response);

    mockMvc.perform(RestDocumentationRequestBuilders.post("/web/v1/waiting/{waitingId}/cancel", WAITING_ID)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("web-waiting-cancel",
            getDocumentResponse(),
            pathParameters(
                parameterWithName("waitingId").description("웨이팅 아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("waitingId").type(JsonFieldType.STRING).description("웨이팅 아이디"),
                fieldWithPath("shopName").type(JsonFieldType.STRING).description("매장 이름"),
                fieldWithPath("shopAddress").type(JsonFieldType.STRING).description("매장 매장 주소"),
                fieldWithPath("shopTelNumber").type(JsonFieldType.STRING).description("매장 연락처"),
                fieldWithPath("waitingStatus").type(JsonFieldType.STRING).description("웨이팅 상태"),
                fieldWithPath("waitingDetailStatus").type(JsonFieldType.STRING).description("웨이팅 상세 상태")
            ))
        );
  }
  @Test
  @DisplayName("웨이팅_웹_미루기")
  public void putOffWaitingWebTest() throws Exception {
    // given
    final String WAITING_ID = "WAITING_UUID";

    // when
    waitingChangeStatusWebService.putOff(any(), any(LocalDate.class));

    mockMvc.perform(RestDocumentationRequestBuilders.post("/web/v1/waiting/{waitingId}/put-off", WAITING_ID)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("web-waiting-put-off",
            getDocumentResponse(),
            pathParameters(
                parameterWithName("waitingId").description("웨이팅 아이디")
            )));
  }

}
