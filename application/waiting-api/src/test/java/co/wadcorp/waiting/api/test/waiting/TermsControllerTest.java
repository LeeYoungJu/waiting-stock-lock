package co.wadcorp.waiting.api.test.waiting;

import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.waiting.api.controller.TermsController;
import co.wadcorp.waiting.api.model.waiting.response.TermsResponse;
import co.wadcorp.waiting.api.service.waiting.WaitingTermsApiService;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import co.wadcorp.waiting.data.service.customer.TermsService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

public class TermsControllerTest extends RestDocsSupport {

  private final WaitingTermsApiService waitingTermsApiService = mock(WaitingTermsApiService.class);

  @Override
  public Object init() {
    return new TermsController(waitingTermsApiService);
  }

  @Test
  @DisplayName("웨이팅_이용약관_목록_조회")
  public void getAllWaitingTermsTest() throws Exception {
    // given
    List<TermsResponse> response = List.of(
        TermsResponse.builder()
            .seq(1)
            .termsSubject("서비스 이용약관 동의")
            .termsContent("서비스 이용약관 동의 설명입니다.")
            .termsUrl("https://catchtable-waiting.co.kr/terms")
            .isRequired(true)
            .isMarketing(false)
            .build()
    );

    // when
    when(waitingTermsApiService.getAllWaitingTerms()).thenReturn(response);

    mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/waiting/terms")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("waiting-terms-list",
            getDocumentResponse(),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("seq").type(JsonFieldType.NUMBER).description("웨이팅 이용약관 시퀀스"),
                fieldWithPath("termsSubject").type(JsonFieldType.STRING).description("이용약관 제목"),
                fieldWithPath("termsContent").type(JsonFieldType.STRING).description("이용약관 내용"),
                fieldWithPath("termsUrl").type(JsonFieldType.STRING).description("이용약관 페이지 URL"),
                fieldWithPath("isRequired").type(JsonFieldType.BOOLEAN).description("이용약관동의 필수여부 (true-필수, false-선택)"),
                fieldWithPath("isMarketing").type(JsonFieldType.BOOLEAN).description("마케팅 약관동의 여부")
            )));
  }
}
