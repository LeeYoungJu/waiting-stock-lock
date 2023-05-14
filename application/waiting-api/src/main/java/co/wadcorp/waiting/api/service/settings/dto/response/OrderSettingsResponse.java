package co.wadcorp.waiting.api.service.settings.dto.response;


import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayMenuNamePriceDto;
import co.wadcorp.waiting.data.query.menu.dto.MenuNamePriceDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderSettingsResponse {

  private final Boolean isPossibleOrder;
  private final List<MenuDto> menus;
  private final List<MenuDto> shopMenus;
  private final List<MenuDto> takeOutMenus;

  private final Boolean existsWaitingTeam;  // 웨이팅 중인 팀이 존재하면 설정을 수정할 수 없다.
  private final Boolean isOpenedOperation;  // 웨이팅 접수 중인경우 설정을 수정할 수 없다.


  @Builder
  private OrderSettingsResponse(Boolean isPossibleOrder, List<MenuDto> menus,
      List<MenuDto> shopMenus, List<MenuDto> takeOutMenus, Boolean existsWaitingTeam,
      Boolean isOpenedOperation) {
    this.isPossibleOrder = isPossibleOrder;
    this.menus = menus;
    this.shopMenus = shopMenus;
    this.takeOutMenus = takeOutMenus;
    this.existsWaitingTeam = existsWaitingTeam;
    this.isOpenedOperation = isOpenedOperation;
  }

  @Getter
  public static class MenuDto {

    private final String id;
    private final String name;

    @Builder
    private MenuDto(String id, String name) {
      this.id = id;
      this.name = name;
    }

    public static MenuDto of(MenuNamePriceDto menuDto) {
      return OrderSettingsResponse.MenuDto.builder()
          .id(menuDto.getMenuId())
          .name(menuDto.getName())
          .build();
    }

    public static MenuDto of(DisplayMenuNamePriceDto displayMenuNamePriceDto) {
      return OrderSettingsResponse.MenuDto.builder()
          .id(displayMenuNamePriceDto.getMenuId())
          .name(displayMenuNamePriceDto.getName())
          .build();
    }

  }
}
