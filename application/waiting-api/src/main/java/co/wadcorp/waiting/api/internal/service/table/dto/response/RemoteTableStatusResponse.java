package co.wadcorp.waiting.api.internal.service.table.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RemoteTableStatusResponse {

  private final Long shopId;
  private final int totalTeamCount;
  private final List<TableCurrentStatusVO> tableCurrentStatus = new ArrayList<>();

  @Builder
  private RemoteTableStatusResponse(Long shopId, int totalTeamCount,
      List<TableCurrentStatusVO> currentStatus) {
    this.shopId = shopId;
    this.totalTeamCount = totalTeamCount;
    if (currentStatus != null) {
      this.tableCurrentStatus.addAll(currentStatus);
    }
  }

  @Getter
  public static class TableCurrentStatusVO {

    private final String tableId;
    private final String tableName;
    private final int teamCount;
    private final Integer expectedWaitingTime;

    @Getter(value = AccessLevel.PRIVATE)
    @JsonProperty("isUsedExpectedWaitingPeriod")
    private final boolean isUsedExpectedWaitingPeriod;

    @Getter(value = AccessLevel.PRIVATE)
    @JsonProperty("isTakeOut")
    private final boolean isTakeOut;

    @Builder
    private TableCurrentStatusVO(String tableId, String tableName, int teamCount,
        Integer expectedWaitingTime, boolean isUsedExpectedWaitingPeriod, boolean isTakeOut) {
      this.tableId = tableId;
      this.tableName = tableName;
      this.teamCount = teamCount;
      this.expectedWaitingTime = expectedWaitingTime;
      this.isUsedExpectedWaitingPeriod = isUsedExpectedWaitingPeriod;
      this.isTakeOut = isTakeOut;
    }

  }

}
