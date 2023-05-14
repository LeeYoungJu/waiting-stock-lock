package co.wadcorp.waiting.api.test.waiting.register;

import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentRequest;
import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentResponse;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.controller.waiting.register.RegisterWaitingChangeStatusController;
import co.wadcorp.waiting.api.model.waiting.request.CancelWaitingRequest;
import co.wadcorp.waiting.api.service.waiting.WaitingRegisterApiService;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

public class RegisterWaitingChangeStatusControllerTest extends RestDocsSupport {

  private final WaitingRegisterApiService waitingRegisterApiService = mock(WaitingRegisterApiService.class);

  @Override
  public Object init() {
    return new RegisterWaitingChangeStatusController(waitingRegisterApiService);
  }
  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  @DisplayName("웨이팅_다중_취소")
  public void cancelWaitingTest() throws Exception {
    // given
    final String shopId = "SHOP_UUID";
    CancelWaitingRequest request = CancelWaitingRequest.builder()
        .waitingIdList(List.of(UUIDUtil.shortUUID(), UUIDUtil.shortUUID()))
        .build();

    // when
    mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/register/waiting/cancel", shopId)
            .header("X-REQUEST-ID", "deviceId")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("waiting-register-cancel",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            requestFields(
                fieldWithPath("waitingIdList[]").type(JsonFieldType.ARRAY).description("웨이팅 아이디 리스트")
            )));
  }
}
