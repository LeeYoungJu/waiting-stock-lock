package co.wadcorp.waiting.data.service.customer;

import static co.wadcorp.waiting.data.domain.waiting.WaitingStatus.CANCEL;
import static co.wadcorp.waiting.data.domain.waiting.WaitingStatus.SITTING;
import static co.wadcorp.waiting.data.domain.waiting.WaitingStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import co.wadcorp.waiting.IntegrationTest;
import co.wadcorp.waiting.data.config.WaitingNumberConstructor;
import co.wadcorp.waiting.data.domain.customer.ShopCustomerEntity;
import co.wadcorp.waiting.data.domain.customer.ShopCustomerId;
import co.wadcorp.waiting.data.domain.customer.ShopCustomerRepository;
import co.wadcorp.waiting.data.domain.waiting.PersonOption;
import co.wadcorp.waiting.data.domain.waiting.PersonOptionsData;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ShopCustomerServiceTest extends IntegrationTest {

  @Autowired
  private ShopCustomerService shopCustomerService;

  @Autowired
  private WaitingHistoryRepository waitingHistoryRepository;

  @Autowired
  private ShopCustomerRepository shopCustomerRepository;

  @DisplayName("복귀 시 이전 웨이팅 이력이 착석이라면 착석 카운트를 1 감소시킨다.")
  @Test
  void undoAfterSitting() {
    // given
    String shopId = "shopId";
    String waitingId = "waitingId";

    ShopCustomerEntity shopCustomer = createShopCustomer(shopId, 1, 1, 1);

    createWaitingHistory(waitingId, WAITING, WaitingDetailStatus.WAITING);
    createWaitingHistory(waitingId, SITTING, WaitingDetailStatus.SITTING);
    createWaitingHistory(waitingId, WAITING, WaitingDetailStatus.UNDO);

    // when
    shopCustomerService.undo(shopId, shopCustomer.getCustomerSeq(), waitingId);

    // then
    assertThat(shopCustomerRepository.findAll()).hasSize(1)
        .extracting("cancelCount", "noshowCount", "sittingCount")
        .contains(
            tuple(1, 1, 0)
        );
  }

  @DisplayName("복귀 시 이전 웨이팅 이력이 고객요청 취소라면 취소 카운트를 1 감소시킨다.")
  @Test
  void undoAfterCancelByCustomer() {
    // given
    String shopId = "shopId";
    String waitingId = "waitingId";

    ShopCustomerEntity shopCustomer = createShopCustomer(shopId, 1, 1, 1);

    createWaitingHistory(waitingId, WAITING, WaitingDetailStatus.WAITING);
    createWaitingHistory(waitingId, CANCEL, WaitingDetailStatus.CANCEL_BY_CUSTOMER);
    createWaitingHistory(waitingId, WAITING, WaitingDetailStatus.UNDO);

    // when
    shopCustomerService.undo(shopId, shopCustomer.getCustomerSeq(), waitingId);

    // then
    assertThat(shopCustomerRepository.findAll()).hasSize(1)
        .extracting("cancelCount", "noshowCount", "sittingCount")
        .contains(
            tuple(0, 1, 1)
        );
  }

  @DisplayName("복귀 시 이전 웨이팅 이력이 타매장방문 취소라면 취소 카운트를 1 감소시킨다.")
  @Test
  void undoAfterCancelBySitting() {
    // given
    String shopId = "shopId";
    String waitingId = "waitingId";

    ShopCustomerEntity shopCustomer = createShopCustomer(shopId, 1, 1, 1);

    createWaitingHistory(waitingId, WAITING, WaitingDetailStatus.WAITING);
    createWaitingHistory(waitingId, CANCEL, WaitingDetailStatus.CANCEL_BY_SITTING);
    createWaitingHistory(waitingId, WAITING, WaitingDetailStatus.UNDO);

    // when
    shopCustomerService.undo(shopId, shopCustomer.getCustomerSeq(), waitingId);

    // then
    assertThat(shopCustomerRepository.findAll()).hasSize(1)
        .extracting("cancelCount", "noshowCount", "sittingCount")
        .contains(
            tuple(0, 1, 1)
        );
  }

  @DisplayName("복귀 시 이전 웨이팅 이력이 매장요청 취소라면 취소 카운트를 1 감소시킨다.")
  @Test
  void undoAfterCancelByShop() {
    // given
    String shopId = "shopId";
    String waitingId = "waitingId";

    ShopCustomerEntity shopCustomer = createShopCustomer(shopId, 1, 1, 1);

    createWaitingHistory(waitingId, WAITING, WaitingDetailStatus.WAITING);
    createWaitingHistory(waitingId, CANCEL, WaitingDetailStatus.CANCEL_BY_SHOP);
    createWaitingHistory(waitingId, WAITING, WaitingDetailStatus.UNDO);

    // when
    shopCustomerService.undo(shopId, shopCustomer.getCustomerSeq(), waitingId);

    // then
    assertThat(shopCustomerRepository.findAll()).hasSize(1)
        .extracting("cancelCount", "noshowCount", "sittingCount")
        .contains(
            tuple(0, 1, 1)
        );
  }

  @DisplayName("복귀 시 이전 웨이팅 이력이 노쇼라면 취소 카운트와 노쇼 카운트를 각각 1 감소시킨다.")
  @Test
  void undoAfterCancelByNoShow() {
    // given
    String shopId = "shopId";
    String waitingId = "waitingId";

    ShopCustomerEntity shopCustomer = createShopCustomer(shopId, 1, 1, 1);

    createWaitingHistory(waitingId, WAITING, WaitingDetailStatus.WAITING);
    createWaitingHistory(waitingId, CANCEL, WaitingDetailStatus.CANCEL_BY_NO_SHOW);
    createWaitingHistory(waitingId, WAITING, WaitingDetailStatus.UNDO);

    // when
    shopCustomerService.undo(shopId, shopCustomer.getCustomerSeq(), waitingId);

    // then
    assertThat(shopCustomerRepository.findAll()).hasSize(1)
        .extracting("cancelCount", "noshowCount", "sittingCount")
        .contains(
            tuple(0, 0, 1)
        );
  }

  private ShopCustomerEntity createShopCustomer(String shopId, int cancelCount, int noShowCount,
      int sittingCount) {
    ShopCustomerEntity shopCustomer = ShopCustomerEntity.builder()
        .shopCustomerId(ShopCustomerId.builder()
            .shopId(shopId)
            .customerSeq(1L)
            .build())
        .name(null)
        .visitCount(0)
        .cancelCount(cancelCount)
        .noshowCount(noShowCount)
        .sittingCount(sittingCount)
        .build();
    return shopCustomerRepository.save(shopCustomer);
  }

  private WaitingHistoryEntity createWaitingHistory(String waitingId,
      WaitingStatus waitingStatus, WaitingDetailStatus waitingDetailStatus) {
    WaitingHistoryEntity history = WaitingHistoryEntity.builder()
        .shopId("shopId")
        .waitingSeq(1L)
        .waitingId(waitingId)
        .operationDate(LocalDate.of(2023, 3, 15))
        .waitingStatus(waitingStatus)
        .waitingDetailStatus(waitingDetailStatus)
        .seatOptionName("홀")
        .customerSeq(1L)
        .waitingCompleteDateTime(null)
        .waitingNumbers(
            WaitingNumber.builder()
                .waitingNumber(WaitingNumberConstructor.initWaitingNumber())
                .waitingOrder(1)
                .build()
        )
        .personOptionsData(PersonOptionsData.builder()
            .personOptions(List.of(PersonOption.builder()
                .name("유아")
                .count(1)
                .build()
            ))
            .build()
        )
        .build();
    return waitingHistoryRepository.save(history);
  }

}