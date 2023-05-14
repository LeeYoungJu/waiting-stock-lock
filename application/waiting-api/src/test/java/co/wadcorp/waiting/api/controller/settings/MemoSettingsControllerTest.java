package co.wadcorp.waiting.api.controller.settings;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.ControllerTest;
import co.wadcorp.waiting.api.controller.settings.dto.request.MemoKeywordCreateRequest;
import co.wadcorp.waiting.api.controller.settings.dto.request.MemoKeywordOrderingRequest;
import co.wadcorp.waiting.api.controller.settings.dto.request.MemoKeywordOrderingRequest.KeywordOrderingDto;
import co.wadcorp.waiting.api.controller.settings.dto.request.MemoKeywordUpdateRequest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

class MemoSettingsControllerTest extends ControllerTest {

  @DisplayName("메모 키워드 단건 생성 시 키워드 id 값은 필수이다.")
  @Test
  void createMemoKeywordWithoutId() throws Exception {
    // given
    String shopId = "test-shop-id";
    MemoKeywordCreateRequest request = MemoKeywordCreateRequest.builder()
        .keyword("단골손님")
        .build();

    // when
    mockMvc.perform(
        post("/api/v1/shops/{shopId}/settings/memo/keywords", shopId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        // then
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("메모 키워드 ID는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메모 키워드 단건 생성 시 키워드 내용은 필수이다.")
  @Test
  void createMemoKeywordWithoutKeyword() throws Exception {
    // given
    String shopId = "test-shop-id";
    MemoKeywordCreateRequest request = MemoKeywordCreateRequest.builder()
        .id(UUIDUtil.shortUUID())
        .build();

    // when
    mockMvc.perform(
        post("/api/v1/shops/{shopId}/settings/memo/keywords", shopId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        // then
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("메모 키워드 내용은 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메모 키워드 단건 생성 시 키워드 최대 길이는 20자다.")
  @Test
  void createMemoKeywordWithLongKeyword() throws Exception {
    // given
    String shopId = "test-shop-id";
    MemoKeywordCreateRequest request = MemoKeywordCreateRequest.builder()
        .id(UUIDUtil.shortUUID())
        .keyword("일이삼사오육칠팔구십일이삼사오육칠팔구십일")
        .build();

    // when
    mockMvc.perform(
        post("/api/v1/shops/{shopId}/settings/memo/keywords", shopId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
    )
        .andDo(print())
        // then
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("키워드 최대 길이는 20자입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메모 키워드 단건 수정 시 키워드 내용은 필수이다.")
  @Test
  void updateMemoKeywordWithoutKeyword() throws Exception {
    // given
    String shopId = "test-shop-id";
    String keywordId = "test-keyword-id";
    MemoKeywordUpdateRequest request = MemoKeywordUpdateRequest.builder()
        .id(keywordId)
        .build();

    // when
    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/memo/keywords/{keywordId}/update", shopId, keywordId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        // then
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("메모 키워드 내용은 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메모 키워드 단건 수정 시 키워드 최대 길이는 20자다.")
  @Test
  void updateMemoKeywordWithLongKeyword() throws Exception {
    // given
    String shopId = "test-shop-id";
    String keywordId = "test-keyword-id";
    MemoKeywordUpdateRequest request = MemoKeywordUpdateRequest.builder()
        .id(keywordId)
        .keyword("일이삼사오육칠팔구십일이삼사오육칠팔구십일")
        .build();

    // when
    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/memo/keywords/{keywordId}/update", shopId, keywordId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        // then
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("키워드 최대 길이는 20자입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메모 키워드 순서 저장 시 순번(ordering)은 중복될 수 없다.")
  @Test
  void updateMemoKeywordsOrderingWithDuplicateOrdering() throws Exception {
    // given
    String shopId = "test-shop-id";
    MemoKeywordOrderingRequest request = MemoKeywordOrderingRequest.builder()
        .keywords(List.of(
            KeywordOrderingDto.builder()
                .id(UUIDUtil.shortUUID())
                .ordering(1)
                .build(),
            KeywordOrderingDto.builder()
                .id(UUIDUtil.shortUUID())
                .ordering(1)
                .build(),
            KeywordOrderingDto.builder()
                .id(UUIDUtil.shortUUID())
                .ordering(3)
                .build()
        ))
        .build();

    // when
    mockMvc.perform(
        post("/api/v1/shops/{shopId}/settings/memo/keywords/ordering", shopId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        // then
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("순서값은 중복될 수 없습니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메모 키워드 순서 저장 시 순번 리스트는 필수값이다.")
  @Test
  void updateMemoKeywordsOrderingWithoutKeywords() throws Exception {
    // given
    String shopId = "test-shop-id";
    MemoKeywordOrderingRequest request = MemoKeywordOrderingRequest.builder()
        .keywords(List.of())
        .build();

    // when
    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/memo/keywords/ordering", shopId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        // then
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("메모 키워드 리스트는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("메모 키워드 순서 저장 시 순번 최소값은 1이다.")
  @Test
  void updateMemoKeywordsOrderingWithZero() throws Exception {
    // given
    String shopId = "test-shop-id";
    MemoKeywordOrderingRequest request = MemoKeywordOrderingRequest.builder()
        .keywords(List.of(
            KeywordOrderingDto.builder()
                .id(UUIDUtil.shortUUID())
                .ordering(0)
                .build()
        ))
        .build();

    // when
    mockMvc.perform(
            post("/api/v1/shops/{shopId}/settings/memo/keywords/ordering", shopId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        // then
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("메모 키워드 순서값은 1이상이어야 합니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }
}