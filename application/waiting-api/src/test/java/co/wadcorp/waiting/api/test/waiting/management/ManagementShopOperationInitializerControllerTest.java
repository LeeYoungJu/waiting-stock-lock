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

import co.wadcorp.waiting.api.controller.waiting.management.ManagementShopOperationInitializerController;
import co.wadcorp.waiting.api.model.waiting.response.ShopOperationInitializerResponse;
import co.wadcorp.waiting.data.domain.shop.operation.status.OperationStatus;
import co.wadcorp.waiting.api.service.waiting.ShopOperationInitializerApiService;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

class ManagementShopOperationInitializerControllerTest extends RestDocsSupport {

  private final ShopOperationInitializerApiService service = mock(ShopOperationInitializerApiService.class);

  @Override
  public Object init() {
    return new ManagementShopOperationInitializerController(service);
  }

  @Test
  @DisplayName("웨이팅 영업일 초기화")
  void initialize() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String ctmAuth = "Bearer accessToken";

    LocalDate operationDate = LocalDate.now();

    ShopOperationInitializerResponse response = ShopOperationInitializerResponse.builder()
        .shopId(SHOP_ID)
        .operationDate(operationDate)
        .operationStatus(OperationStatus.CLOSED)
        .operationStartDateTime(ZonedDateTimeUtils.nowOfSeoul())
        .operationEndDateTime(null)
        .build();

    // when
    when(service.initializer(any(), any(LocalDate.class), any())).thenReturn(response);

    mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/shops/{shopId}/management/operation/init", SHOP_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-CTM-AUTH", ctmAuth))
        .andExpect(status().isOk())
        .andDo(document("management-waiting-initializer",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("shopId").type(JsonFieldType.STRING).description("매장아이디"),
                fieldWithPath("operationDate").type(JsonFieldType.STRING).description("영업일"),
                fieldWithPath("operationStatus").type(JsonFieldType.STRING).description("운영 상태"),
                fieldWithPath("operationStartDateTime").type(JsonFieldType.STRING).description("운영 시작일").optional(),
                fieldWithPath("operationEndDateTime").type(JsonFieldType.STRING).description("운영 종료일").optional()
            )
        ));
  }
}