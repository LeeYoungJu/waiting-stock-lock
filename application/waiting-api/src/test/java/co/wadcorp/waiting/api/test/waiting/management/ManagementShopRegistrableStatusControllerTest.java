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

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.controller.waiting.management.ManagementShopOperationStatusController;
import co.wadcorp.waiting.api.model.waiting.request.ChangeShopOperationStatusRequest;
import co.wadcorp.waiting.data.domain.shop.operation.status.OperationStatus;
import co.wadcorp.waiting.api.service.waiting.ShopOperationApiService;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

class ManagementShopRegistrableStatusControllerTest extends RestDocsSupport {

  private final ShopOperationApiService service = mock(ShopOperationApiService.class);
  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @Override
  public Object init() {
    return new ManagementShopOperationStatusController(service);
  }

  @Test
  @DisplayName("웨이팅 운영 상태 변경")
  void initialize() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";

    ChangeShopOperationStatusRequest request = new ChangeShopOperationStatusRequest(
        OperationStatus.PAUSE, UUIDUtil.shortUUID(), 120);

    // when
    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/management/operation/change-status",
                    SHOP_ID)
                .header("X-REQUEST-ID", "deviceId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("management-waiting-operation-change-status",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            requestFields(
                fieldWithPath("operationStatus").type(JsonFieldType.STRING)
                    .description("운영 상태 - OPEN(영업 중), BY_PASS(바로 입장), PAUSE(일시 중지), CLOSED(영업 종료)"),
                fieldWithPath("pauseReasonId").type(JsonFieldType.STRING)
                    .description("일시 중지 사유 (일시 중지에서만 사용)").optional(),
                fieldWithPath("pausePeriod").type(JsonFieldType.NUMBER).description("일시 중지 시간 - 범위 10분 ~ 180분, 미정 - -1 (일시 중지에서만 사용)").optional()
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
            )
        ));
  }
}