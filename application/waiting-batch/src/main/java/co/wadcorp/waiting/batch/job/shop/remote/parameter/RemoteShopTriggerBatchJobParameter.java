package co.wadcorp.waiting.batch.job.shop.remote.parameter;

import co.wadcorp.waiting.batch.job.shop.remote.RemoteShopTriggerBatchConfiguration;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Getter
@NoArgsConstructor
@ConditionalOnBean(value = RemoteShopTriggerBatchConfiguration.class)
@Component
@JobScope
public class RemoteShopTriggerBatchJobParameter {

  private List<String> shopSeq;

  @Value("#{jobParameters[shopSeq]}")
  public void setShopSeq(String shopSeq) {

    if (!StringUtils.hasText(shopSeq)) {
      this.shopSeq = List.of();
      return;
    }

    this.shopSeq = List.of(shopSeq.split(":"));
  }

}
