package co.wadcorp.waiting.api.test.internal.errordocs;

import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentRequest;
import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.waiting.api.internal.controller.errordocs.RemoteWaitingOrderErrorDocsController;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

public class RemoteWaitingOrderControllerErrorDocsTest extends RestDocsSupport {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object init() {
    return new RemoteWaitingOrderErrorDocsController();
  }

  @DisplayName("원격 웨이팅 선주문 메뉴 검증 실패 - 공통")
  @Test
  void checkOrderMenuInvalid() throws Exception {
    mockMvc.perform(
            get("/docs/internal/api/v1/orders/validation/error-common")
        )
        .andExpect(status().isBadRequest())
        .andDo(
            document("remote-waiting-order-menu-validate-error-common",
                getDocumentRequest(),
                getDocumentResponse(),
                responseFields(
                    beneathPath("data").withSubsectionId("data"),
                    fieldWithPath("reason").type(JsonFieldType.STRING)
                        .description("검증실패 이유")
                )
            ));
  }

  @DisplayName("원격 웨이팅 선주문 메뉴 검증 실패 - 재고소진")
  @Test
  void checkOrderMenuOutOfStock() throws Exception {

    mockMvc.perform(
            post("/docs/internal/api/v1/orders/validation/outofstock")
        )
        .andExpect(status().isBadRequest())
        .andDo(
            document("remote-waiting-order-menu-validate-outofstock",
                getDocumentRequest(),
                getDocumentResponse(),
                responseFields(
                    beneathPath("data").withSubsectionId("data"),
                    fieldWithPath("reason").type(JsonFieldType.STRING)
                        .description("검증실패 이유"),
                    fieldWithPath("menus[]").type(JsonFieldType.ARRAY)
                        .description("검증실패 메뉴 목록"),
                    fieldWithPath("menus[].id").type(JsonFieldType.STRING)
                        .description("검증실패 메뉴 - 아이디"),
                    fieldWithPath("menus[].name").type(JsonFieldType.STRING)
                        .description("검증실패 메뉴 - 이름"),
                    fieldWithPath("menus[].quantity").type(JsonFieldType.NUMBER)
                        .description("검증실패 메뉴 - 주문수량"),
                    fieldWithPath("menus[].remainingQuantity").type(JsonFieldType.NUMBER)
                        .description("검증실패 메뉴 - 남은 재고"),
                    fieldWithPath("menus[].isOutOfStock").type(JsonFieldType.BOOLEAN)
                        .description("검증실패 메뉴 - 품절 여부")
                )
            ));
  }
}
