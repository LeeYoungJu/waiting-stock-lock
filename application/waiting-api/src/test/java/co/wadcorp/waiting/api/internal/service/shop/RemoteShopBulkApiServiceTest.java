package co.wadcorp.waiting.api.internal.service.shop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.internal.service.shop.dto.request.RemoteShopBulkServiceRequest;
import co.wadcorp.waiting.api.internal.service.shop.dto.response.RemoteShopBulkResponse;
import co.wadcorp.waiting.data.domain.channel.ChannelMappingEntity;
import co.wadcorp.waiting.data.domain.shop.ShopEntity;
import co.wadcorp.waiting.data.domain.shop.ShopRepository;
import co.wadcorp.waiting.data.infra.channel.JpaChannelMappingRepository;
import co.wadcorp.waiting.shared.enums.ServiceChannelId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RemoteShopBulkApiServiceTest extends IntegrationTest {

  @Autowired
  private RemoteShopBulkApiService remoteShopBulkApiService;

  @Autowired
  private JpaChannelMappingRepository jpaChannelMappingRepository;

  @Autowired
  private ShopRepository shopRepository;

  @DisplayName("No Offset 방식으로 매장 테이블의 seq, B2C id 묶음을 조회한다.")
  @Test
  void findShopsByBulk() {
    // given
    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";
    String shopId3 = "shopId-3";
    String shopId4 = "shopId-4";
    String shopId5 = "shopId-5";

    createChannelMapping("11", shopId1);
    createChannelMapping("12", shopId2);
    createChannelMapping("13", shopId3);
    createChannelMapping("14", shopId4);
    createChannelMapping("15", shopId5);

    ShopEntity shop1 = createShop(shopId1);
    ShopEntity shop2 = createShop(shopId2);
    ShopEntity shop3 = createShop(shopId3);
    ShopEntity shop4 = createShop(shopId4);
    ShopEntity shop5 = createShop(shopId5);

    RemoteShopBulkServiceRequest request = RemoteShopBulkServiceRequest.builder()
        .minSeq(shop3.getSeq())
        .size(2)
        .build();

    // when
    RemoteShopBulkResponse result = remoteShopBulkApiService.findShopsByBulk(
        ServiceChannelId.CATCHTABLE_B2C.getValue(), request);

    // then
    assertThat(result.getShopIdPairs()).hasSize(2)
        .extracting("seq", "shopId", "waitingShopId")
        .contains(
            tuple(shop3.getSeq(), 13L, "shopId-3"),
            tuple(shop4.getSeq(), 14L, "shopId-4")
        );
  }

  @DisplayName("No Offset 방식으로 매장 테이블의 seq, B2C id 묶음을 조회할 때, 채널 매핑 정보가 없으면 null을 반환한다.")
  @Test
  void findShopsByBulkIfEmpty() {
    // given
    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";
    String shopId3 = "shopId-3";
    String shopId4 = "shopId-4";
    String shopId5 = "shopId-5";

    // 4번 없음
    createChannelMapping("11", shopId1);
    createChannelMapping("12", shopId2);
    createChannelMapping("13", shopId3);
    createChannelMapping("15", shopId5);

    ShopEntity shop1 = createShop(shopId1);
    ShopEntity shop2 = createShop(shopId2);
    ShopEntity shop3 = createShop(shopId3);
    ShopEntity shop4 = createShop(shopId4);
    ShopEntity shop5 = createShop(shopId5);

    RemoteShopBulkServiceRequest request = RemoteShopBulkServiceRequest.builder()
        .minSeq(shop3.getSeq())
        .size(2)
        .build();

    // when
    RemoteShopBulkResponse result = remoteShopBulkApiService.findShopsByBulk(
        ServiceChannelId.CATCHTABLE_B2C.getValue(), request);

    // then
    assertThat(result.getShopIdPairs()).hasSize(2)
        .extracting("seq", "shopId", "waitingShopId")
        .contains(
            tuple(shop3.getSeq(), 13L, "shopId-3"),
            tuple(shop4.getSeq(), null, "shopId-4")
        );
  }

  private ChannelMappingEntity createChannelMapping(String channelShopId, String shopId) {
    ChannelMappingEntity entity = ChannelMappingEntity.builder()
        .channelId(ServiceChannelId.CATCHTABLE_B2C.getValue())
        .channelShopId(channelShopId)
        .shopId(shopId)
        .build();
    return jpaChannelMappingRepository.save(entity);
  }

  private ShopEntity createShop(String shopId) {
    ShopEntity shop = ShopEntity.builder()
        .shopId(shopId)
        .shopName("shopName")
        .shopAddress("shopAddress")
        .shopTelNumber("shopTelNumber")
        .build();
    return shopRepository.save(shop);
  }

}