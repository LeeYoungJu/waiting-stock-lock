package co.wadcorp.waiting.api.internal.service.waiting;

import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingOrderMenuResponse;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingOrderMenuResponse.CategoryDto;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingOrderMenuResponse.MenuDto;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.query.displaymenu.DisplayCategoryQuery;
import co.wadcorp.waiting.data.query.displaymenu.DisplayMenuQuery;
import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayCategoryDto;
import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayMenuDto;
import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayMenusDto;
import co.wadcorp.waiting.data.service.stock.StockService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RemoteDisplayMenuApiService {

  private final DisplayMenuQuery displayMenuQuery;
  private final DisplayCategoryQuery displayCategoryQuery;

  private final StockService stockService;

  public RemoteWaitingOrderMenuResponse getOrderMenu(ChannelShopIdMapping channelShopIdMapping,
      DisplayMappingType displayMappingType, LocalDate operationDate) {
    channelShopIdMapping.checkOnlyOneShopId();
    String shopId = channelShopIdMapping.getFirstWaitingShopId();

    DisplayMenusDto displayMenuDto = new DisplayMenusDto(
        displayMenuQuery.getDisplayMenu(shopId, displayMappingType)
    );

    List<DisplayCategoryDto> displayCategory = displayCategoryQuery.getDisplayCategory(
        displayMenuDto.getCategoryIds(),
        displayMappingType
    );
    Map<String, List<DisplayMenuDto>> collectCategoryMenus = displayMenuDto.toMapByCategoryId();

    Map<String, StockEntity> collectStocks = stockService.getStocks(
            displayMenuDto.getMenuIds(),
            operationDate
        )
        .stream()
        .collect(Collectors.toMap(StockEntity::getMenuId, item -> item));

    return RemoteWaitingOrderMenuResponse.of(displayCategory, collectCategoryMenus, collectStocks);
  }

}
