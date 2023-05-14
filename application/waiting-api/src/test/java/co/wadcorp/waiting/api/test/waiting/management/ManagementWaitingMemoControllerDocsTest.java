package co.wadcorp.waiting.api.test.waiting.management;

import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentRequest;
import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.waiting.api.controller.waiting.management.ManagementWaitingMemoController;
import co.wadcorp.waiting.api.controller.waiting.management.dto.request.WaitingMemoSaveRequest;
import co.wadcorp.waiting.api.service.waiting.management.ManagementWaitingMemoApiService;
import co.wadcorp.waiting.api.service.waiting.management.dto.request.WaitingMemoSaveServiceRequest;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.WaitingMemoSaveResponse;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

class ManagementWaitingMemoControllerDocsTest extends RestDocsSupport {

  private final ManagementWaitingMemoApiService managementWaitingMemoApiService = mock(
      ManagementWaitingMemoApiService.class
  );

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object init() {
    return new ManagementWaitingMemoController(managementWaitingMemoApiService);
  }

  @DisplayName("웨이팅별 메모 저장/수정")
  @Test
  void saveWaitingMemo() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String WAITING_ID = "WAITING_UUID";
    String waitingId = "L4FZN2VUQ6EQQZIBEFDC0G";
    String memo = "단골, 진상, 오이X";
    WaitingMemoSaveRequest request = WaitingMemoSaveRequest.builder()
        .waitingId(waitingId)
        .memo(memo)
        .build();

    WaitingMemoSaveResponse response = WaitingMemoSaveResponse.builder()
        .waitingId(waitingId)
        .memo(memo)
        .build();

    // when
    when(managementWaitingMemoApiService.save(any(), any(WaitingMemoSaveServiceRequest.class)))
        .thenReturn(response);

    // then
    mockMvc.perform(
        post("/api/v1/shops/{shopId}/management/waiting/{waitingId}/memo",
            SHOP_ID, WAITING_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andDo(
            document("waiting-memo-save",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("shopId").description("매장 아이디"),
                    parameterWithName("waitingId").description("웨이팅 아이디")
                ),
                requestFields(
                    fieldWithPath("waitingId").type(JsonFieldType.STRING)
                        .description("웨이팅 아이디"),
                    fieldWithPath("memo").type(JsonFieldType.STRING)
                        .description("웨이팅 메모 내용")
                ),
                responseFields(
                    beneathPath("data").withSubsectionId("data"),
                    fieldWithPath("waitingId").type(JsonFieldType.STRING)
                        .description("웨이팅 아이디"),
                    fieldWithPath("memo").type(JsonFieldType.STRING)
                        .description("웨이팅 메모 내용")
                )
            ));
  }

  @DisplayName("웨이팅별 메모 삭제")
  @Test
  void deleteWaitingMemo() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String WAITING_ID = "WAITING_UUID";

    // then
    mockMvc.perform(
            post("/api/v1/shops/{shopId}/management/waiting/{waitingId}/memo/delete",
                SHOP_ID, WAITING_ID)
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andDo(document("waiting-memo-delete",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장 아이디"),
                parameterWithName("waitingId").description("웨이팅 아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
            )
        ));
  }

}