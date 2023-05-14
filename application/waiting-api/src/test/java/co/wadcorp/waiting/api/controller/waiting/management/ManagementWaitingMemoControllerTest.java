package co.wadcorp.waiting.api.controller.waiting.management;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.waiting.api.ControllerTest;
import co.wadcorp.waiting.api.controller.waiting.management.dto.request.WaitingMemoSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

class ManagementWaitingMemoControllerTest extends ControllerTest {

  @DisplayName("웨이팅 메모 저장 시 빈값은 허용된다.")
  @Test
  void saveWaitingMemoEmpty() throws Exception {
    // given
    String shopId = "test-shop-id";
    String waitingId = "test-waiting-id";
    WaitingMemoSaveRequest request = WaitingMemoSaveRequest.builder()
        .waitingId(waitingId)
        .memo("")
        .build();

    // when
    mockMvc.perform(
        post("/api/v1/shops/{shopId}/management/waiting/{waitingId}/memo",
            shopId, waitingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
    )
        .andDo(print())
        // then
        .andExpect(status().isOk());
  }

  @DisplayName("웨이팅 메모 저장 시 null은 허용되지 않는다.")
  @Test
  void saveWaitingMemoNull() throws Exception {
    // given
    String shopId = "test-shop-id";
    String waitingId = "test-waiting-id";
    WaitingMemoSaveRequest request = WaitingMemoSaveRequest.builder()
        .waitingId("test-waiting-id")
        .build();

    // when
    mockMvc.perform(
            post("/api/v1/shops/{shopId}/management/waiting/{waitingId}/memo",
                shopId, waitingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        // then
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("메모 내용은 null을 허용하지 않습니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());;
  }

}