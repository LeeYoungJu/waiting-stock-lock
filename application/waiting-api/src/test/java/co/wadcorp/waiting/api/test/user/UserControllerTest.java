package co.wadcorp.waiting.api.test.user;

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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.waiting.api.controller.user.UserController;
import co.wadcorp.waiting.api.controller.user.dto.UpdateEmailRequest;
import co.wadcorp.waiting.api.controller.user.dto.UpdatePasswordRequest;
import co.wadcorp.waiting.api.controller.user.dto.UpdatePhoneNumberConfirmRequest;
import co.wadcorp.waiting.api.controller.user.dto.UpdatePhoneNumberRequest;
import co.wadcorp.waiting.api.controller.user.dto.UserResponse;
import co.wadcorp.waiting.api.service.user.UserApiService;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

class UserControllerTest extends RestDocsSupport {

  private final UserApiService userApiService = mock(UserApiService.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object init() {
    return new UserController(userApiService);
  }

  @Test
  @DisplayName("계정 설정 조회")
  void getShops() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String ctmAuth = "Bearer accessToken";

    UserResponse.User user = UserResponse.User.builder()
        .phone("010-1234-5678")
        .email("email@email.com")
        .updateEmail("update@email.com")
        .build();

    UserResponse.Business business = UserResponse.Business.builder()
        .bizNum("123-123-12345")
        .bizAddress("서울특별시 어딘가")
        .build();

    // when
    when(userApiService.getUserInfo(any(), any())).thenReturn(new UserResponse(user, business));

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/shops/{shopId}/user", SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-CTM-AUTH", ctmAuth))
        .andExpect(status().isOk())
        .andDo(document("user-user",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("user").type(JsonFieldType.OBJECT).description("유저 정보"),
                fieldWithPath("user.phone").type(JsonFieldType.STRING).description("유저 연락처"),
                fieldWithPath("user.email").type(JsonFieldType.STRING).description("유저 이메일 주소"),
                fieldWithPath("user.updateEmail").type(JsonFieldType.STRING)
                    .description("변경할 이메일 주소").optional(),
                fieldWithPath("business").type(JsonFieldType.OBJECT).description("사업자 정보")
                    .optional(),
                fieldWithPath("business.bizNum").type(JsonFieldType.STRING).description("사업자 등록번호")
                    .optional(),
                fieldWithPath("business.bizAddress").type(JsonFieldType.STRING)
                    .description("사업장 주소").optional()
            )
        ));
  }


  @Test
  @DisplayName("이메일 변경 요청")
  void updateEmail() throws Exception {

    // given
    final String SHOP_ID = "SHOP_UUID";
    final String ctmAuth = "Bearer accessToken";

    UpdateEmailRequest request = new UpdateEmailRequest("newEmail@email.com");

    // when
    userApiService.updatePassword(any(), any(), any());

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/user/update-email", SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-CTM-AUTH", ctmAuth)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("user-update-email",
            getDocumentRequest(),
            getDocumentResponse(),
            requestFields(
                fieldWithPath("newEmail").type(JsonFieldType.STRING).description("변경할 이메일")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
            )
        ));
  }


  @Test
  @DisplayName("비밀번호 변경")
  void updatePassword() throws Exception {

    // given
    final String SHOP_ID = "SHOP_UUID";
    final String ctmAuth = "Bearer accessToken";

    UpdatePasswordRequest request = new UpdatePasswordRequest("oldpassword", "newpassword");

    // when
    userApiService.updatePassword(any(), any(), any());

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/user/password/update", SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-CTM-AUTH", ctmAuth)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("user-update-password",
            getDocumentRequest(),
            getDocumentResponse(),
            requestFields(
                fieldWithPath("oldPassword").type(JsonFieldType.STRING).description("기존 비밀번호"),
                fieldWithPath("newPassword").type(JsonFieldType.STRING).description("신규 비밀번호")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
            )
        ));
  }

  @Test
  @DisplayName("휴대전화 변경 - 요청")
  void updatePhoneRequest() throws Exception {

    // given
    final String SHOP_ID = "SHOP_UUID";
    final String ctmAuth = "Bearer accessToken";

    UpdatePhoneNumberRequest request = UpdatePhoneNumberRequest.builder()
        .phoneNumber("010-2345-6789").build();

    // when
    userApiService.updatePhoneRequest(any(), any());

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/user/update-phone/request", SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-CTM-AUTH", ctmAuth)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("user-update-phone-request",
            getDocumentRequest(),
            getDocumentResponse(),
            requestFields(
                fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("변경할 연락처")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
            )
        ));
  }

  @Test
  @DisplayName("휴대전화 변경 - 인증")
  void updatePhoneConfirm() throws Exception {

    // given
    final String SHOP_ID = "SHOP_UUID";
    final String ctmAuth = "Bearer accessToken";

    UpdatePhoneNumberConfirmRequest request = UpdatePhoneNumberConfirmRequest.builder()
        .phoneNumber("010-2345-6789").certNo("123456").build();

    // when
    userApiService.updatePhoneConfirm(any(), any(), any());

    // then
    mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/user/update-phone/confirm", SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-CTM-AUTH", ctmAuth)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("user-update-phone-confirm",
            getDocumentRequest(),
            getDocumentResponse(),
            requestFields(
                fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("변경할 연락처"),
                fieldWithPath("certNo").type(JsonFieldType.STRING).description("인증번호")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
            )
        ));
  }
}