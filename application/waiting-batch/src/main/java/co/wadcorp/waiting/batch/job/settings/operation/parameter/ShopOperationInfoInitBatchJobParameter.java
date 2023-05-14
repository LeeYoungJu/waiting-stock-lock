package co.wadcorp.waiting.batch.job.settings.operation.parameter;

import co.wadcorp.waiting.batch.job.settings.operation.ShopOperationInfoInitBatchConfiguration;
import co.wadcorp.waiting.shared.util.LocalDateUtils;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Getter
@NoArgsConstructor
@ConditionalOnBean(value = ShopOperationInfoInitBatchConfiguration.class)
@Component
@JobScope
public class ShopOperationInfoInitBatchJobParameter {

  private LocalDate operationStartDate;
  private LocalDate operationEndDate;

  @Value("#{jobParameters[operationStartDate]}")
  public void setOperationStartDate(String operationStartDate) { // yyyy-MM-dd
    this.operationStartDate = LocalDateUtils.parseToLocalDate(operationStartDate);
  }

  @Value("#{jobParameters[operationEndDate]}")
  public void setOperationEndDate(String operationEndDate) { // yyyy-MM-dd
    this.operationEndDate = LocalDateUtils.parseToLocalDate(operationEndDate);
  }

  public List<LocalDate> getOperationDateRange() {
    return LocalDateUtils.getRangeBy(operationStartDate, operationEndDate);
  }

}
