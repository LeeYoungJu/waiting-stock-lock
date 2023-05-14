package co.wadcorp.waiting.data.service.settings;

import co.wadcorp.waiting.data.domain.settings.putoff.DisablePutOffEntity;
import co.wadcorp.waiting.data.domain.settings.putoff.DisablePutOffRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DisablePutOffSettingsService {

  private final DisablePutOffRepository disablePutOffRepository;

  public List<DisablePutOffEntity> getDisablePutOff(String shopId) {
    return disablePutOffRepository.findAllByShopIdAndIsPublished(shopId, true);
  }

  public DisablePutOffEntity save(DisablePutOffEntity disablePutOffEntity) {
    return disablePutOffRepository.save(disablePutOffEntity);
  }

}
