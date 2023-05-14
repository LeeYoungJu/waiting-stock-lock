package co.wadcorp.waiting.batch.job.shop.sync.store;


import co.wadcorp.waiting.batch.job.shop.sync.SyncPosShopBatchConfiguration;
import co.wadcorp.waiting.infra.pos.dto.PosSearchShopsResponse.SearchShopInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@JobScope
@ConditionalOnBean(value = SyncPosShopBatchConfiguration.class)
@Component
public class SyncPosShopStore {

  private final Map<String, SearchShopInfo> shopInfoMap = new HashMap<>();
  private final List<String> targetCreateSettingShopId = new ArrayList<>();

  public void addAll(List<? extends SearchShopInfo> shopInfoList) {
    shopInfoList
        .forEach(item -> shopInfoMap.put(item.getShopId(), item));
  }

  public void addTargetCreateSettingShopId(String shopIds)  {
    this.targetCreateSettingShopId.add(shopIds);
  }

  public Map<String, SearchShopInfo> getShopInfoMap() {
    return shopInfoMap;
  }

  public List<String> getTargetCreateSettingShopId() {
    return targetCreateSettingShopId;
  }
}
