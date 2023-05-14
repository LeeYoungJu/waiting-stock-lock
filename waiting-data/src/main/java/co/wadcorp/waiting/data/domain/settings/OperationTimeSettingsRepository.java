package co.wadcorp.waiting.data.domain.settings;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationTimeSettingsRepository {

  Optional<OperationTimeSettingsEntity> findFirstByShopIdAndIsPublished(String shopId, Boolean isPublished);

  @Query("select o from OperationTimeSettingsEntity o "
      + "where o.shopId = :shopId and o.isPublished = false order by o.seq desc limit 1")
  Optional<OperationTimeSettingsEntity> findFirstByShopIdAndUnpublishedOrderBySeqDesc(String shopId);

  List<OperationTimeSettingsEntity> findAllByShopIdInAndIsPublished(List<String> shopIds, Boolean isPublished);

  OperationTimeSettingsEntity save(OperationTimeSettingsEntity operationTimeSettingsEntity);

  void deleteAllInBatch();

  <S extends OperationTimeSettingsEntity> List<S> saveAll(Iterable<S> entities);
}
