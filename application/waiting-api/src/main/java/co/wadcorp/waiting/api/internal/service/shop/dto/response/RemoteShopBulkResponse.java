package co.wadcorp.waiting.api.internal.service.shop.dto.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RemoteShopBulkResponse {

  private final List<ShopSeqPair> shopIdPairs = new ArrayList<>();

  @Builder
  private RemoteShopBulkResponse(List<ShopSeqPair> shopIdPairs) {
    if (shopIdPairs != null) {
      this.shopIdPairs.addAll(shopIdPairs);
    }
  }

  @Getter
  public static class ShopSeqPair {

    private Long seq;
    private Long shopId;
    private String waitingShopId;

    @Builder
    private ShopSeqPair(Long seq, Long shopId, String waitingShopId) {
      this.seq = seq;
      this.shopId = shopId;
      this.waitingShopId = waitingShopId;
    }

  }

}
