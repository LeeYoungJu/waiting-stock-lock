package co.wadcorp.waiting.data.infra.waiting.putoff;

import co.wadcorp.waiting.data.domain.settings.putoff.DisablePutOffEntity;
import co.wadcorp.waiting.data.domain.settings.putoff.DisablePutOffRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDisablePutOffRepository extends DisablePutOffRepository,
    JpaRepository<DisablePutOffEntity, Long> {

  List<DisablePutOffEntity> findAllByShopIdAndIsPublished(String shopId, boolean isPublished);

}
