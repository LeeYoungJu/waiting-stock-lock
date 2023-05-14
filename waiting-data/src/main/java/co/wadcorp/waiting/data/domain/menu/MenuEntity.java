package co.wadcorp.waiting.data.domain.menu;

import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.data.support.BooleanYnConverter;
import co.wadcorp.waiting.data.support.Price;
import co.wadcorp.waiting.data.support.PriceConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
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
@Table(name = "cw_menu",
    indexes = {
        @Index(name = "cw_menu_menu_id_index", columnList = "menu_id"),
        @Index(name = "cw_menu_shop_id_index", columnList = "shop_id")
    }
)
@Entity
public class MenuEntity extends BaseEntity implements Comparable<MenuEntity> {

  public static final MenuEntity EMPTY = new MenuEntity("", "", "", 1, Price.of(0), false, null,
      false, null);

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "menu_id")
  private String menuId;

  @Column(name = "shop_id")
  private String shopId;

  @Column(name = "name")
  private String name;

  @Column(name = "ordering")
  private int ordering;

  @Column(name = "unit_price")
  @Convert(converter = PriceConverter.class)
  private Price unitPrice; // 메뉴 단위 가격

  @Column(name = "used_daily_stock_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private boolean isUsedDailyStock; // 일별 재고 사용 여부

  @Column(name = "daily_stock")
  private Integer dailyStock; // 일별 재고

  @Column(name = "used_menu_quantity_per_team_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private boolean isUsedMenuQuantityPerTeam; // 팀 당 주문 가능 수량 사용 여부

  @Column(name = "menu_quantity_per_team")
  private Integer menuQuantityPerTeam; // 팀 당 주문 가능 수량

  @Column(name = "deleted_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private Boolean isDeleted;

  @Builder
  private MenuEntity(String menuId, String shopId, String name, int ordering, Price unitPrice,
      boolean isUsedDailyStock, Integer dailyStock, boolean isUsedMenuQuantityPerTeam,
      Integer menuQuantityPerTeam) {
    this.menuId = menuId;
    this.shopId = shopId;
    this.name = name;
    this.ordering = ordering;
    this.unitPrice = unitPrice;
    this.isUsedDailyStock = isUsedDailyStock;
    this.dailyStock = dailyStock;
    this.isUsedMenuQuantityPerTeam = isUsedMenuQuantityPerTeam;
    this.menuQuantityPerTeam = menuQuantityPerTeam;
    this.isDeleted = false;
  }

  @Override
  public int compareTo(MenuEntity o) {
    return ordering - o.ordering;
  }

  public void delete() {
    this.isDeleted = true;
  }

  public void update(String name, Price unitPrice, boolean isUsedDailyStock, Integer dailyStock,
      boolean isUsedMenuQuantityPerTeam, Integer menuQuantityPerTeam) {
    this.name = name;
    this.unitPrice = unitPrice;
    this.isUsedDailyStock = isUsedDailyStock;
    if (isUsedDailyStock) {
      this.dailyStock = dailyStock;
    }
    this.isUsedMenuQuantityPerTeam = isUsedMenuQuantityPerTeam;
    if (isUsedMenuQuantityPerTeam) {
      this.menuQuantityPerTeam = menuQuantityPerTeam;
    }
  }

  public void updateOrdering(int ordering) {
    this.ordering = ordering;
  }

  public boolean isNotUsedDailyStock() {
    return !this.isUsedDailyStock;
  }

  public boolean isValidMenuQuantityPerTeam(int quantity) {
    if (!isUsedMenuQuantityPerTeam) {
      return true;
    }
    return quantity <= this.menuQuantityPerTeam;
  }

  public boolean isInvalidMenuQuantityPerTeam(int quantity) {
    return !isValidMenuQuantityPerTeam(quantity);
  }

}
