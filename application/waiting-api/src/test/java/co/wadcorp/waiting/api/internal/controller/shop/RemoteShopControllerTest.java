package co.wadcorp.waiting.api.internal.controller.shop;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.waiting.api.ControllerTest;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;

class RemoteShopControllerTest extends ControllerTest {

  @DisplayName("매장 ID를 CSV 형태로 받을 수 있다.")
  @Test
  void ok() throws Exception {
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put("1", "11");
    channelShopIdMapping.put("2", "22");
    channelShopIdMapping.put("3", "33");

    given(channelShopIdConverter.getShopIds(any()))
        .willReturn(channelShopIdMapping);
    given(channelShopIdConverter.isSupport(any()))
        .willReturn(true);

    mockMvc.perform(
            get("/internal/api/v1/shops/{shopIds}", "1,2,3")
                .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
                .queryParam("operationDate", "2023-03-09")
        )
        .andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("매장 seq, B2C shopSeq 정보를 벌크 페이징으로 조회할 수 있다.")
  @Test
  void findShopByBulk() throws Exception {
    mockMvc.perform(get("/internal/api/v1/shops/bulk")
            .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
            .queryParam("minSeq", "1")
            .queryParam("size", "500")
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("200"))
        .andExpect(jsonPath("$.message").isEmpty())
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("매장 seq, B2C shopSeq 정보 조회 시 최소 Seq는 필수값이다.")
  @Test
  void findShopByBulkWithoutMinSeq() throws Exception {
    mockMvc.perform(get("/internal/api/v1/shops/bulk")
            .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
            .queryParam("size", "100")
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("최소 Seq는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("매장 seq, B2C shopSeq 정보 조회 시 페이징 개수는 필수값이다.")
  @Test
  void findShopByBulkWithoutSize() throws Exception {
    mockMvc.perform(get("/internal/api/v1/shops/bulk")
            .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
            .queryParam("minSeq", "1")
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("페이징 개수는 필수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("매장 seq, B2C shopSeq 정보 조회 시 페이징 개수는 최소 1이다.")
  @Test
  void findShopByBulkWithLessSize() throws Exception {
    mockMvc.perform(get("/internal/api/v1/shops/bulk")
            .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
            .queryParam("minSeq", "1")
            .queryParam("size", "0")
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("페이징 개수는 1 이상의 수입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @DisplayName("매장 seq, B2C shopSeq 정보 조회 시 페이징 개수는 최대 500이다.")
  @Test
  void findShopByBulkWithOverSize() throws Exception {
    mockMvc.perform(get("/internal/api/v1/shops/bulk")
            .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
            .queryParam("minSeq", "1")
            .queryParam("size", "501")
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.resultCode").value("400"))
        .andExpect(jsonPath("$.message").value("페이징 개수는 최대 500까지입니다."))
        .andExpect(jsonPath("$.displayMessage").isEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

}