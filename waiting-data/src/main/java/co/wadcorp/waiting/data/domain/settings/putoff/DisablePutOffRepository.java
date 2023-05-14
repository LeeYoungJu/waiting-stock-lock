package co.wadcorp.waiting.data.domain.settings.putoff;

import java.util.List;

public interface DisablePutOffRepository {

  DisablePutOffEntity save(DisablePutOffEntity disablePutOff);

  List<DisablePutOffEntity> findAllByShopIdAndIsPublished(String shopId, boolean isPublished);

  List<DisablePutOffEntity> findAll();

}
