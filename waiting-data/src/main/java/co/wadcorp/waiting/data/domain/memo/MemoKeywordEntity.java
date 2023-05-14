package co.wadcorp.waiting.data.domain.memo;

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
@Table(name = "cw_memo_keyword",
    indexes = {
        @Index(name = "cw_memo_keyword_shop_id_index", columnList = "shop_id"),
        @Index(name = "cw_memo_keyword_keyword_id_index", columnList = "keyword_id")
    })
@Entity
public class MemoKeywordEntity extends BaseEntity implements Comparable<MemoKeywordEntity> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "keyword_id")
  private String keywordId;

  @Column(name = "shop_id")
  private String shopId;

  @Column(name = "keyword")
  private String keyword;

  @Column(name = "ordering")
  private int ordering;

  @Column(name = "deleted_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private Boolean isDeleted;

  @Override
  public int compareTo(MemoKeywordEntity entity) {
    return ordering - entity.ordering;
  }

  @Builder
  public MemoKeywordEntity(String keywordId, String shopId, String keyword, int ordering) {
    this.keywordId = keywordId;
    this.shopId = shopId;
    this.keyword = keyword;
    this.ordering = ordering;
    this.isDeleted = false;
  }

  public void updateKeyword(String keyword) {
    this.keyword = keyword;
  }

  public void updateOrdering(int ordering) {
    this.ordering = ordering;
  }

  public void delete() {
    this.isDeleted = true;
  }

}
