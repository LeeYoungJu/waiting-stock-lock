package co.wadcorp.waiting.data.service.order;

import co.wadcorp.waiting.IntegrationTest;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.menu.MenuRepository;
import co.wadcorp.waiting.data.domain.order.*;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.StockRepository;
import co.wadcorp.waiting.data.support.Price;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest extends IntegrationTest {

    @Autowired
    private OrderSaveService orderSaveService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderLineItemRepository orderLineItemRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private StockRepository stockRepository;

    @Test
    void saveTest() throws InterruptedException {
        int threadCnt = 100;

        LocalDate operationDate = LocalDate.of(2023, 2, 25);
        String shopId = "SHOP_ID";
        String orderId = "ORDER_ID";
        String waitingId = "WAITING_ID";
        String menuId1 = "MENU_ID_1";

        createMenu(shopId, menuId1, "menu1", 1, 1000, 100);
        createStock(menuId1, operationDate, 100);

        OrderLineItemEntity menu1 = createOrderLineItem(orderId, "MENU_ID_1", "menu1", 1000, 1);
        List<OrderEntity> orders = new ArrayList<>();
        IntStream.range(0, threadCnt).forEach(i -> orders.add(
                createOrderEntity(shopId, "ORDER_ID_"+i, waitingId, operationDate, List.of(menu1))
        ));

        StockEntity beforeStock = stockRepository.findByMenuIdAndOperationDate(menuId1, operationDate).get();
        System.out.println("=================================");
        System.out.println(beforeStock.getSalesQuantity());
        System.out.println("=================================");

        ExecutorService executorService = Executors.newFixedThreadPool(30);
        CountDownLatch latch = new CountDownLatch(threadCnt);

        for (int i = 0; i < threadCnt; i++) {
            final int idx = i;
            executorService.submit(() -> {
                try {
                    orderSaveService.save(orders.get(idx));
                } catch (Exception e) {
                    System.out.println(e);
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        StockEntity stock = stockRepository.findByMenuIdAndOperationDate(menuId1, operationDate).get();
        assertEquals(100, stock.getSalesQuantity());
    }

    private MenuEntity createMenu(String shopId, String menuId, String name, int ordering,
                                int unitPrice, int dailyStock) {
        return menuRepository.save(MenuEntity.builder()
                .shopId(shopId)
                .menuId(menuId)
                .name(name)
                .ordering(ordering)
                .unitPrice(Price.of(unitPrice))
                .isUsedDailyStock(true)
                .dailyStock(dailyStock)
                .build()
        );
    }

    private StockEntity createStock(String menuId, LocalDate operationDate, int stock) {
        return stockRepository.save(StockEntity.builder()
                .menuId(menuId)
                .operationDate(operationDate)
                .isUsedDailyStock(true)
                .stock(stock)
                .salesQuantity(0)
                .isOutOfStock(false)
                .build()
        );
    }

    private OrderLineItemEntity createOrderLineItem(String orderId, String menuId, String menuName, int unitPrice, int quantity) {
        OrderLineItemEntity orderLineItemEntity = OrderLineItemEntity.builder()
                .orderId(orderId)
                .menuId(menuId)
                .menuName(menuName)
                .unitPrice(Price.of(unitPrice))
                .linePrice(Price.of(unitPrice * quantity))
                .quantity(quantity)
                .build();

        return orderLineItemRepository.save(orderLineItemEntity);
    }

    private OrderEntity createOrderEntity(String shopId, String orderId, String waitingId, LocalDate operationDate,
                                   List<OrderLineItemEntity> orderLineItems) {
        OrderEntity orderEntity = OrderEntity.builder()
                .orderId(orderId)
                .shopId(shopId)
                .waitingId(waitingId)
                .operationDate(operationDate)
                .orderType(OrderType.SHOP)
                .totalPrice(Price.of(1000))
                .orderLineItems(orderLineItems)
                .build();

        return orderRepository.save(orderEntity);
    }
}
