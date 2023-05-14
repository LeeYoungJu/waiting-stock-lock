package co.wadcorp.waiting.batch.job.settings.stock.parameter;

import co.wadcorp.waiting.batch.job.settings.stock.DailyStockInitBatchConfiguration;
import co.wadcorp.waiting.shared.util.LocalDateUtils;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Getter
@NoArgsConstructor
@ConditionalOnBean(value = DailyStockInitBatchConfiguration.class)
@Component
@JobScope
public class DailyStockInitBatchJobParameter {

  private LocalDate operationDate;

  @Value("#{jobParameters[operationDate]}")
  public void setOperationDate(String operationDate) { // yyyy-MM-dd
    this.operationDate = LocalDateUtils.parseToLocalDate(operationDate);
  }

}
