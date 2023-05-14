package co.wadcorp.waiting.batch.job.waiting.expiration.parameter;

import co.wadcorp.waiting.batch.job.waiting.expiration.WaitingExpirationBatchConfiguration;
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
@ConditionalOnBean(value = WaitingExpirationBatchConfiguration.class)
@Component
@JobScope
public class WaitingExpirationBatchJobParameter {

  private LocalDate operationDate;

  @Value("#{jobParameters[operationDate]}")
  public void setOperationDate(String operationDateString) { // yyyy-MM-dd
    this.operationDate = LocalDateUtils.parseToLocalDate(operationDateString);
  }

}
