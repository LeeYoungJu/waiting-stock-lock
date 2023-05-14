package co.wadcorp.waiting.api.model.waiting.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelWaitingRequest {

  private List<String> waitingIdList;
}
