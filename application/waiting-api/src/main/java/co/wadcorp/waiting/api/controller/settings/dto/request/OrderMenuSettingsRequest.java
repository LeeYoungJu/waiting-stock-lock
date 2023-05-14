package co.wadcorp.waiting.api.controller.settings.dto.request;

import co.wadcorp.waiting.api.service.settings.dto.request.OrderMenuSettingsServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderMenuSettingsRequest {

  @NotBlank(message = "메뉴 ID는 필수입니다.")
  private String id;

  @NotBlank(message = "카테고리 ID는 필수입니다.")
  private String categoryId;

  @NotBlank(message = "메뉴 이름은 필수입니다.")
  private String name;

  @NotNull(message = "가격은 필수입니다.")
  private BigDecimal unitPrice;

  @NotNull(message = "일별 재고 사용 여부는 필수입니다.")
  private Boolean isUsedDailyStock;

  private Integer dailyStock;

  @NotNull(message = "한 팀당 주문 가능 여부는 필수입니다.")
  private Boolean isUsedMenuQuantityPerTeam;

  private Integer menuQuantityPerTeam;

  @Builder
  private OrderMenuSettingsRequest(String id, String categoryId, String name, BigDecimal unitPrice,
      Boolean isUsedDailyStock, Integer dailyStock, Boolean isUsedMenuQuantityPerTeam,
      Integer menuQuantityPerTeam) {
    this.id = id;
    this.categoryId = categoryId;
    this.name = name;
    this.unitPrice = unitPrice;
    this.isUsedDailyStock = isUsedDailyStock;
    this.dailyStock = dailyStock;
    this.isUsedMenuQuantityPerTeam = isUsedMenuQuantityPerTeam;
    this.menuQuantityPerTeam = menuQuantityPerTeam;
  }

  public OrderMenuSettingsServiceRequest toServiceRequest() {
    return OrderMenuSettingsServiceRequest.builder()
        .id(this.id)
        .categoryId(this.categoryId)
        .name(this.name)
        .unitPrice(this.unitPrice)
        .isUsedDailyStock(this.isUsedDailyStock)
        .dailyStock(this.dailyStock)
        .isUsedMenuQuantityPerTeam(this.isUsedMenuQuantityPerTeam)
        .menuQuantityPerTeam(this.menuQuantityPerTeam)
        .build();
  }

}
