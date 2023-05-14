package co.wadcorp.waiting.api.model.waiting.vo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonOptionVO {

  private String id;
  private String name;
  private Integer count;
  private List<AdditionalOption> additionalOptions;

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AdditionalOption {
    private String id;
    private String name;
    private Integer count;

  }
}
