package co.wadcorp.waiting.api.internal.controller.shop;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.waiting.api.ControllerTest;
import co.wadcorp.waiting.api.internal.controller.shop.dto.request.InternalDisablePutOffSettingsRequest;
import co.wadcorp.waiting.api.internal.controller.shop.dto.request.InternalRemoteShopOperationTimeSettingsRequest;
import co.wadcorp.waiting.api.internal.controller.shop.dto.request.InternalRemoteShopOperationTimeSettingsRequest.RemoteOperationTimeSettingsDto;
import co.wadcorp.waiting.data.enums.OperationDay;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

class InternalShopInfoRegisterControllerTest extends ControllerTest {

  @DisplayName("원격 운영시간 세팅 시 1건 이상 등록해야 한다.")
  @Test
  void saveRemoteShopOperationTimeSettingsWithoutList() throws Exception {
    InternalRemoteShopOperationTimeSettingsRequest request = InternalRemoteShopOperationTimeSettingsRequest.builder()
        .settings(List.of())
        .build();

    mockMvc.perform(
            post("/internal/api/v1/shops/{shopId}/settings/remote/time-operations", "SHOP_ID")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("원격 운영시간 세팅은 1건 이상 등록해야 합니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("원격 운영시간 세팅 시 요일 정보는 필수값이다.")
  @Test
  void saveRemoteShopOperationTimeSettingsWithoutOperationDay() throws Exception {
    InternalRemoteShopOperationTimeSettingsRequest request = InternalRemoteShopOperationTimeSettingsRequest.builder()
        .settings(List.of(RemoteOperationTimeSettingsDto.builder()
            .operationStartTime(LocalTime.of(10, 0))
            .operationEndTime(LocalTime.of(22, 0))
            .isClosedDay(false)
            .isUsedAutoPause(true)
            .autoPauseStartTime(LocalTime.of(14, 0))
            .autoPauseEndTime(LocalTime.of(15, 0))
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/internal/api/v1/shops/{shopId}/settings/remote/time-operations", "SHOP_ID")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("요일은 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("원격 운영시간 세팅 시 운영 시작 시각은 필수값이다.")
  @Test
  void saveRemoteShopOperationTimeSettingsWithoutOperationStartTime() throws Exception {
    InternalRemoteShopOperationTimeSettingsRequest request = InternalRemoteShopOperationTimeSettingsRequest.builder()
        .settings(List.of(RemoteOperationTimeSettingsDto.builder()
            .operationDay(OperationDay.MONDAY)
            .operationEndTime(LocalTime.of(22, 0))
            .isClosedDay(false)
            .isUsedAutoPause(true)
            .autoPauseStartTime(LocalTime.of(14, 0))
            .autoPauseEndTime(LocalTime.of(15, 0))
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/internal/api/v1/shops/{shopId}/settings/remote/time-operations", "SHOP_ID")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("운영 시작 시각은 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("원격 운영시간 세팅 시 운영 종료 시각은 필수값이다.")
  @Test
  void saveRemoteShopOperationTimeSettingsWithoutOperationEndTime() throws Exception {
    InternalRemoteShopOperationTimeSettingsRequest request = InternalRemoteShopOperationTimeSettingsRequest.builder()
        .settings(List.of(RemoteOperationTimeSettingsDto.builder()
            .operationDay(OperationDay.MONDAY)
            .operationStartTime(LocalTime.of(10, 0))
            .isClosedDay(false)
            .isUsedAutoPause(true)
            .autoPauseStartTime(LocalTime.of(14, 0))
            .autoPauseEndTime(LocalTime.of(15, 0))
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/internal/api/v1/shops/{shopId}/settings/remote/time-operations", "SHOP_ID")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("운영 종료 시각은 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("원격 운영시간 세팅 시 휴무일 여부는 필수값이다.")
  @Test
  void saveRemoteShopOperationTimeSettingsWithoutClosedDay() throws Exception {
    InternalRemoteShopOperationTimeSettingsRequest request = InternalRemoteShopOperationTimeSettingsRequest.builder()
        .settings(List.of(RemoteOperationTimeSettingsDto.builder()
            .operationDay(OperationDay.MONDAY)
            .operationStartTime(LocalTime.of(10, 0))
            .operationEndTime(LocalTime.of(22, 0))
            .isUsedAutoPause(true)
            .autoPauseStartTime(LocalTime.of(14, 0))
            .autoPauseEndTime(LocalTime.of(15, 0))
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/internal/api/v1/shops/{shopId}/settings/remote/time-operations", "SHOP_ID")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("휴무일 여부는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("원격 운영시간 세팅 시 자동 일시정지 사용 여부는 필수값이다.")
  @Test
  void saveRemoteShopOperationTimeSettingsWithoutUsedAutoPause() throws Exception {
    InternalRemoteShopOperationTimeSettingsRequest request = InternalRemoteShopOperationTimeSettingsRequest.builder()
        .settings(List.of(RemoteOperationTimeSettingsDto.builder()
            .operationDay(OperationDay.MONDAY)
            .operationStartTime(LocalTime.of(10, 0))
            .operationEndTime(LocalTime.of(22, 0))
            .isClosedDay(false)
            .autoPauseStartTime(LocalTime.of(14, 0))
            .autoPauseEndTime(LocalTime.of(15, 0))
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/internal/api/v1/shops/{shopId}/settings/remote/time-operations", "SHOP_ID")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("자동 일시정지 사용 여부는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("원격 운영시간 세팅 시 자동 일시정지 시작 시각은 필수값이다.")
  @Test
  void saveRemoteShopOperationTimeSettingsWithoutAutoPauseStartTime() throws Exception {
    InternalRemoteShopOperationTimeSettingsRequest request = InternalRemoteShopOperationTimeSettingsRequest.builder()
        .settings(List.of(RemoteOperationTimeSettingsDto.builder()
            .operationDay(OperationDay.MONDAY)
            .operationStartTime(LocalTime.of(10, 0))
            .operationEndTime(LocalTime.of(22, 0))
            .isClosedDay(false)
            .isUsedAutoPause(true)
            .autoPauseEndTime(LocalTime.of(15, 0))
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/internal/api/v1/shops/{shopId}/settings/remote/time-operations", "SHOP_ID")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("자동 일시정지 시작 시각은 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("원격 운영시간 세팅 시 자동 일시정지 종료 시각은 필수값이다.")
  @Test
  void saveRemoteShopOperationTimeSettingsWithoutOperationDay2() throws Exception {
    InternalRemoteShopOperationTimeSettingsRequest request = InternalRemoteShopOperationTimeSettingsRequest.builder()
        .settings(List.of(RemoteOperationTimeSettingsDto.builder()
            .operationDay(OperationDay.MONDAY)
            .operationStartTime(LocalTime.of(10, 0))
            .operationEndTime(LocalTime.of(22, 0))
            .isClosedDay(false)
            .isUsedAutoPause(true)
            .autoPauseStartTime(LocalTime.of(14, 0))
            .build()
        ))
        .build();

    mockMvc.perform(
            post("/internal/api/v1/shops/{shopId}/settings/remote/time-operations", "SHOP_ID")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("자동 일시정지 종료 시각은 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("미루기 off 세팅 시 미루기 off 여부는 필수값이다.")
  @Test
  void setDisablePutOffSettingsWithoutDisablePutOff() throws Exception {
    InternalDisablePutOffSettingsRequest request = InternalDisablePutOffSettingsRequest.builder()
        .build();

    mockMvc.perform(
            post("/internal/api/v1/shops/{shopId}/settings/disable-put-off", "SHOP_ID")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("미루기 off 여부는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

}