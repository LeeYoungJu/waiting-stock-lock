package co.wadcorp.waiting.data.domain.displaymenu;

import co.wadcorp.waiting.data.domain.menu.CategoryEntity;
import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.data.support.BooleanYnConverter;
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
@Table(name = "cw_display_category",
    indexes = {
        @Index(name = "cw_display_category_category_id_index", columnList = "category_id"),
        @Index(name = "cw_display_category_shop_id_index", columnList = "shop_id")
    }
)
@Entity
public class DisplayCategoryEntity extends BaseEntity implements Comparable<DisplayCategoryEntity> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "category_id")
  private String categoryId;

  @Column(name = "shop_id")
  private String shopId;

  @Column(name = "display_mapping_type")
  @Enumerated(EnumType.STRING)
  private DisplayMappingType displayMappingType;

  @Column(name = "ordering")
  private int ordering;

  @Column(name = "all_checked_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private boolean isAllChecked;

  @Builder
  private DisplayCategoryEntity(String categoryId, String shopId,
      DisplayMappingType displayMappingType, int ordering, boolean isAllChecked) {
    this.categoryId = categoryId;
    this.shopId = shopId;
    this.displayMappingType = displayMappingType;
    this.ordering = ordering;
    this.isAllChecked = isAllChecked;
  }

  public static DisplayCategoryEntity of(CategoryEntity category,
      DisplayMappingType displayMappingType, int ordering) {
    return DisplayCategoryEntity.builder()
        .categoryId(category.getCategoryId())
        .shopId(category.getShopId())
        .displayMappingType(displayMappingType)
        .ordering(ordering)
        .isAllChecked(false)
        .build();
  }

  @Override
  public int compareTo(DisplayCategoryEntity o) {
    return ordering - o.ordering;
  }

  public void allChecked() {
    this.isAllChecked = true;
  }

  public void releaseAllChecked() {
    this.isAllChecked = false;
  }

  public void update(int ordering) {
    this.ordering = ordering;
  }

}
