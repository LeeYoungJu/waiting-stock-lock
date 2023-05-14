package co.wadcorp.waiting.data.domain.settings;

import co.wadcorp.waiting.data.exception.AppException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@EqualsAndHashCode
public class PrecautionSettingsData {

  private String messagePrecaution;
  private Boolean isUsedPrecautions;
  private List<Precaution> precautions;

  public PrecautionSettingsData() {
  }

  @Builder
  public PrecautionSettingsData(String messagePrecaution, Boolean isUsedPrecautions,
      List<Precaution> precautions) {
    if (messagePrecaution.length() > 500) {
      throw new AppException(HttpStatus.BAD_REQUEST, "알림톡 내 유의사항은 500자 이상 입력할 수 없습니다.");
    }

    this.messagePrecaution = messagePrecaution;
    this.isUsedPrecautions = isUsedPrecautions;
    this.precautions = precautions;
  }

  public String getPrecautionsText() {
    return precautions.stream()
        .map(item -> String.format("- %s", item.getContent()))
        .collect(Collectors.joining("\n"));

  }
}
