package co.wadcorp.waiting.data.domain.menu;

import co.wadcorp.waiting.data.support.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cw_category_menu",
    indexes = {
        @Index(name = "cw_category_menu_category_id_index", columnList = "category_id"),
        @Index(name = "cw_category_menu_menu_id_index", columnList = "menu_id")
    }
)
@Entity
public class CategoryMenuEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "category_id")
  private String categoryId;

  @Column(name = "menu_id")
  private String menuId;

  @Builder
  private CategoryMenuEntity(String categoryId, String menuId) {
    this.categoryId = categoryId;
    this.menuId = menuId;
  }

}
