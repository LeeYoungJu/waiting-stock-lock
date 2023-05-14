package co.wadcorp.waiting.api.test.waiting.management;

import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentRequest;
import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentResponse;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.waiting.api.controller.waiting.management.ManagementWaitingChangeStatusController;
import co.wadcorp.waiting.api.model.waiting.request.CancelWaitingByManagementRequest;
import co.wadcorp.waiting.api.model.waiting.vo.CancelReason;
import co.wadcorp.waiting.api.service.waiting.management.ManagementWaitingApiService;
import co.wadcorp.waiting.api.service.waiting.WaitingUndoValidateApiService;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

class ManagementWaitingChangeStatusControllerTest extends RestDocsSupport {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final ManagementWaitingApiService managementWaitingApiService = mock(
      ManagementWaitingApiService.class);
  private final WaitingUndoValidateApiService waitingUndoValidateApiService = mock(
      WaitingUndoValidateApiService.class);

  @Override
  public Object init() {
    return new ManagementWaitingChangeStatusController(managementWaitingApiService, waitingUndoValidateApiService);
  }


  @Test
  @DisplayName("웨이팅 관리 - 호출")
  void call() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String WAITING_ID = "WAITING_ID";

    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/management/waiting/{waitingId}/call", SHOP_ID, WAITING_ID)
                .header("X-REQUEST-ID", "deviceId")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("management-waiting-call",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디"),
                parameterWithName("waitingId").description("웨이팅 아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
            )
        ));
  }

  @Test
  @DisplayName("웨이팅 관리 - 착석")
  void sitting() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String WAITING_ID = "WAITING_ID";

    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/management/waiting/{waitingId}/sitting", SHOP_ID, WAITING_ID)
                .header("X-REQUEST-ID", "deviceId")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("management-waiting-sitting",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디"),
                parameterWithName("waitingId").description("웨이팅 아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
            )
        ));
  }

  @Test
  @DisplayName("웨이팅 관리 - 취소")
  void cancel() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String WAITING_ID = "WAITING_ID";

    CancelWaitingByManagementRequest request = new CancelWaitingByManagementRequest(CancelReason.SHOP_REASON);

    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/management/waiting/{waitingId}/cancel", SHOP_ID, WAITING_ID)
                .header("X-REQUEST-ID", "deviceId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("management-waiting-cancel",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디"),
                parameterWithName("waitingId").description("웨이팅 아이디")
            ),
            requestFields(
                fieldWithPath("cancelReason").type(JsonFieldType.STRING).description("웨이팅 취소사유: CUSTOMER_REASON(고객사유), SHOP_REASON(매장사유), NO_SHOW(노쇼), OUT_OF_STOCK_REASON(재고소진)")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
            )
        ));
  }


  @Test
  @DisplayName("웨이팅 관리 - 되돌리기 예외상황 확인")
  void validateUndo() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String WAITING_ID = "WAITING_ID";

    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/management/waiting/{waitingId}/undo/validation", SHOP_ID, WAITING_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("management-waiting-undo-validation",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디"),
                parameterWithName("waitingId").description("웨이팅 아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
            )
        ));
  }


  @Test
  @DisplayName("웨이팅 관리 - 되돌리기")
  void undo() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String WAITING_ID = "WAITING_ID";

    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/management/waiting/{waitingId}/undo", SHOP_ID, WAITING_ID)
                .header("X-REQUEST-ID", "deviceId")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("management-waiting-undo",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디"),
                parameterWithName("waitingId").description("웨이팅 아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
            )
        ));
  }
}