package co.wadcorp.waiting.api.model.shop.response;

import co.wadcorp.waiting.api.model.shop.vo.ShopInfoVO;
import co.wadcorp.waiting.infra.pos.dto.ShopInfo;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.beans.BeanUtils;

@Getter
public class ShopsResponse {

  private final List<ShopInfoVO> shopList;

  public ShopsResponse(List<ShopInfoVO> shopList) {
    this.shopList = shopList;
  }

  public static ShopsResponse toDto(List<ShopInfo> shopList) {
    List<ShopInfoVO> target = new ArrayList<>();

    for (ShopInfo shopInfo : shopList) {
      ShopInfoVO shopInfoVO = ShopInfoVO.toDto(shopInfo);
      target.add(shopInfoVO);
    }
    return new ShopsResponse(target);
  }
}
