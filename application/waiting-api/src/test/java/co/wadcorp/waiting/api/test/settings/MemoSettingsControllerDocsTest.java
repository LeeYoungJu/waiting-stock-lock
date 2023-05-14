package co.wadcorp.waiting.api.test.settings;

import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentRequest;
import static co.wadcorp.waiting.api.test.ApiDocUtil.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.controller.settings.MemoSettingsController;
import co.wadcorp.waiting.api.controller.settings.dto.request.MemoKeywordCreateRequest;
import co.wadcorp.waiting.api.controller.settings.dto.request.MemoKeywordOrderingRequest;
import co.wadcorp.waiting.api.controller.settings.dto.request.MemoKeywordUpdateRequest;
import co.wadcorp.waiting.api.service.settings.MemoSettingsApiService;
import co.wadcorp.waiting.api.service.settings.dto.request.MemoKeywordMergeServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.request.MemoKeywordOrderingServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.response.MemoKeywordListResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.MemoKeywordResponse;
import co.wadcorp.waiting.api.test.RestDocsSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

class MemoSettingsControllerDocsTest extends RestDocsSupport {

  private final MemoSettingsApiService memoSettingsApiService = mock(
      MemoSettingsApiService.class
  );

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object init() {
    return new MemoSettingsController(memoSettingsApiService);
  }

  @DisplayName("매장별 메모 키워드 리스트 조회")
  @Test
  void getMemoKeywords() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    MemoKeywordListResponse response = createDefaultMemoKeywordListResponse();

    // when
    when(memoSettingsApiService.getMemoKeywords(any())).thenReturn(response);

