package co.wadcorp.waiting.api.service.waiting.management;

import static co.wadcorp.libs.stream.StreamUtils.convert;
import static co.wadcorp.libs.stream.StreamUtils.convertToMap;

import co.wadcorp.waiting.api.service.waiting.management.dto.request.UpdateStockServiceRequest;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementStockListResponse;
import co.wadcorp.waiting.data.domain.menu.CategoryEntity;
import co.wadcorp.waiting.data.domain.menu.CategoryMenuEntity;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.event.ChangedMenuStockEvent;
import co.wadcorp.waiting.data.query.menu.CategoryMenuQueryRepository;
import co.wadcorp.waiting.data.query.menu.CategoryQueryRepository;
import co.wadcorp.waiting.data.query.menu.MenuQueryRepository;
import co.wadcorp.waiting.data.query.stock.StockQueryRepository;
import co.wadcorp.waiting.data.service.stock.StockService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ManagementStockApiService {

  private final CategoryQueryRepository categoryQueryRepository;
  private final MenuQueryRepository menuQueryRepository;
  private final CategoryMenuQueryRepository categoryMenuQueryRepository;
  private final StockQueryRepository stockQueryRepository;
  private final StockService stockService;

  private final ApplicationEventPublisher eventPublisher;

  public ManagementStockListResponse getStocks(String shopId, LocalDate operationDate) {
    List<CategoryEntity> categories = categoryQueryRepository.findAllBy(shopId);
    Map<String, Set<String>> categoryMenuGroupingMap = createCategoryMenuMap(categories);

    List<MenuEntity> menus = menuQueryRepository.findAllBy(shopId);
    Map<String, MenuEntity> menuMap = convertToMap(menus, MenuEntity::getMenuId);
    Map<String, StockEntity> stockMap = createStockMap(menus, operationDate);

    return ManagementStockListResponse.of(categories, categoryMenuGroupingMap, menuMap, stockMap);
  }

  @Transactional
  public ManagementStockListResponse updateStocks(String shopId,
      UpdateStockServiceRequest request, LocalDate operationDate, String deviceId) {
    stockService.addDailyStockAndUpdateOutOfStock(request.getMenuDtos(), operationDate);
    eventPublisher.publishEvent(new ChangedMenuStockEvent(shopId, deviceId));

    return getStocks(shopId, operationDate);
  }

  private Map<String, Set<String>> createCategoryMenuMap(List<CategoryEntity> categories) {
    List<String> categoryIds = convert(categories, CategoryEntity::getCategoryId);
    List<CategoryMenuEntity> categoryMenus = categoryMenuQueryRepository.findAllBy(categoryIds);

    return categoryMenus.stream()
        .collect(Collectors.groupingBy(
            CategoryMenuEntity::getCategoryId,
            Collectors.mapping(CategoryMenuEntity::getMenuId, Collectors.toSet())
        ));
  }

  private Map<String, StockEntity> createStockMap(List<MenuEntity> menus, LocalDate operationDate) {
    List<String> menuIds = convert(menus, MenuEntity::getMenuId);
    List<StockEntity> stocks = stockQueryRepository.findAllBy(menuIds, operationDate);

    return convertToMap(stocks, StockEntity::getMenuId);
  }

}
