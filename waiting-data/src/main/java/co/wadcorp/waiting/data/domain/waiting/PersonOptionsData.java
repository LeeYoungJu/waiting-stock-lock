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
public class PersonOptionsData {

  private static final String DELIMITER = " / ";

  private List<PersonOption> personOptions;


  public String getPersonOptionText() {
    return personOptions.stream()
        .filter(item -> item.getCount() > 0)
        .map(item -> String.format("%s %s%s", item.getName(), item.getCount(), item.createAdditionalOptionText()))
        .collect(Collectors.joining(DELIMITER));
  }
}
