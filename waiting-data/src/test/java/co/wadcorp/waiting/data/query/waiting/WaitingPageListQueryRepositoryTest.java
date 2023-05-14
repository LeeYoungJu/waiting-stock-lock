package co.wadcorp.waiting.data.query.waiting;

import static co.wadcorp.waiting.data.domain.waiting.WaitingStatus.CANCEL;
import static co.wadcorp.waiting.data.domain.waiting.WaitingStatus.SITTING;
import static co.wadcorp.waiting.data.domain.waiting.WaitingStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.IntegrationTest;
import co.wadcorp.waiting.data.config.WaitingNumberConstructor;
import co.wadcorp.waiting.data.domain.customer.CustomerEntity;
import co.wadcorp.waiting.data.domain.customer.CustomerRepository;
import co.wadcorp.waiting.data.domain.customer.ShopCustomerEntity;
import co.wadcorp.waiting.data.domain.customer.ShopCustomerId;
import co.wadcorp.waiting.data.domain.customer.ShopCustomerRepository;
import co.wadcorp.waiting.data.domain.shop.ShopEntity;
import co.wadcorp.waiting.data.domain.shop.ShopRepository;
import co.wadcorp.waiting.data.domain.shop.operation.status.RegistrableStatus;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingOnRegistrationDto;
import co.wadcorp.waiting.data.query.waiting.dto.WebWaitingDto;
import co.wadcorp.waiting.shared.util.PhoneNumberUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class WaitingPageListQueryRepositoryTest extends IntegrationTest {

  @Autowired
  private WaitingPageListQueryRepository waitingPageListQueryRepository;

  @Autowired
  private WaitingRepository waitingRepository;

  @Autowired
  private ShopRepository shopRepository;

  @Autowired
  private ShopOperationInfoRepository shopOperationInfoRepository;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private ShopCustomerRepository shopCustomerRepository;

  @DisplayName("매장 ID, 운영일, 웨이팅 상태로 웨이팅 목록을 조회한다. 상태가 WAITING인 경우를 조회할 때는 웨이팅순서 정순으로 페이징한다.")
  @Test
  void getDefaultWaitingList() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 4, 11);
    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";

    ShopEntity shop1 = createShop(shopId1, "shopName-1");
    ShopEntity shop2 = createShop(shopId2, "shopName-2");

    CustomerEntity customer1 = createCustomer("010-0000-0001");
    CustomerEntity customer2 = createCustomer("010-0000-0002");
    CustomerEntity customer3 = createCustomer("010-0000-0003");
    CustomerEntity customer4 = createCustomer("010-0000-0004");
    createShopCustomer(customer1, shop1);
    createShopCustomer(customer1, shop2);
    createShopCustomer(customer2, shop1);
    createShopCustomer(customer2, shop2);
    createShopCustomer(customer3, shop1);
    createShopCustomer(customer3, shop2);
    createShopCustomer(customer4, shop1);
    createShopCustomer(customer4, shop2);

    createWaiting(shopId1, operationDate, WAITING, WaitingDetailStatus.WAITING, customer1.getSeq(),
        1, "홀");
    createWaiting(shopId1, operationDate, WAITING, WaitingDetailStatus.WAITING, customer2.getSeq(),
        2, "홀");
    createWaiting(shopId1, operationDate, WAITING, WaitingDetailStatus.WAITING, customer3.getSeq(),
        3, "홀");
    createWaiting(shopId2, operationDate, WAITING, WaitingDetailStatus.WAITING, customer1.getSeq(),
        1, "홀");
    createWaiting(shopId1, operationDate.minusDays(1), WAITING, WaitingDetailStatus.WAITING,
        customer1.getSeq(), 1, "홀");
    createWaiting(shopId1, operationDate, SITTING, WaitingDetailStatus.SITTING, customer4.getSeq(),
        1, "홀");

    // when
    Page<WaitingDto> results = waitingPageListQueryRepository.getDefaultWaitingList(shopId1,
        operationDate, WAITING, PageRequest.of(0, 2));

    // then
    assertThat(results).hasSize(2)
        .extracting("shopId", "operationDate", "customerSeq", "waitingOrder")
        .containsExactly(
            tuple(shopId1, operationDate, customer1.getSeq(), 1),
            tuple(shopId1, operationDate, customer2.getSeq(), 2)
        );
  }

  @DisplayName("수동 등록으로 인해 고객 정보가 없어도 매장 ID, 운영일, 웨이팅 상태로 웨이팅 목록을 조회할 수 있다.")
  @Test
  void getDefaultWaitingListWithoutCustomer() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 4, 11);
    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";

    ShopEntity shop1 = createShop(shopId1, "shopName-1");
    ShopEntity shop2 = createShop(shopId2, "shopName-2");

    createWaiting(shopId1, operationDate, WAITING, WaitingDetailStatus.WAITING, null, 1, "홀");
    createWaiting(shopId1, operationDate, WAITING, WaitingDetailStatus.WAITING, null, 2, "홀");
    createWaiting(shopId1, operationDate, WAITING, WaitingDetailStatus.WAITING, null, 3, "홀");
    createWaiting(shopId2, operationDate, WAITING, WaitingDetailStatus.WAITING, null, 1, "홀");
    createWaiting(shopId1, operationDate.minusDays(1), WAITING, WaitingDetailStatus.WAITING, null,
        1, "홀");
    createWaiting(shopId1, operationDate, SITTING, WaitingDetailStatus.SITTING, null, 1, "홀");

    // when
    Page<WaitingDto> results = waitingPageListQueryRepository.getDefaultWaitingList(shopId1,
        operationDate, WAITING, PageRequest.of(0, 2));

    // then
    assertThat(results).hasSize(2)
        .extracting("shopId", "operationDate", "customerSeq", "waitingOrder")
        .containsExactly(
            tuple(shopId1, operationDate, 0L, 1),
            tuple(shopId1, operationDate, 0L, 2)
        );
  }

  @DisplayName("매장 ID, 운영일, 웨이팅 상태로 웨이팅 목록을 조회할 때, 상태가 SITTING인 경우를 조회할 때는 웨이팅 완료시간 역순으로 페이징한다.")
  @Test
  void getDefaultSittingWaitingList() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 4, 11);
    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";

    ShopEntity shop1 = createShop(shopId1, "shopName-1");
    ShopEntity shop2 = createShop(shopId2, "shopName-2");

    CustomerEntity customer1 = createCustomer("010-0000-0001");
    CustomerEntity customer2 = createCustomer("010-0000-0002");
    CustomerEntity customer3 = createCustomer("010-0000-0003");
    CustomerEntity customer4 = createCustomer("010-0000-0004");
    createShopCustomer(customer1, shop1);
    createShopCustomer(customer1, shop2);
    createShopCustomer(customer2, shop1);
    createShopCustomer(customer2, shop2);
    createShopCustomer(customer3, shop1);
    createShopCustomer(customer3, shop2);
    createShopCustomer(customer4, shop1);
    createShopCustomer(customer4, shop2);

    WaitingEntity waiting1 = createWaiting(shopId1, operationDate, SITTING,
        WaitingDetailStatus.SITTING, customer1.getSeq(), 1, LocalDateTime.of(2023, 4, 11, 10, 10),
        "홀");
    WaitingEntity waiting2 = createWaiting(shopId1, operationDate, SITTING,
        WaitingDetailStatus.SITTING, customer2.getSeq(), 2, LocalDateTime.of(2023, 4, 11, 10, 20),
        "홀");
    WaitingEntity waiting3 = createWaiting(shopId1, operationDate, SITTING,
        WaitingDetailStatus.SITTING, customer3.getSeq(), 3, LocalDateTime.of(2023, 4, 11, 10, 30),
        "홀");
    WaitingEntity waiting4 = createWaiting(shopId2, operationDate, SITTING,
        WaitingDetailStatus.SITTING, customer1.getSeq(), 1, LocalDateTime.of(2023, 4, 11, 10, 0),
        "홀");
    WaitingEntity waiting5 = createWaiting(shopId1, operationDate.minusDays(1), SITTING,
        WaitingDetailStatus.SITTING, customer1.getSeq(), 1, LocalDateTime.of(2023, 4, 10, 10, 0),
        "홀");
    WaitingEntity waiting6 = createWaiting(shopId1, operationDate, WAITING,
        WaitingDetailStatus.WAITING, customer4.getSeq(), 1, LocalDateTime.of(2023, 4, 11, 10, 0),
        "홀");

    // when
    Page<WaitingDto> results = waitingPageListQueryRepository.getDefaultWaitingList(shopId1,
        operationDate, SITTING, PageRequest.of(0, 2));

    // then
    assertThat(results).hasSize(2)
        .extracting("waitingId")
        .containsExactly(waiting3.getWaitingId(), waiting2.getWaitingId());
  }

  @DisplayName("매장 ID, 운영일, 웨이팅 상태로 웨이팅 목록을 조회할 때, 상태가 CANCEL인 경우를 조회할 때는 웨이팅 완료시간 역순으로 페이징한다.")
  @Test
  void getDefaultCancelWaitingList() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 4, 11);
    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";

    ShopEntity shop1 = createShop(shopId1, "shopName-1");
    ShopEntity shop2 = createShop(shopId2, "shopName-2");

    CustomerEntity customer1 = createCustomer("010-0000-0001");
    CustomerEntity customer2 = createCustomer("010-0000-0002");
    CustomerEntity customer3 = createCustomer("010-0000-0003");
    CustomerEntity customer4 = createCustomer("010-0000-0004");
    createShopCustomer(customer1, shop1);
    createShopCustomer(customer1, shop2);
    createShopCustomer(customer2, shop1);
    createShopCustomer(customer2, shop2);
    createShopCustomer(customer3, shop1);
    createShopCustomer(customer3, shop2);
    createShopCustomer(customer4, shop1);
    createShopCustomer(customer4, shop2);

    WaitingEntity waiting1 = createWaiting(shopId1, operationDate, CANCEL,
        WaitingDetailStatus.CANCEL_BY_NO_SHOW, customer1.getSeq(), 1,
        LocalDateTime.of(2023, 4, 11, 10, 10), "홀");
    WaitingEntity waiting2 = createWaiting(shopId1, operationDate, CANCEL,
        WaitingDetailStatus.CANCEL_BY_NO_SHOW, customer2.getSeq(), 2,
        LocalDateTime.of(2023, 4, 11, 10, 20), "홀");
    WaitingEntity waiting3 = createWaiting(shopId1, operationDate, CANCEL,
        WaitingDetailStatus.CANCEL_BY_NO_SHOW, customer3.getSeq(), 3,
        LocalDateTime.of(2023, 4, 11, 10, 30), "홀");
    WaitingEntity waiting4 = createWaiting(shopId2, operationDate, CANCEL,
        WaitingDetailStatus.CANCEL_BY_NO_SHOW, customer1.getSeq(), 1,
        LocalDateTime.of(2023, 4, 11, 10, 0), "홀");
    WaitingEntity waiting5 = createWaiting(shopId1, operationDate.minusDays(1), CANCEL,
        WaitingDetailStatus.CANCEL_BY_NO_SHOW, customer1.getSeq(), 1,
        LocalDateTime.of(2023, 4, 10, 10, 0), "홀");
    WaitingEntity waiting6 = createWaiting(shopId1, operationDate, WAITING,
        WaitingDetailStatus.WAITING, customer4.getSeq(), 1, LocalDateTime.of(2023, 4, 11, 10, 0),
        "홀");

    // when
    Page<WaitingDto> results = waitingPageListQueryRepository.getDefaultWaitingList(shopId1,
        operationDate, CANCEL, PageRequest.of(0, 2));

    // then
    assertThat(results).hasSize(2)
        .extracting("waitingId")
        .containsExactly(waiting3.getWaitingId(), waiting2.getWaitingId());
  }

  @DisplayName("매장 ID, 운영일, 웨이팅 상태, 테이블 이름으로 웨이팅 목록을 조회한다. 상태가 WAITING인 경우를 조회할 때는 웨이팅순서 정순으로 페이징한다.")
  @Test
  void getTableWaitingList() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 4, 11);
    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";

    ShopEntity shop1 = createShop(shopId1, "shopName-1");
    ShopEntity shop2 = createShop(shopId2, "shopName-2");

    CustomerEntity customer1 = createCustomer("010-0000-0001");
    CustomerEntity customer2 = createCustomer("010-0000-0002");
    CustomerEntity customer3 = createCustomer("010-0000-0003");
    CustomerEntity customer4 = createCustomer("010-0000-0004");
    CustomerEntity customer5 = createCustomer("010-0000-0005");
    createShopCustomer(customer1, shop1);
    createShopCustomer(customer1, shop2);
    createShopCustomer(customer2, shop1);
    createShopCustomer(customer2, shop2);
    createShopCustomer(customer3, shop1);
    createShopCustomer(customer3, shop2);
    createShopCustomer(customer4, shop1);
    createShopCustomer(customer4, shop2);
    createShopCustomer(customer5, shop1);
    createShopCustomer(customer5, shop2);

    createWaiting(shopId1, operationDate, WAITING, WaitingDetailStatus.WAITING, customer1.getSeq(),
        1, "홀");
    createWaiting(shopId1, operationDate, WAITING, WaitingDetailStatus.WAITING, customer2.getSeq(),
        2, "홀");
    createWaiting(shopId1, operationDate, WAITING, WaitingDetailStatus.WAITING, customer3.getSeq(),
        3, "홀");
    createWaiting(shopId2, operationDate, WAITING, WaitingDetailStatus.WAITING, customer1.getSeq(),
        1, "홀");
    createWaiting(shopId1, operationDate.minusDays(1), WAITING, WaitingDetailStatus.WAITING,
        customer1.getSeq(), 1, "홀");
    createWaiting(shopId1, operationDate, SITTING, WaitingDetailStatus.SITTING, customer4.getSeq(),
        1, "홀");
    createWaiting(shopId1, operationDate, WAITING, WaitingDetailStatus.WAITING, customer5.getSeq(),
        1, "바");

    // when
    Page<WaitingDto> results = waitingPageListQueryRepository.getTableWaitingList(shopId1,
        operationDate, WAITING, List.of("홀"), PageRequest.of(0, 2));

    // then
    assertThat(results).hasSize(2)
        .extracting("shopId", "operationDate", "customerSeq", "waitingOrder")
        .containsExactly(
            tuple(shopId1, operationDate, customer1.getSeq(), 1),
            tuple(shopId1, operationDate, customer2.getSeq(), 2)
        );
  }

  @DisplayName("수동 등록으로 인해 고객 정보가 없어도 매장 ID, 운영일, 웨이팅 상태, 테이블 이름으로 웨이팅 목록을 조회할 수 있다.")
  @Test
  void getTableWaitingListWithoutCustomer() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 4, 11);
    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";

    ShopEntity shop1 = createShop(shopId1, "shopName-1");
    ShopEntity shop2 = createShop(shopId2, "shopName-2");

    createWaiting(shopId1, operationDate, WAITING, WaitingDetailStatus.WAITING, null, 1, "홀");
    createWaiting(shopId1, operationDate, WAITING, WaitingDetailStatus.WAITING, null, 2, "홀");
    createWaiting(shopId1, operationDate, WAITING, WaitingDetailStatus.WAITING, null, 3, "홀");
    createWaiting(shopId2, operationDate, WAITING, WaitingDetailStatus.WAITING, null, 1, "홀");
    createWaiting(shopId1, operationDate.minusDays(1), WAITING, WaitingDetailStatus.WAITING, null,
        1, "홀");
    createWaiting(shopId1, operationDate, SITTING, WaitingDetailStatus.SITTING, null, 1, "홀");

    // when
    Page<WaitingDto> results = waitingPageListQueryRepository.getTableWaitingList(shopId1,
        operationDate, WAITING, List.of("홀"), PageRequest.of(0, 2));

    // then
    assertThat(results).hasSize(2)
        .extracting("shopId", "operationDate", "customerSeq", "waitingOrder")
        .containsExactly(
            tuple(shopId1, operationDate, 0L, 1),
            tuple(shopId1, operationDate, 0L, 2)
        );
  }

  @DisplayName("매장 ID, 운영일, 웨이팅 상태, 테이블 이름으로 웨이팅 목록을 조회할 때, 상태가 SITTING인 경우를 조회할 때는 웨이팅 완료시간 역순으로 페이징한다.")
  @Test
  void getTableSittingWaitingList() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 4, 11);
    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";

    ShopEntity shop1 = createShop(shopId1, "shopName-1");
    ShopEntity shop2 = createShop(shopId2, "shopName-2");

    CustomerEntity customer1 = createCustomer("010-0000-0001");
    CustomerEntity customer2 = createCustomer("010-0000-0002");
    CustomerEntity customer3 = createCustomer("010-0000-0003");
    CustomerEntity customer4 = createCustomer("010-0000-0004");
    createShopCustomer(customer1, shop1);
    createShopCustomer(customer1, shop2);
    createShopCustomer(customer2, shop1);
    createShopCustomer(customer2, shop2);
    createShopCustomer(customer3, shop1);
    createShopCustomer(customer3, shop2);
    createShopCustomer(customer4, shop1);
    createShopCustomer(customer4, shop2);

    WaitingEntity waiting1 = createWaiting(shopId1, operationDate, SITTING,
        WaitingDetailStatus.SITTING, customer1.getSeq(), 1, LocalDateTime.of(2023, 4, 11, 10, 10),
        "홀");
    WaitingEntity waiting2 = createWaiting(shopId1, operationDate, SITTING,
        WaitingDetailStatus.SITTING, customer2.getSeq(), 2, LocalDateTime.of(2023, 4, 11, 10, 20),
        "홀");
    WaitingEntity waiting3 = createWaiting(shopId1, operationDate, SITTING,
        WaitingDetailStatus.SITTING, customer3.getSeq(), 3, LocalDateTime.of(2023, 4, 11, 10, 30),
        "홀");
    WaitingEntity waiting4 = createWaiting(shopId2, operationDate, SITTING,
        WaitingDetailStatus.SITTING, customer1.getSeq(), 1, LocalDateTime.of(2023, 4, 11, 10, 0),
        "홀");
    WaitingEntity waiting5 = createWaiting(shopId1, operationDate.minusDays(1), SITTING,
        WaitingDetailStatus.SITTING, customer1.getSeq(), 1, LocalDateTime.of(2023, 4, 10, 10, 0),
        "홀");
    WaitingEntity waiting6 = createWaiting(shopId1, operationDate, WAITING,
        WaitingDetailStatus.WAITING, customer4.getSeq(), 1, LocalDateTime.of(2023, 4, 11, 10, 0),
        "홀");

    // when
    Page<WaitingDto> results = waitingPageListQueryRepository.getTableWaitingList(shopId1,
        operationDate, SITTING, List.of("홀"), PageRequest.of(0, 2));

    // then
    assertThat(results).hasSize(2)
        .extracting("waitingId")
        .containsExactly(waiting3.getWaitingId(), waiting2.getWaitingId());
  }

  @DisplayName("매장 ID, 운영일, 웨이팅 상태, 테이블 이름으로 웨이팅 목록을 조회할 때, 상태가 CANCEL인 경우를 조회할 때는 웨이팅 완료시간 역순으로 페이징한다.")
  @Test
  void getTableCancelWaitingList() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 4, 11);
    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";

    ShopEntity shop1 = createShop(shopId1, "shopName-1");
    ShopEntity shop2 = createShop(shopId2, "shopName-2");

    CustomerEntity customer1 = createCustomer("010-0000-0001");
    CustomerEntity customer2 = createCustomer("010-0000-0002");
    CustomerEntity customer3 = createCustomer("010-0000-0003");
    CustomerEntity customer4 = createCustomer("010-0000-0004");
    createShopCustomer(customer1, shop1);
    createShopCustomer(customer1, shop2);
    createShopCustomer(customer2, shop1);
    createShopCustomer(customer2, shop2);
    createShopCustomer(customer3, shop1);
    createShopCustomer(customer3, shop2);
    createShopCustomer(customer4, shop1);
    createShopCustomer(customer4, shop2);

    WaitingEntity waiting1 = createWaiting(shopId1, operationDate, CANCEL,
        WaitingDetailStatus.CANCEL_BY_NO_SHOW, customer1.getSeq(), 1,
        LocalDateTime.of(2023, 4, 11, 10, 10), "홀");
    WaitingEntity waiting2 = createWaiting(shopId1, operationDate, CANCEL,
        WaitingDetailStatus.CANCEL_BY_NO_SHOW, customer2.getSeq(), 2,
        LocalDateTime.of(2023, 4, 11, 10, 20), "홀");
    WaitingEntity waiting3 = createWaiting(shopId1, operationDate, CANCEL,
        WaitingDetailStatus.CANCEL_BY_NO_SHOW, customer3.getSeq(), 3,
        LocalDateTime.of(2023, 4, 11, 10, 30), "홀");
    WaitingEntity waiting4 = createWaiting(shopId2, operationDate, CANCEL,
        WaitingDetailStatus.CANCEL_BY_NO_SHOW, customer1.getSeq(), 1,
        LocalDateTime.of(2023, 4, 11, 10, 0), "홀");
    WaitingEntity waiting5 = createWaiting(shopId1, operationDate.minusDays(1), CANCEL,
        WaitingDetailStatus.CANCEL_BY_NO_SHOW, customer1.getSeq(), 1,
        LocalDateTime.of(2023, 4, 10, 10, 0), "홀");
    WaitingEntity waiting6 = createWaiting(shopId1, operationDate, WAITING,
        WaitingDetailStatus.WAITING, customer4.getSeq(), 1, LocalDateTime.of(2023, 4, 11, 10, 0),
        "홀");

    // when
    Page<WaitingDto> results = waitingPageListQueryRepository.getTableWaitingList(shopId1,
        operationDate, CANCEL, List.of("홀"), PageRequest.of(0, 2));

    // then
    assertThat(results).hasSize(2)
        .extracting("waitingId")
        .containsExactly(waiting3.getWaitingId(), waiting2.getWaitingId());
  }

  @DisplayName("매장 ID, 운영일로 등록 화면의 웨이팅 목록을 웨이팅 번호 정순으로 페이징 조회한다.")
  @Test
  void getWaitingListOnRegistration() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 4, 11);
    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";

    WaitingEntity waiting1 = createWaiting(shopId1, operationDate, WAITING,
        WaitingDetailStatus.WAITING, null, 1, "홀");
    WaitingEntity waiting2 = createWaiting(shopId1, operationDate, WAITING,
        WaitingDetailStatus.WAITING, null, 2, "홀");
    WaitingEntity waiting3 = createWaiting(shopId1, operationDate, WAITING,
        WaitingDetailStatus.WAITING, null, 3, "홀");
    WaitingEntity waiting4 = createWaiting(shopId2, operationDate, WAITING,
        WaitingDetailStatus.WAITING, null, 1, "홀");
    WaitingEntity waiting5 = createWaiting(shopId1, operationDate.minusDays(1), WAITING,
        WaitingDetailStatus.WAITING, null, 1, "홀");
    WaitingEntity waiting6 = createWaiting(shopId1, operationDate, SITTING,
        WaitingDetailStatus.SITTING, null, 1, "홀");

    // when
    Page<WaitingOnRegistrationDto> results = waitingPageListQueryRepository.getWaitingListOnRegistration(
        shopId1, operationDate, PageRequest.of(0, 2));

    // then
    assertThat(results).hasSize(2)
        .extracting("seq", "waitingNumber", "seatOptionName", "totalPersonCount")
        .containsExactly(
            tuple(waiting1.getSeq(), waiting1.getWaitingNumber(), "홀",
                waiting1.getTotalPersonCount()),
            tuple(waiting2.getSeq(), waiting2.getWaitingNumber(), "홀",
                waiting2.getTotalPersonCount())
        );
  }

  @DisplayName("고객 Seq와 운영일 날짜로 대기 중인 모든 웨이팅을 조회한다.")
  @Test
  void getAllWaitingByCustomer() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 2, 24);
    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";
    String shopId3 = "shopId-3";
    String shopName1 = "shopName-1";
    String shopName2 = "shopName-2";
    String shopName3 = "shopName-3";

    createShop(shopId1, shopName1);
    createShop(shopId2, shopName2);
    createShop(shopId3, shopName3);

    createShopOperationInfo(operationDate, shopId1);
    createShopOperationInfo(operationDate, shopId2);
    createShopOperationInfo(operationDate, shopId3);

    CustomerEntity customer = createCustomer("010-1234-1234");

    createWaiting(shopId1, operationDate, WAITING, WaitingDetailStatus.WAITING, customer.getSeq(),
        1, "홀");
    createWaiting(shopId2, operationDate, WAITING, WaitingDetailStatus.WAITING, customer.getSeq(),
        1, "홀");
    createWaiting(shopId3, operationDate, SITTING, WaitingDetailStatus.SITTING, customer.getSeq(),
        1, "홀");

    // when
    List<WebWaitingDto> results = waitingPageListQueryRepository.getAllWaitingByCustomer(
        customer.getSeq(), operationDate);

    // then
    assertThat(results).hasSize(2)
        .extracting("shopId")
        .contains(shopId1, shopId2);
  }

  private ShopOperationInfoEntity createShopOperationInfo(LocalDate operationDate, String shopId1) {
    ShopOperationInfoEntity shopOperationInfo = ShopOperationInfoEntity.builder()
        .shopId(shopId1)
        .operationDate(operationDate)
        .registrableStatus(RegistrableStatus.OPEN)
        .build();
    return shopOperationInfoRepository.save(shopOperationInfo);
  }

  private ShopEntity createShop(String shopId, String shopName) {
    ShopEntity shop = ShopEntity.builder()
        .shopId(shopId)
        .shopName(shopName)
        .shopAddress("shopAddress")
        .shopTelNumber("shopTelNumber")
        .build();
    return shopRepository.save(shop);
  }

  private CustomerEntity createCustomer(String encCustomerPhone) {
    CustomerEntity customer = CustomerEntity.builder()
        .encCustomerPhone(PhoneNumberUtils.ofKr(encCustomerPhone))
        .build();
    return customerRepository.save(customer);
  }

  private ShopCustomerEntity createShopCustomer(CustomerEntity customer, ShopEntity shop) {
    ShopCustomerEntity shopCustomer = ShopCustomerEntity.builder()
        .shopCustomerId(ShopCustomerId.builder()
            .shopId(shop.getShopId())
            .customerSeq(customer.getSeq())
            .build()
        )
        .name("customerName")
        .build();
    return shopCustomerRepository.save(shopCustomer);
  }

  private WaitingEntity createWaiting(
      String shopId, LocalDate operationDate, WaitingStatus waitingStatus,
      WaitingDetailStatus waitingDetailStatus, Long customerSeq, int waitingOrder,
      String seatOptionName) {
    WaitingEntity waiting = WaitingEntity.builder()
        .shopId(shopId)
        .waitingId(UUIDUtil.shortUUID().toUpperCase())
        .operationDate(operationDate)
        .waitingStatus(waitingStatus)
        .waitingDetailStatus(waitingDetailStatus)
        .seatOptionName(seatOptionName)
        .customerSeq(customerSeq)
        .waitingCompleteDateTime(null)
        .waitingNumbers(
            WaitingNumber.builder()
                .waitingNumber(WaitingNumberConstructor.initWaitingNumber())
                .waitingOrder(waitingOrder)
                .build()
        )
        .totalPersonCount(1)
        .build();
    return waitingRepository.save(waiting);
  }

  private WaitingEntity createWaiting(
      String shopId, LocalDate operationDate, WaitingStatus waitingStatus,
      WaitingDetailStatus waitingDetailStatus, Long customerSeq, int waitingOrder,
      LocalDateTime waitingCompleteDateTime, String seatOptionName) {
    WaitingEntity waiting = WaitingEntity.builder()
        .shopId(shopId)
        .waitingId(UUIDUtil.shortUUID().toUpperCase())
        .operationDate(operationDate)
        .waitingStatus(waitingStatus)
        .waitingDetailStatus(waitingDetailStatus)
        .seatOptionName(seatOptionName)
        .customerSeq(customerSeq)
        .waitingCompleteDateTime(ZonedDateTimeUtils.ofSeoul(waitingCompleteDateTime))
        .waitingNumbers(
            WaitingNumber.builder()
                .waitingNumber(WaitingNumberConstructor.initWaitingNumber())
                .waitingOrder(waitingOrder)
                .build()
        )
        .totalPersonCount(1)
        .build();
    return waitingRepository.save(waiting);
  }

}