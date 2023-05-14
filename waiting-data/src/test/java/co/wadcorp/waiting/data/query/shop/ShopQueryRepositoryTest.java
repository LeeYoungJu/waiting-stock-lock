package co.wadcorp.waiting.data.query.shop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import co.wadcorp.waiting.IntegrationTest;
import co.wadcorp.waiting.data.domain.shop.ShopEntity;
import co.wadcorp.waiting.data.domain.shop.ShopRepository;
import co.wadcorp.waiting.data.query.shop.dto.ShopSeqShopIdDto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ShopQueryRepositoryTest extends IntegrationTest {

  @Autowired
  private ShopQueryRepository shopQueryRepository;

  @Autowired
  private ShopRepository shopRepository;

  @DisplayName("최소 seq와 size 수로 Shop seq와 ID를 조회한다.")
  @Test
  void findShopSeqsByNoOffsetPaging() {
    // given
    ShopEntity shop1 = createShop("shopId-1");
    ShopEntity shop2 = createShop("shopId-2");
    ShopEntity shop3 = createShop("shopId-3");
    ShopEntity shop4 = createShop("shopId-4");
    ShopEntity shop5 = createShop("shopId-5");

    // when
    List<ShopSeqShopIdDto> results = shopQueryRepository.findShopSeqsByNoOffsetPaging(shop3.getSeq(), 2);

    // then
    assertThat(results).hasSize(2)
        .extracting("seq", "shopId")
        .contains(
            tuple(shop3.getSeq(), "shopId-3"),
            tuple(shop4.getSeq(), "shopId-4")
        );
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