package co.wadcorp.waiting.batch.job.settings.operation.store;

import co.wadcorp.waiting.batch.job.settings.operation.ShopOperationInfoInitBatchConfiguration;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.remote.RemoteOperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.remote.ShopRemoteOperationTimeSettings;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Getter
@NoArgsConstructor
@ConditionalOnBean(value = ShopOperationInfoInitBatchConfiguration.class)
@Component
@JobScope
public class ShopOperationInfoInitBatchStore {

  private final Map<String, ShopRemoteOperationTimeSettings> remoteOperationTimeSettingsMap = new HashMap<>();
  private final Map<String, List<LocalDate>> operationDatesByTargetShopId = new HashMap<>();
  private final Set<String> processedShopIds = new HashSet<>();

  public void putRemoteOperationTimeSettings(List<RemoteOperationTimeSettingsEntity> settingsList) {
    for (RemoteOperationTimeSettingsEntity settings : settingsList) {
      remoteOperationTimeSettingsMap.merge(
          settings.getShopId(),
          ShopRemoteOperationTimeSettings.of(settings),
          (s1, s2) -> s1.put(settings)
      );
    }
  }

  public ShopRemoteOperationTimeSettings getRemoteOperationTimeSettings(String shopId) {
    return remoteOperationTimeSettingsMap.getOrDefault(shopId,
        ShopRemoteOperationTimeSettings.EMPTY);
  }

  public void putTargetShopIds(List<String> targetShopIds, LocalDate operationDate) {
    for (String targetShopId : targetShopIds) {
      operationDatesByTargetShopId.computeIfAbsent(targetShopId, shopId -> new ArrayList<>())
          .add(operationDate);
    }
  }

  // 시간 설정 저장 시 따닥 이슈가 생겨, 활성화된 시간 설정 데이터가 2개 이상인 경우를 방어한다.
  public boolean hasDuplicateTimeSettings(OperationTimeSettingsEntity operationTimeSettings) {
    String shopId = operationTimeSettings.getShopId();
    if (processedShopIds.contains(shopId)) {
      return true;
    }

    processedShopIds.add(shopId);
    return false;
  }

  public Set<String> getAllTargetShopIds() {
    return operationDatesByTargetShopId.keySet();
  }

  public List<LocalDate> getTargetShopIdsBy(String shopId) {
    return operationDatesByTargetShopId.getOrDefault(shopId, List.of());
  }

}
