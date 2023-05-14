package co.wadcorp.waiting.api.model.shop.vo;

import co.wadcorp.waiting.infra.pos.dto.ShopInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ShopInfoVO {
    private String shopId;
    private String shopName;
    private Boolean isRemoveProcessing;
    private Boolean isCatchWaiting;

    @Builder
    public ShopInfoVO(String shopId, String shopName, Boolean isRemoveProcessing,
        Boolean isCatchWaiting) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.isRemoveProcessing = isRemoveProcessing;
        this.isCatchWaiting = isCatchWaiting;
    }

    public static ShopInfoVO toDto(ShopInfo shopInfo) {
        return ShopInfoVO.builder()
            .shopId(shopInfo.getShopId())
            .shopName(shopInfo.getShopName())
            .isRemoveProcessing(shopInfo.getIsRemoveProcessing())
            .isCatchWaiting(true)
            .build();
    }
}
