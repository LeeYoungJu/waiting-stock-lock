package co.wadcorp.waiting.data.infra.waiting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import co.wadcorp.waiting.IntegrationTest;
import co.wadcorp.waiting.data.domain.shop.operation.status.RegistrableStatus;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class JpaShopOperationInfoRepositoryTest extends IntegrationTest {

  @Autowired
  private ShopOperationInfoRepository shopOperationInfoRepository;

  @AfterEach
  void tearDown() {
    shopOperationInfoRepository.deleteAllInBatch();
  }

  @DisplayName("shopId에 해당하는 특정 날짜 이후의 매장 운영정보를 조회한다.")
  @Test
  void test() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 3, 8);

    createShopOperationInfo(operationDate.minusDays(1), "shopId-1");
    createShopOperationInfo(operationDate, "shopId-1");
    createShopOperationInfo(operationDate, "shopId-2");
    createShopOperationInfo(operationDate.plusDays(1), "shopId-1");
    createShopOperationInfo(operationDate.plusDays(2), "shopId-1");

    // when
    List<ShopOperationInfoEntity> results = shopOperationInfoRepository.findByShopIdAndOperationDateAfterOrEqual(
        "shopId-1", operationDate);

    // then
    assertThat(results).hasSize(3)
        .extracting("shopId", "operationDate")
        .containsExactlyInAnyOrder(
            tuple("shopId-1", operationDate),
            tuple("shopId-1", operationDate.plusDays(1)),
            tuple("shopId-1", operationDate.plusDays(2))
        );
  }

  private ShopOperationInfoEntity createShopOperationInfo(LocalDate operationDate, String shopId) {
    ShopOperationInfoEntity shopOperationInfo = ShopOperationInfoEntity.builder()
        .shopId(shopId)
        .operationDate(operationDate)
        .registrableStatus(RegistrableStatus.OPEN)
        .build();
    return shopOperationInfoRepository.save(shopOperationInfo);
  }

}