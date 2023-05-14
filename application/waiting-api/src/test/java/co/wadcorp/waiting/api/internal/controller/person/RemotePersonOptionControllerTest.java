package co.wadcorp.waiting.api.internal.controller.person;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.wadcorp.waiting.api.ControllerTest;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RemotePersonOptionControllerTest extends ControllerTest {

  @DisplayName("인원 옵션 목록 - 매장 ID를 CSV 형태로 받을 수 있다.")
  @Test
  void personOptions() throws Exception {

    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put("1", "11");
    channelShopIdMapping.put("2", "22");
    channelShopIdMapping.put("3", "33");

    when(channelShopIdConverter.getShopIds(any())).thenReturn(
        channelShopIdMapping
    );
    when(channelShopIdConverter.isSupport(any())).thenReturn(true);

    mockMvc.perform(
            get("/internal/api/v1/shops/{shopIds}/person-options", "1,2,3")
                .header("X-CHANNEL-ID", "CATCHTABLE-B2C")
        )
        .andDo(print())
        .andExpect(status().isOk());
  }

}