package co.wadcorp.waiting.data.domain.waiting;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PersonOption {

  private static final String DELIMITER = " / ";

  private String name;
  private Integer count;

  private List<AdditionalOption> additionalOptions;

  public String createAdditionalOptionText() {
    if (additionalOptions == null || additionalOptions.isEmpty()) {
      return "";
    }

    return String.format(" - %s", additionalOptions.stream()
        .map(item -> String.format("%s %s", item.getName(), item.getCount()))
        .collect(Collectors.joining(DELIMITER)));
  }


  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @EqualsAndHashCode
  public static class AdditionalOption {

    private String name;
    private Integer count;
  }
}
