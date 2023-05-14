package co.wadcorp.waiting.data.query.displaymenu.dto;

import static co.wadcorp.libs.stream.StreamUtils.convert;
import static co.wadcorp.libs.stream.StreamUtils.groupingByList;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class DisplayMenusDto {

  private final List<DisplayMenuDto> displayMenuDtos;

  public DisplayMenusDto(List<DisplayMenuDto> displayMenuDtos) {
    this.displayMenuDtos = displayMenuDtos;
  }

  public Set<String> getCategoryIds() {
    return displayMenuDtos.stream()
        .map(DisplayMenuDto::getCategoryId)
        .collect(Collectors.toSet());
  }

  public List<String> getMenuIds() {
    return convert(displayMenuDtos, DisplayMenuDto::getMenuId);
  }

  public Map<String, List<DisplayMenuDto>> toMapByCategoryId() {
    return groupingByList(displayMenuDtos, DisplayMenuDto::getCategoryId);
  }
}
