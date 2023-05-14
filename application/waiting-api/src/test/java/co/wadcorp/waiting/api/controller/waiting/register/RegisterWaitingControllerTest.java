package co.wadcorp.waiting.api.controller.waiting.register;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.waiting.api.ControllerTest;
import co.wadcorp.waiting.api.controller.waiting.register.dto.request.WaitingRegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

class RegisterWaitingControllerTest extends ControllerTest {


  @DisplayName("현장 웨이팅 등록 시 연락처는 필수값이다.")
  @Test
  void registerWaitingWithoutCustomerPhone() throws Exception {

    WaitingRegisterRequest request = WaitingRegisterRequest.builder()
        .totalPersonCount(1)
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/register/waiting", "1")
                .header("X-REQUEST-ID", "deviceId")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("연락처는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("현장 웨이팅 등록 시 총 인원은 필수값이다.")
  @Test
  void registerWaitingWithoutTotalCount() throws Exception {

    WaitingRegisterRequest request = WaitingRegisterRequest.builder()
        .customerPhone("010-1234-5678")
        .build();

    mockMvc.perform(
            post("/api/v1/shops/{shopId}/register/waiting", "1")
                .header("X-REQUEST-ID", "deviceId")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("총 인원은 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }
}