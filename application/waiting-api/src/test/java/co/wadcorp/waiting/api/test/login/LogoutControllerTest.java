package co.wadcorp.waiting.api.test.login;

import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentRequest;
import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.waiting.api.controller.login.LogoutController;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

class LogoutControllerTest extends RestDocsSupport {


  @Override
  public Object init() {
    return new LogoutController();
  }

  @Test
  @DisplayName("로그아웃")
  void logout() throws Exception {
    // given
    // when

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/logout")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("login-logout",
            getDocumentRequest(),
            getDocumentResponse()
        ));
  }
}