    // then
    mockMvc.perform(
            get("/api/v1/shops/{shopId}/settings/memo/keywords",
                    SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("memo-keywords-settings-get",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("keywords").type(JsonFieldType.ARRAY)
                    .description("키워드 목록"),
                fieldWithPath("keywords[].id").type(JsonFieldType.STRING)
                    .description("키워드 short UUID"),
                fieldWithPath("keywords[].keyword").type(JsonFieldType.STRING)
                    .description("키워드 내용"),
                fieldWithPath("keywords[].ordering").type(JsonFieldType.NUMBER)
                    .description("키워드 노출 순서")
            )
        ));
  }

  @DisplayName("메모 키워드 단건 조회")
  @Test
  void getMemoKeyword() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String KEYWORD_ID = "KEYWORD_UUID";
    MemoKeywordResponse response = createMemoKeywordResponse(UUIDUtil.shortUUID(), "창가쪽", 5);

    // when
    when(memoSettingsApiService.getMemoKeyword(any())).thenReturn(response);

    // then
    mockMvc.perform(
            get("/api/v1/shops/{shopId}/settings/memo/keywords/{keywordId}",
                SHOP_ID, KEYWORD_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("memo-keywords-settings-get-one",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디"),
                parameterWithName("keywordId").description("키워드아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("id").type(JsonFieldType.STRING)
                    .description("키워드 아이디"),
                fieldWithPath("keyword").type(JsonFieldType.STRING)
                    .description("키워드 내용"),
                fieldWithPath("ordering").type(JsonFieldType.NUMBER)
                    .description("키워드 노출 순서")
            )
        ));
  }

  @DisplayName("메모 키워드 단건 저장")
  @Test
  void saveMemoKeyword() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    MemoKeywordResponse response = createMemoKeywordResponse(UUIDUtil.shortUUID(), "연예인", 5);
    MemoKeywordCreateRequest request = MemoKeywordCreateRequest.builder()
        .id(UUIDUtil.shortUUID())
        .keyword("연예인")
        .build();

    // when
    when(memoSettingsApiService.create(any(),
        any(MemoKeywordMergeServiceRequest.class))).thenReturn(response);

    // then
    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/memo/keywords",
                    SHOP_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("memo-keywords-settings-post",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            requestFields(
                fieldWithPath("id").type(JsonFieldType.STRING)
                    .description("생성할 키워드 아이디"),
                fieldWithPath("keyword").type(JsonFieldType.STRING)
                    .description("생성할 키워드 내용")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("id").type(JsonFieldType.STRING)
                    .description("키워드 아이디"),
                fieldWithPath("keyword").type(JsonFieldType.STRING)
                    .description("키워드 내용"),
                fieldWithPath("ordering").type(JsonFieldType.NUMBER)
                    .description("키워드 노출 순서")
            )
        ));
  }

  @DisplayName("메모 키워드 단건 수정")
  @Test
  void updateMemoKeyword() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String KEYWORD_ID = "KEYWORD_UUID";
    MemoKeywordResponse response = createMemoKeywordResponse(UUIDUtil.shortUUID(), "단골손님2", 5);
    MemoKeywordUpdateRequest request = MemoKeywordUpdateRequest.builder()
        .id(KEYWORD_ID)
        .keyword("단골손님2")
        .build();

    // when
    when(memoSettingsApiService.update(any(),
        any(MemoKeywordMergeServiceRequest.class))).thenReturn(response);

    // then
    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/memo/keywords/{keywordId}/update",
                SHOP_ID, KEYWORD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("memo-keywords-settings-update",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디"),
                parameterWithName("keywordId").description("키워드아이디")
            ),
            requestFields(
                fieldWithPath("id").type(JsonFieldType.STRING)
                    .description("수정 키워드 아이디"),
                fieldWithPath("keyword").type(JsonFieldType.STRING)
                    .description("수정 키워드 내용")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("id").type(JsonFieldType.STRING)
                    .description("키워드 아이디"),
                fieldWithPath("keyword").type(JsonFieldType.STRING)
                    .description("키워드 내용"),
                fieldWithPath("ordering").type(JsonFieldType.NUMBER)
                    .description("키워드 노출 순서")
            )
        ));
  }

  @DisplayName("메모 키워드 단건 삭제")
  @Test
  void deleteMemoKeyword() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    final String KEYWORD_ID = "KEYWORD_UUID";

    // then
    mockMvc.perform(
        post("/api/v1/shops/{shopId}/settings/memo/keywords/{keywordId}/delete",
            SHOP_ID, KEYWORD_ID)
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andDo(document("memo-keywords-settings-delete",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디"),
                parameterWithName("keywordId").description("키워드아이디")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부")
            )
        ));
  }

  @DisplayName("메모 키워드 순서 저장")
  @Test
  void updateMemoKeywordOrdering() throws Exception {
    // given
    final String SHOP_ID = "SHOP_UUID";
    MemoKeywordOrderingRequest request = createDefaultMemoKeywordOrderingRequest();
    MemoKeywordListResponse response = createDefaultMemoKeywordListResponse();

    // when
    when(memoSettingsApiService.updateMemoKeywordsOrdering(any(),
        any(MemoKeywordOrderingServiceRequest.class))).thenReturn(response);

    // then
    mockMvc.perform(
        post("/api/v1/shops/{shopId}/settings/memo/keywords/ordering", SHOP_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andDo(document("memo-keywords-settings-ordering",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("shopId").description("매장아이디")
            ),
            requestFields(
                fieldWithPath("keywords").type(JsonFieldType.ARRAY)
                    .description("키워드 순서 목록"),
                fieldWithPath("keywords[].id").type(JsonFieldType.STRING)
                    .description("키워드 short UUID"),
                fieldWithPath("keywords[].ordering").type(JsonFieldType.NUMBER)
                    .description("키워드 순서")
            ),
            responseFields(
                beneathPath("data").withSubsectionId("data"),
                fieldWithPath("keywords").type(JsonFieldType.ARRAY)
                    .description("키워드 목록"),
                fieldWithPath("keywords[].id").type(JsonFieldType.STRING)
                    .description("키워드 short UUID"),
                fieldWithPath("keywords[].keyword").type(JsonFieldType.STRING)
                    .description("키워드 내용"),
                fieldWithPath("keywords[].ordering").type(JsonFieldType.NUMBER)
                    .description("키워드 노출 순서")
            )
        ));
  }

  private MemoKeywordResponse createMemoKeywordResponse(String shortUUID, String keyword, int ordering) {
    return MemoKeywordResponse.builder()
        .id(shortUUID)
        .keyword(keyword)
        .ordering(ordering)
        .build();
  }

  private MemoKeywordListResponse createDefaultMemoKeywordListResponse() {
    return MemoKeywordListResponse.builder()
        .keywords(
            List.of(
                MemoKeywordListResponse.MemoKeywordDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .keyword("단골")
                    .ordering(1)
                    .build(),
                MemoKeywordListResponse.MemoKeywordDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .keyword("진상")
                    .ordering(2)
                    .build(),
                MemoKeywordListResponse.MemoKeywordDto.builder()
                    .id(UUIDUtil.shortUUID())
                    .keyword("창가")
                    .ordering(3)
                    .build()
            )
        )
        .build();
  }

  private MemoKeywordOrderingRequest createDefaultMemoKeywordOrderingRequest() {
    return MemoKeywordOrderingRequest.builder()
        .keywords(List.of(
            MemoKeywordOrderingRequest.KeywordOrderingDto.builder()
                .id(UUIDUtil.shortUUID())
                .ordering(1)
                .build(),
            MemoKeywordOrderingRequest.KeywordOrderingDto.builder()
                .id(UUIDUtil.shortUUID())
                .ordering(2)
                .build(),
            MemoKeywordOrderingRequest.KeywordOrderingDto.builder()
                .id(UUIDUtil.shortUUID())
                .ordering(3)
                .build()
        ))
        .build();

  }
}