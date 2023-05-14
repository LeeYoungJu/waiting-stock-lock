package co.wadcorp.waiting.api.internal.service.person;

import static co.wadcorp.libs.stream.StreamUtils.convert;

import co.wadcorp.waiting.api.internal.service.person.dto.response.RemotePersonOptionResponse;
import co.wadcorp.waiting.api.internal.service.person.dto.response.RemotePersonOptionResponse.AdditionalOptionVO;
import co.wadcorp.waiting.api.internal.service.person.dto.response.RemotePersonOptionResponse.PersonOptionVO;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData.AdditionalOption;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData.PersonOptionSetting;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsEntity;
import co.wadcorp.waiting.data.query.settings.OptionSettingsQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RemotePersonOptionApiService {

  private final OptionSettingsQueryRepository optionSettingsQueryRepository;

  public List<RemotePersonOptionResponse> findPersonOptions(
      ChannelShopIdMapping channelShopIdMapping
  ) {

    List<OptionSettingsEntity> optionSettingsEntities = optionSettingsQueryRepository.findByShopIds(
        channelShopIdMapping.getAllWaitingShopIds());

    return optionSettingsEntities.stream()
        .map(item ->
            RemotePersonOptionResponse.builder()
                .shopId(Long.valueOf(channelShopIdMapping.getChannelShopId(item.getShopId())))
                .isUsedPersonOptionSetting(item.isUsedPersonOptionSetting())
                .personOptions(
                    convert(item.getPersonOptionSettings(), this::convertPersonOptionVO)
                )
                .build()
        )
        .toList();
  }

  private PersonOptionVO convertPersonOptionVO(PersonOptionSetting item) {
    List<AdditionalOption> additionalOptions = item.getAdditionalOptions();

    return PersonOptionVO.builder()
        .id(item.getId())
        .name(item.getName())
        .isDisplayed(item.getIsDisplayed())
        .isSeat(item.getIsSeat())
        .additionalOptions(
            convert(additionalOptions, this::convertAdditionalOptionVO)
        )
        .build();
  }

  private AdditionalOptionVO convertAdditionalOptionVO(AdditionalOption additionalOption) {
    return AdditionalOptionVO.builder()
        .id(additionalOption.getId())
        .name(additionalOption.getName())
        .isDisplayed(additionalOption.getIsDisplayed())
        .build();
  }
}