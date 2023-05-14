package co.wadcorp.waiting.api.internal.service.waiting.dto.request;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class RemoteWaitingRegisterServiceRequest {

  private String tableId;
  private int totalPersonCount;
  private List<PersonOptionVO> personOptions;
  private String phoneNumber;
  private Object extra;
  private RemoteOrderDto order;

  @Builder
  private RemoteWaitingRegisterServiceRequest(String tableId, int totalPersonCount,
      List<PersonOptionVO> personOptions, String phoneNumber, Object extra,
      RemoteOrderDto order) {
    this.tableId = tableId;
    this.totalPersonCount = totalPersonCount;
    this.personOptions = personOptions;
    this.phoneNumber = phoneNumber;
    this.extra = extra;
    this.order = order;
  }

  @Getter
  @NoArgsConstructor
  public static class PersonOptionVO {

    private String id;
    private int count;
    private List<AdditionalOptionVO> additionalOptions;

    @Builder
    private PersonOptionVO(String id, int count, List<AdditionalOptionVO> additionalOptions) {
      this.id = id;
      this.count = count;
      this.additionalOptions = additionalOptions;
    }

  }

  @Getter
  @NoArgsConstructor
  public static class AdditionalOptionVO {

    private String id;
    private int count;

    @Builder
    private AdditionalOptionVO(String id, int count) {
      this.id = id;
      this.count = count;
    }

  }

}
