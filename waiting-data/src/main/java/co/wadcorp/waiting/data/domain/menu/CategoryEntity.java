package co.wadcorp.waiting.data.domain.menu;

import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.data.support.BooleanYnConverter;
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
@Table(name = "cw_category",
    indexes = {
        @Index(name = "cw_category_category_id_index", columnList = "category_id"),
        @Index(name = "cw_category_shop_id_index", columnList = "shop_id")
    }
)
@Entity
public class CategoryEntity extends BaseEntity implements Comparable<CategoryEntity> {

  public static final CategoryEntity EMPTY = new CategoryEntity("", "", "", 1);

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "category_id")
  private String categoryId;

  @Column(name = "shop_id")
  private String shopId;

  @Column(name = "name")
  private String name;

  @Column(name = "ordering")
  private int ordering;

  @Column(name = "deleted_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private Boolean isDeleted;

  @Builder
  private CategoryEntity(String categoryId, String shopId, String name, int ordering) {
    this.categoryId = categoryId;
    this.shopId = shopId;
    this.name = name;
    this.ordering = ordering;
    this.isDeleted = false;
  }

  @Override
  public int compareTo(CategoryEntity o) {
    return ordering - o.ordering;
  }

  public void delete() {
    this.isDeleted = true;
  }

  public void update(String name, int ordering) {
    this.name = name;
    this.ordering = ordering;
  }

}
