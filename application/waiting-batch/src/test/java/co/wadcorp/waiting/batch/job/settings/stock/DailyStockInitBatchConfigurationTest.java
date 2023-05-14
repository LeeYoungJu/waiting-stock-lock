package co.wadcorp.waiting.batch.job.settings.stock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import co.wadcorp.waiting.batch.job.BatchTestSupport;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.menu.MenuRepository;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.StockHistoryRepository;
import co.wadcorp.waiting.data.domain.stock.StockRepository;
import co.wadcorp.waiting.data.support.Price;
import co.wadcorp.waiting.shared.util.LocalDateTimeUtils;
import co.wadcorp.waiting.shared.util.LocalDateUtils;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "chunkSize=1")
class DailyStockInitBatchConfigurationTest extends BatchTestSupport {

  @Autowired
  @Qualifier(DailyStockInitBatchConfiguration.JOB_NAME)
  private Job job;

  @Autowired
  private StockRepository stockRepository;

  @Autowired
  private MenuRepository menuRepository;

  @Autowired
  private StockHistoryRepository stockHistoryRepository;

  @AfterEach
  void tearDown() {
    menuRepository.deleteAllInBatch();
    stockRepository.deleteAllInBatch();
    stockHistoryRepository.deleteAllInBatch();
  }

  @DisplayName("운영 시간 정보를 기반으로 재고를 생성한다.")
  @Test
  void saveByBatch() throws Exception {
    // given
    String operationDateString = "2023-02-15"; // 수요일
    LocalDate operationDate = LocalDateUtils.parseToLocalDate(operationDateString);
    String shopId = "shopId";
    String menuId = "menuId";
    int dailyStock = 200;

    menuRepository.save(
        MenuEntity.builder()
            .shopId(shopId)
            .menuId(menuId)
            .name("아무개")
            .isUsedDailyStock(true)
            .dailyStock(dailyStock)
            .isUsedMenuQuantityPerTeam(false)
            .unitPrice(Price.ZERO)
            .build()
    );

    JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParametersBuilder()
        .addString("operationDate", operationDateString)
        .toJobParameters();

    // when
    jobLauncherTestUtils.setJob(job);
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    List<StockEntity> entities = stockRepository.findAllByMenuIdInAndOperationDate(
        List.of(menuId), operationDate);
    assertThat(entities).hasSize(1)
        .extracting("menuId", "operationDate", "stock",
            "salesQuantity", "isUsedDailyStock", "isOutOfStock"
        )
        .contains(
            tuple(
                menuId, operationDate, dailyStock,
                0, true, false
            )
        );

    List<StockEntity> historyEntities = stockRepository.findAll();
    assertThat(historyEntities).hasSize(1)
        .extracting("menuId", "operationDate", "stock",
            "salesQuantity", "isUsedDailyStock", "isOutOfStock"
        )
        .contains(
            tuple(
                menuId, operationDate, dailyStock,
                0, true, false
            )
        );
  }

  @DisplayName("기존에 재고가 존재한다면 신규 생성 시도 시 무시한다.")
  @Test
  void saveByBatchIfInfoAlreadyExists() throws Exception {

    // given
    String operationDateString = "2023-02-15"; // 수요일
    LocalDate operationDate = LocalDateUtils.parseToLocalDate(operationDateString);
    String shopId = "shopId";
    String menuId = "menuId";
    int dailyStock = 200;

    MenuEntity menu = menuRepository.save(
        MenuEntity.builder()
            .shopId(shopId)
            .menuId(menuId)
            .name("아무개")
            .isUsedDailyStock(true)
            .dailyStock(dailyStock)
            .isUsedMenuQuantityPerTeam(false)
            .unitPrice(Price.ZERO)
            .build()
    );

    stockRepository.save(
        StockEntity.of(menu, operationDate)
    );

    JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParametersBuilder()
        .addString("operationDate", operationDateString)
        .toJobParameters();

    // when
    jobLauncherTestUtils.setJob(job);
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    List<StockEntity> entities = stockRepository.findAllByMenuIdInAndOperationDate(
        List.of(menuId), operationDate);
    assertThat(entities).hasSize(1)
        .extracting("menuId", "operationDate", "stock",
            "salesQuantity", "isUsedDailyStock", "isOutOfStock"
        )
        .contains(
            tuple(
                menuId, operationDate, dailyStock,
                0, true, false
            )
        );

    List<StockEntity> historyEntities = stockRepository.findAll();
    assertThat(historyEntities).hasSize(1)
        .extracting("menuId", "operationDate", "stock",
            "salesQuantity", "isUsedDailyStock", "isOutOfStock"
        )
        .contains(
            tuple(
                menuId, operationDate, dailyStock,
                0, true, false
            )
        );



  }

}