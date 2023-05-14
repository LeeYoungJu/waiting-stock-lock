package co.wadcorp.waiting.data.service.settings;

import co.wadcorp.waiting.data.domain.settings.DefaultOperationTimeSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.OperationTimeForDaysChangeChecker;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OperationTimeSettingsService {

  private final OperationTimeSettingsRepository operationTimeSettingsRepository;

  public OperationTimeSettingsEntity getOperationTimeSettings(String shopId) {
    return operationTimeSettingsRepository.findFirstByShopIdAndIsPublished(shopId, true)
        .orElseGet(
            () -> createDefaultOperationTimeSettings(shopId)
        );
  }

  public OperationTimeSettingsEntity saveOperationTimeSettings(OperationTimeSettingsEntity entity) {
    operationTimeSettingsRepository.findAllByShopIdInAndIsPublished(List.of(entity.getShopId()), true)
        .forEach(OperationTimeSettingsEntity::unPublish);

    return operationTimeSettingsRepository.save(entity);
  }

  private OperationTimeSettingsEntity createDefaultOperationTimeSettings(String shopId) {
    return OperationTimeSettingsEntity.builder()
        .shopId(shopId)
        .operationTimeSettingsData(DefaultOperationTimeSettingDataFactory.create())
        .build();
  }

  /**
   * <pre>
   * 매장 운영시간 최신 2개의 데이터를 가져와서 비교 후 변경감지를 한다.
   * 비교 시 서로 동일하지 않으면 변경된 것으로 간주하고 true 를 return 한다.
   * </pre>
   * @param shopId
   * @return 변경됐으면 true / 아니면 false
   */
  public OperationTimeForDaysChangeChecker isThereChangeInOperationTime(String shopId) {
    Optional<OperationTimeSettingsEntity> publishedTimeDataOpt =
        operationTimeSettingsRepository.findFirstByShopIdAndIsPublished(shopId, true);
    Optional<OperationTimeSettingsEntity> unpublishedLatestTimeDataOpt
        = operationTimeSettingsRepository.findFirstByShopIdAndUnpublishedOrderBySeqDesc(shopId);

    if(publishedTimeDataOpt.isEmpty() || unpublishedLatestTimeDataOpt.isEmpty()) {
      return DefaultOperationTimeSettingDataFactory
              .createOperationTimeForDaysChangeChecker();
    }

    return publishedTimeDataOpt.get().getOperationTimeSettingsData()
            .checkChangesInTimeForDays(
                unpublishedLatestTimeDataOpt.get().getOperationTimeSettingsData());
  }

}
