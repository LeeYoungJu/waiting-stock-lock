package co.wadcorp.waiting.api.service.waiting.register;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.service.waiting.register.dto.request.ValidateWaitingOrderManuStockServiceRequest;
import co.wadcorp.waiting.api.service.waiting.register.dto.request.ValidateWaitingOrderManuStockServiceRequest.Menu;
import co.wadcorp.waiting.api.service.waiting.register.dto.response.RegisterValidateMenuResponse;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.menu.MenuRepository;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.StockRepository;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.support.Price;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RegisterValidateMenuApiServiceTest extends IntegrationTest {

  @Autowired
  private RegisterValidateMenuApiService registerValidateMenuApiService;
  @Autowired
  private MenuRepository menuRepository;
  @Autowired
  private StockRepository stockRepository;

  @Test
  @DisplayName("재고 validation - 정상")
  void validateStock() {
    // given
    String SHOP_ID = "SHOP_ID";
    LocalDate operationDate = LocalDate.of(2023, 3, 27);
    String menu1Id = UUIDUtil.shortUUID();
    String menu2Id = UUIDUtil.shortUUID();

    MenuEntity menuEntity1 = MenuEntity.builder()
        .menuId(menu1Id)
        .shopId(SHOP_ID)
        .name("메뉴1")
        .unitPrice(Price.of(100))
        .isUsedDailyStock(true)
        .dailyStock(1000)
        .build();
    menuRepository.save(menuEntity1);

    MenuEntity menuEntity2 = MenuEntity.builder()
        .menuId(menu2Id)
        .shopId(SHOP_ID)
        .name("메뉴2")
        .unitPrice(Price.of(100))
        .build();
    menuRepository.save(menuEntity2);

    stockRepository.save(StockEntity.builder().menuId(menu1Id).operationDate(operationDate).isUsedDailyStock(true).stock(1000).salesQuantity(0).build());
    stockRepository.save(StockEntity.builder().menuId(menu2Id).operationDate(operationDate).isUsedDailyStock(true).stock(1000).salesQuantity(0).build());

    Menu menu1 = Menu.builder()
        .id(menu1Id)
        .name("메뉴1")
        .quantity(1)
        .build();

    Menu menu2 = Menu.builder()
        .id(menu2Id)
        .name("메뉴2")
        .quantity(1)
        .build();

    ValidateWaitingOrderManuStockServiceRequest request = ValidateWaitingOrderManuStockServiceRequest.builder()
        .menus(List.of(
            menu1, menu2
        ))
        .build();

    // when
    // then
    assertDoesNotThrow(
        () -> registerValidateMenuApiService.validateStock(SHOP_ID, operationDate, request)
    );

  }

  @Test
  @DisplayName("재고 validation - 팀당 주문 가능 수량을 초과")
  void invalidStock_exceedingOrderQuantityPerTeam() {

    // given
    String SHOP_ID = "SHOP_ID";
    LocalDate operationDate = LocalDate.of(2023, 3, 27);
    String menu1Id = UUIDUtil.shortUUID();
    String menu2Id = UUIDUtil.shortUUID();

    MenuEntity menuEntity1 = MenuEntity.builder()
        .menuId(menu1Id)
        .shopId(SHOP_ID)
        .name("메뉴1")
        .unitPrice(Price.of(100))
        .isUsedMenuQuantityPerTeam(true)
        .menuQuantityPerTeam(2)
        .build();
    menuRepository.save(menuEntity1);

    MenuEntity menuEntity2 = MenuEntity.builder()
        .menuId(menu2Id)
        .shopId(SHOP_ID)
        .name("메뉴2")
        .unitPrice(Price.of(100))
        .isUsedMenuQuantityPerTeam(true)
        .menuQuantityPerTeam(2)
        .build();
    menuRepository.save(menuEntity2);

    stockRepository.save(StockEntity.builder().menuId(menu1Id).operationDate(operationDate).isUsedDailyStock(true).stock(1000).salesQuantity(0).build());
    stockRepository.save(StockEntity.builder().menuId(menu2Id).operationDate(operationDate).isUsedDailyStock(true).stock(1000).salesQuantity(0).build());

    Menu menu1 = Menu.builder()
        .id(menu1Id)
        .name("메뉴1")
        .quantity(3)
        .build();

    Menu menu2 = Menu.builder()
        .id(menu2Id)
        .name("메뉴2")
        .quantity(2)
        .build();

    ValidateWaitingOrderManuStockServiceRequest request = ValidateWaitingOrderManuStockServiceRequest.builder()
        .menus(List.of(
            menu1, menu2
        ))
        .build();

    // when

    AppException appException = assertThrows(AppException.class,
        () -> registerValidateMenuApiService.validateStock(SHOP_ID, operationDate, request)
    );

    // then
    RegisterValidateMenuResponse data = (RegisterValidateMenuResponse) appException.getData();

    assertEquals(ErrorCode.EXCEEDING_ORDER_QUANTITY_PER_TEAM.getMessage(),
        appException.getMessage());
    assertEquals(ErrorCode.EXCEEDING_ORDER_QUANTITY_PER_TEAM.getCode(), data.getReason());
    assertThat(data.getMenus()).hasSize(1)
        .extracting(
            "id", "name", "quantity", "remainingQuantity"
        )
        .containsExactly(
            Tuple.tuple(menu1Id, "메뉴1", 3, 1000)
        );
  }

  @Test
  @DisplayName("재고 validation - 재고 소진으로 주문 불가")
  void invalidStock_outOfStock() {

    // given
    String SHOP_ID = "SHOP_ID";
    LocalDate operationDate = LocalDate.of(2023, 3, 27);
    String menu1Id = UUIDUtil.shortUUID();
    String menu2Id = UUIDUtil.shortUUID();

    MenuEntity menuEntity1 = MenuEntity.builder()
        .menuId(menu1Id)
        .shopId(SHOP_ID)
        .name("메뉴1")
        .isUsedDailyStock(true)
        .dailyStock(10)
        .unitPrice(Price.of(100))
        .build();
    menuRepository.save(menuEntity1);

    MenuEntity menuEntity2 = MenuEntity.builder()
        .menuId(menu2Id)
        .shopId(SHOP_ID)
        .name("메뉴2")
        .isUsedDailyStock(true)
        .dailyStock(10)
        .unitPrice(Price.of(100))
        .build();
    menuRepository.save(menuEntity2);

    stockRepository.save(StockEntity.builder().menuId(menu1Id).operationDate(operationDate).isUsedDailyStock(true).stock(10).salesQuantity(0).build());
    stockRepository.save(StockEntity.builder().menuId(menu2Id).operationDate(operationDate).isUsedDailyStock(true).stock(10).salesQuantity(0).build());

    Menu menu1 = Menu.builder()
        .id(menu1Id)
        .name("메뉴1")
        .quantity(11)
        .build();

    Menu menu2 = Menu.builder()
        .id(menu2Id)
        .name("메뉴2")
        .quantity(11)
        .build();

    ValidateWaitingOrderManuStockServiceRequest request = ValidateWaitingOrderManuStockServiceRequest.builder()
        .menus(List.of(
            menu1, menu2
        ))
        .build();

    // when
    AppException appException = assertThrows(AppException.class,
        () -> registerValidateMenuApiService.validateStock(SHOP_ID, operationDate, request)
    );

    // then
    RegisterValidateMenuResponse data = (RegisterValidateMenuResponse) appException.getData();

    assertEquals(ErrorCode.OUT_OF_STOCK.getMessage(),
        appException.getMessage());
    assertEquals(ErrorCode.OUT_OF_STOCK.getCode(), data.getReason());
    assertThat(data.getMenus()).hasSize(2)
        .extracting(
            "id", "name", "quantity", "remainingQuantity"
        )
        .containsExactly(
            Tuple.tuple(menu1Id, "메뉴1", 11, 10),
            Tuple.tuple(menu2Id, "메뉴2", 11, 10)
        );
  }
}