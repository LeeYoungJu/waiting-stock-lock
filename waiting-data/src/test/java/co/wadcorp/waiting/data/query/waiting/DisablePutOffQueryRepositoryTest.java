package co.wadcorp.waiting.data.query.waiting;

import static org.assertj.core.api.Assertions.assertThat;

import co.wadcorp.waiting.IntegrationTest;
import co.wadcorp.waiting.data.domain.settings.putoff.DisablePutOffEntity;
import co.wadcorp.waiting.data.domain.settings.putoff.DisablePutOffRepository;
import co.wadcorp.waiting.data.query.settings.DisablePutOffQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class DisablePutOffQueryRepositoryTest extends IntegrationTest {

  @Autowired
  private DisablePutOffQueryRepository disablePutOffQueryRepository;

  @Autowired
  private DisablePutOffRepository disablePutOffRepository;

  @DisplayName("미루기 off한 매장인지 체크한다.")
  @CsvSource(value = {"shopId-1,true", "shopId-2,false"})
  @ParameterizedTest
  void isShopDisabledPutOff(String shopId, boolean expected) {
    // given
    createDisablePutOff("shopId-1");

    // when
    boolean result = disablePutOffQueryRepository.isShopDisabledPutOff(shopId);

    // then
    assertThat(result).isEqualTo(expected);
  }

  private DisablePutOffEntity createDisablePutOff(String shopId) {
    DisablePutOffEntity disablePutOff = DisablePutOffEntity.builder()
        .shopId(shopId)
        .build();
    return disablePutOffRepository.save(disablePutOff);
  }

}