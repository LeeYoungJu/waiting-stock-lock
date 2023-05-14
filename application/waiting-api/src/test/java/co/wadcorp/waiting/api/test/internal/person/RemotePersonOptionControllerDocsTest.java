package co.wadcorp.waiting.api.test.internal.person;

import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentRequest;
import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.internal.controller.person.RemotePersonOptionController;
import co.wadcorp.waiting.api.internal.service.person.RemotePersonOptionApiService;
import co.wadcorp.waiting.api.internal.service.person.dto.response.RemotePersonOptionResponse;
import co.wadcorp.waiting.api.internal.service.person.dto.response.RemotePersonOptionResponse.AdditionalOptionVO;
import co.wadcorp.waiting.api.internal.service.person.dto.response.RemotePersonOptionResponse.PersonOptionVO;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

class RemotePersonOptionControllerDocsTest extends RestDocsSupport {

  private final RemotePersonOptionApiService remotePersonOptionApiService
      = mock(RemotePersonOptionApiService.class);

  @Override
  public Object init() {
    return new RemotePersonOptionController(remotePersonOptionApiService);
  }

  @DisplayName("원격 웨이팅 인원 옵션 목록")
  @Test
  void getPersonOptionTest() throws Exception {
    // given
    List<RemotePersonOptionResponse> response = List.of(RemotePersonOptionResponse.builder()
        .shopId(1L)
        .isUsedPersonOptionSetting(true)
        .personOptions(List.of(PersonOptionVO.builder()
            .id(UUIDUtil.shortUUID())
            .name("유아")
            .isDisplayed(true)
            .isSeat(true)
            .additionalOptions(List.of(AdditionalOptionVO.builder()
                .id(UUIDUtil.shortUUID())
                .name("유아용 의자")
                .isDisplayed(true)
                .build()
            ))
            .build()
        ))
        .build()
    );

    // when
    when(remotePersonOptionApiService.findPersonOptions(any()))
        .thenReturn(response);

    // then
    mockMvc.perform(get("/internal/api/v1/shops/{shopIds}/person-options", "CSV_SHOP_IDS")
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
        )
        .andExpect(status().isOk())
        .andDo(document("remote-person-options",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopIds").description("B2C의 shopSeq CSV (1,2,3)")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("shopId").type(JsonFieldType.NUMBER)
                    .description("B2C의 shopSeq"),
                fieldWithPath("isUsedPersonOptionSetting").type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 사용 여부"),
                fieldWithPath("personOptions").type(JsonFieldType.ARRAY)
                    .description("인원 옵션 정보"),
                fieldWithPath("personOptions[].id").type(JsonFieldType.STRING)
                    .description("인원 옵션 ID"),
                fieldWithPath("personOptions[].name").type(JsonFieldType.STRING)
                    .description("인원 옵션 이름"),
                fieldWithPath("personOptions[].isDisplayed").type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 노출 여부"),
                fieldWithPath("personOptions[].isSeat").type(JsonFieldType.BOOLEAN)
                    .description("인원 옵션 착석 여부 (테이블 착석자 수에 포함 여부)"),
                fieldWithPath("personOptions[].additionalOptions").type(JsonFieldType.ARRAY)
                    .description("추가 인원 옵션"),
                fieldWithPath("personOptions[].additionalOptions[].id").type(JsonFieldType.STRING)
                    .description("추가 인원 옵션 ID"),
                fieldWithPath("personOptions[].additionalOptions[].name").type(JsonFieldType.STRING)
                    .description("추가 인원 옵션 이름"),
                fieldWithPath("personOptions[].additionalOptions[].isDisplayed")
                    .type(JsonFieldType.BOOLEAN)
                    .description("추가 인원 옵션 노출 여부")
            )
        ));
  }

}
