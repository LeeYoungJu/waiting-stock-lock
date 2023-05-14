package co.wadcorp.waiting.batch.job.shop.sync.reader;

import co.wadcorp.waiting.infra.pos.CatchtablePosShopClient;
import co.wadcorp.waiting.infra.pos.dto.PosApiResponse;
import co.wadcorp.waiting.infra.pos.dto.PosSearchShopsResponse;
import co.wadcorp.waiting.infra.pos.dto.PosSearchShopsResponse.SearchShopInfo;
import co.wadcorp.waiting.infra.pos.dto.PosShopSearchRequest;
import co.wadcorp.waiting.infra.pos.dto.PosShopSearchRequest.FetchType;
import co.wadcorp.waiting.infra.pos.dto.PosShopSearchRequest.ShopServiceType;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.AbstractPagingItemReader;

@Slf4j
public class PosShopPagingItemReader extends AbstractPagingItemReader<SearchShopInfo> {

  private final CatchtablePosShopClient catchtablePosShopClient;

  public PosShopPagingItemReader(CatchtablePosShopClient catchtablePosShopClient, int chunk) {
    this.catchtablePosShopClient = catchtablePosShopClient;
    this.setPageSize(chunk);
  }

  @Override
  protected void doReadPage() {
    PosShopSearchRequest request = PosShopSearchRequest.builder()
        .shopServiceTypes(List.of(ShopServiceType.WAITING))
        .page(getPage() + 1)
        .pageSize(getPageSize())
        .isMembership(true)
        .fetchTypes(List.of(FetchType.BASIC, FetchType.BUSINESS_INFO))
        .build();

    PosApiResponse<PosSearchShopsResponse> posApiResponse = catchtablePosShopClient.searchShops(
        request);
    log.info("data = {}", posApiResponse.getData().getData());

    if (posApiResponse.isOk()) {
      results = posApiResponse.getData().getData();
      return;
    }
    results = new ArrayList<>();
  }
}
