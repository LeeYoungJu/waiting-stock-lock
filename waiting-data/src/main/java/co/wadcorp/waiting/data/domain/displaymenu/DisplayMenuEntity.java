package co.wadcorp.waiting.data.domain.displaymenu;

import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.data.support.BooleanYnConverter;
import co.wadcorp.waiting.data.support.Price;
import co.wadcorp.waiting.data.support.PriceConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "seq", callSuper = false)
@Table(name = "cw_display_menu",
    indexes = {
        @Index(name = "cw_display_menu_category_id_index", columnList = "category_id"),
        @Index(name = "cw_display_menu_menu_id_index", columnList = "menu_id")
    }
)
@Entity
public class DisplayMenuEntity extends BaseEntity implements Comparable<DisplayMenuEntity> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "category_id")
  private String categoryId;

  @Column(name = "menu_id")
  private String menuId;

  @Column(name = "shop_id")
  private String shopId;

  @Column(name = "display_mapping_type")
  @Enumerated(EnumType.STRING)
  private DisplayMappingType displayMappingType;

  @Column(name = "ordering")
  private int ordering;

  @Column(name = "menu_name")
  private String menuName;

  @Column(name = "unit_price")
  @Convert(converter = PriceConverter.class)
  private Price unitPrice;

  @Column(name = "checked_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private Boolean isChecked;

  @Builder
  private DisplayMenuEntity(String categoryId, String menuId, String shopId,
      DisplayMappingType displayMappingType, int ordering, String menuName, Price unitPrice,
      boolean isChecked) {
    this.categoryId = categoryId;
    this.menuId = menuId;
    this.shopId = shopId;
    this.displayMappingType = displayMappingType;
    this.ordering = ordering;
    this.menuName = menuName;
    this.unitPrice = unitPrice;
    this.isChecked = isChecked;
  }

  public static DisplayMenuEntity of(MenuEntity menu, String categoryId,
      DisplayMappingType displayMappingType, int ordering) {
    return DisplayMenuEntity.builder()
        .categoryId(categoryId)
        .menuId(menu.getMenuId())
        .shopId(menu.getShopId())
        .displayMappingType(displayMappingType)
        .ordering(ordering)
        .menuName(menu.getName())
        .unitPrice(menu.getUnitPrice())
        .isChecked(false)
        .build();
  }

  @Override
  public int compareTo(DisplayMenuEntity o) {
    return ordering - o.ordering;
  }

  public void update(String categoryId, String menuName, Price unitPrice) {
    this.categoryId = categoryId;
    this.menuName = menuName;
    this.unitPrice = unitPrice;
  }

  public void update(boolean isChecked, int ordering) {
    this.isChecked = isChecked;
    this.ordering = ordering;
  }

  public void checked() {
    this.isChecked = true;
  }

}
