package co.wadcorp.waiting.api.test.login;

import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentRequest;
import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.waiting.api.controller.login.LoginController;
import co.wadcorp.waiting.api.model.login.LoginRefreshRequest;
import co.wadcorp.waiting.api.model.login.LoginRequest;
import co.wadcorp.waiting.api.model.login.LoginResponse;
import co.wadcorp.waiting.api.service.login.LoginApiService;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

class LoginControllerTest extends RestDocsSupport {

  private final LoginApiService loginApiService = mock(LoginApiService.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object init() {
    return new LoginController(loginApiService);
  }

  @Test
  @DisplayName("로그인")
  void login() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";

    LoginRequest request = new LoginRequest("waiting@catchtable.co.kr", "1q2w3e4r");

    LoginResponse loginResponse = new LoginResponse("eyJ0eXAi......eyJhdWQiOiJjYXRj......XQiOjE2NjQ3NTg0NTAsInVzZXIiOiIyIn0.lF1eGWrdFwdyJ-fm8Ln8gvbUrZyVmcwKacY2kxTu6Ac",
        "eyJ0eXAi....eyJhdWQiOiJjYXRj........IjoxNjY0NzU4NzUwLCJpYXQiOjdsnOiIyIn0.HWchbkEsoT2ujg3TC9u7O9IeTIN0QkBeu9WKf7AQKyY");


    // when
    when(loginApiService.login(any())).thenReturn(loginResponse);


    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/login", SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("login-login",
            getDocumentRequest(),
            getDocumentResponse(),
            requestFields(
                fieldWithPath("userId").type(JsonFieldType.STRING).description("아이디(이메일)"),
                fieldWithPath("userPw").type(JsonFieldType.STRING).description("비밀번호")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("엑세스 토큰"),
                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레쉬 토큰")
            )
        ));
  }


  @Test
  @DisplayName("토큰 재발급")
  void refresh() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";

    LoginRefreshRequest request = new LoginRefreshRequest("eyJ0eXAi....eyJhdWQiOiJjYXRj........IjoxNjY0NzU4NzUwLCJpYXQiOjdsnOiIyIn0.HWchbkEsoT2ujg3TC9u7O9IeTIN0QkBeu9WKf7AQKyY");

    LoginResponse loginResponse = new LoginResponse("eyJ0eXAi......eyJhdWQiOiJjYXRj......XQiOjE2NjQ3NTg0NTAsInVzZXIiOiIyIn0.lF1eGWrdFwdyJ-fm8Ln8gvbUrZyVmcwKacY2kxTu6Ac",
        "eyJ0eXAi....eyJhdWQiOiJjYXRj........IjoxNjY0NzU4NzUwLCJpYXQiOjdsnOiIyIn0.HWchbkEsoT2ujg3TC9u7O9IeTIN0QkBeu9WKf7AQKyY");

    // when
    when(loginApiService.refresh(any())).thenReturn(loginResponse);


    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/login/refresh", SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("login-refresh",
            getDocumentRequest(),
            getDocumentResponse(),
            requestFields(
                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레쉬 토큰")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("엑세스 토큰"),
                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레쉬 토큰")
            )
        ));
  }
}
