package co.wadcorp.waiting.data.service.waiting;

import static co.wadcorp.libs.stream.StreamUtils.convert;
import static co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsEntity.DEFAULT_OPERATION_END_TIME;
import static co.wadcorp.waiting.shared.util.ZonedDateTimeUtils.convertToTimestamp;

import co.wadcorp.waiting.data.domain.settings.DefaultOperationTimeSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.AutoPauseSettings.PauseReason;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsRepository;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoHistoryEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoHistoryRepository;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoRepository;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.shared.util.LocalDateTimeUtils;
import co.wadcorp.waiting.shared.util.LocalDateUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class ShopOperationInfoService {

  private final ShopOperationInfoRepository shopOperationInfoRepository;
  private final ShopOperationInfoHistoryRepository shopOperationInfoHistoryRepository;

  private final OperationTimeSettingsRepository operationTimeSettingsRepository;

  private final JdbcTemplate jdbcTemplate;

  public ShopOperationInfoEntity getByShopIdAndOperationDate(String shopId,
      LocalDate operationDate) {
    return shopOperationInfoRepository.findByShopIdAndOperationDate(shopId, operationDate)
        .orElse(ShopOperationInfoEntity.EMPTY_OPERATION_INFO);
  }

  public List<ShopOperationInfoEntity> findByShopIdAndOperationDateAfterOrEqual(String shopId,
      LocalDate operationDate) {
    return shopOperationInfoRepository.findByShopIdAndOperationDateAfterOrEqual(shopId,
        operationDate);
  }

  public ShopOperationInfoEntity findByShopIdAndOperationDate(String shopId,
      LocalDate operationDate) {
    return shopOperationInfoRepository.findByShopIdAndOperationDate(shopId, operationDate)
        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_OPERATION));
  }

  public ShopOperationInfoEntity save(ShopOperationInfoEntity shopOperationInfoEntity) {
    shopOperationInfoRepository.findByShopIdAndOperationDate(shopOperationInfoEntity.getShopId(),
            shopOperationInfoEntity.getOperationDate())
        .ifPresent(item -> {
          throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.ALREADY_CREATED_OPERATION);
        });

    ShopOperationInfoEntity savedEntity = shopOperationInfoRepository.save(shopOperationInfoEntity);
    saveHistory(shopOperationInfoEntity);

    return savedEntity;
  }

  // 동일한 운영일에 대해서만
  public void saveByBatch(List<ShopOperationInfoEntity> entities, LocalDate operationDate) {
    jdbcTemplate.batchUpdate(
        ShopOperationInfoBatchInsertSupport.getSqlOfOperationInfo(),
        ShopOperationInfoBatchInsertSupport.getPreparedStatementSetterOfOperationInfo(entities)
    );

    List<String> savedShopIds = convert(entities, ShopOperationInfoEntity::getShopId);
    List<ShopOperationInfoEntity> savedEntities = shopOperationInfoRepository.findAllByShopIdInAndOperationDate(
        savedShopIds, operationDate
    );

    jdbcTemplate.batchUpdate(
        ShopOperationInfoBatchInsertSupport.getSqlOfOperationInfoHistory(),
        ShopOperationInfoBatchInsertSupport.getPreparedStatementSetterOfOperationInfoHistory(
            savedEntities
        )
    );
  }

  public ShopOperationInfoEntity open(String shopId, LocalDate operationDate,
      ZonedDateTime nowLocalDateTime) {
    ShopOperationInfoEntity shopOperationInfoEntity = findByShopIdAndOperationDate(shopId,
        operationDate);

    if (shopOperationInfoEntity.isBeforeOperationDateTime(nowLocalDateTime)) {
      shopOperationInfoEntity.updateOperationStartDateTime(nowLocalDateTime);
    }

    if (shopOperationInfoEntity.isAfterOperationEndDateTime(nowLocalDateTime)) {
      LocalDate localDate = operationDate.plusDays(1);
      ZonedDateTime operationEndDateTime = ZonedDateTimeUtils.ofSeoul(localDate,
          DEFAULT_OPERATION_END_TIME);
      shopOperationInfoEntity.updateOperationEndDateTime(operationEndDateTime);
    }

    if (shopOperationInfoEntity.isBetweenAutoPauseRange(nowLocalDateTime)) {
      shopOperationInfoEntity.clearAutoPauseInfo();
    }

    shopOperationInfoEntity.open();
    saveHistory(shopOperationInfoEntity);

    return shopOperationInfoEntity;
  }

  public ShopOperationInfoEntity close(String shopId, LocalDate operationDate) {
    ShopOperationInfoEntity shopOperationInfoEntity = findByShopIdAndOperationDate(shopId,
        operationDate);

    shopOperationInfoEntity.close();
    saveHistory(shopOperationInfoEntity);

    return shopOperationInfoEntity;
  }

  public ShopOperationInfoEntity byPass(String shopId, LocalDate operationDate) {
    ShopOperationInfoEntity shopOperationInfoEntity = findByShopIdAndOperationDate(shopId,
        operationDate);

    shopOperationInfoEntity.byPass();
    saveHistory(shopOperationInfoEntity);

    return shopOperationInfoEntity;
  }

  public ShopOperationInfoEntity pause(String shopId, LocalDate operationDate, String pauseReasonId,
      Integer pausePeriod) {
    if (pausePeriod != -1 && (pausePeriod < 10 || pausePeriod > 180)) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.OUT_OF_RANGE_PAUSE_PERIOD);
    }

    ShopOperationInfoEntity shopOperationInfoEntity = findByShopIdAndOperationDate(shopId,
        operationDate);

    OperationTimeSettingsEntity operationTimeSettingsEntity =
        operationTimeSettingsRepository.findFirstByShopIdAndIsPublished(shopId, true)
            .orElseGet(() -> new OperationTimeSettingsEntity(shopId,
                DefaultOperationTimeSettingDataFactory.create()));

    PauseReason reason = operationTimeSettingsEntity.findReason(pauseReasonId);
    shopOperationInfoEntity.pause(reason.getId(), reason.getReason(), pausePeriod);
    saveHistory(shopOperationInfoEntity);

    return shopOperationInfoEntity;
  }

  private void saveHistory(ShopOperationInfoEntity shopOperationInfoEntity) {
    shopOperationInfoHistoryRepository.save(
        ShopOperationInfoHistoryEntity.of(shopOperationInfoEntity));
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  private static class ShopOperationInfoBatchInsertSupport {

    public static String getSqlOfOperationInfo() {
      return """
          INSERT INTO cw_shop_operation_info
           (shop_id, operation_date, registrable_status, operation_start_date_time, operation_end_date_time,
            remote_operation_start_date_time, remote_operation_end_date_time, manual_pause_start_date_time, manual_pause_end_date_time,
             auto_pause_start_date_time, auto_pause_end_date_time, closed_reason, manual_pause_reason_id, manual_pause_reason,
              auto_pause_reason_id, auto_pause_reason, remote_auto_pause_start_date_time, remote_auto_pause_end_date_time,
               reg_date_time, mod_date_time)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
          """;
    }

    public static String getSqlOfOperationInfoHistory() {
      return """
          INSERT INTO cw_shop_operation_info_history
           (shop_id, operation_date, registrable_status, operation_start_date_time, operation_end_date_time,
            remote_operation_start_date_time, remote_operation_end_date_time, manual_pause_start_date_time, manual_pause_end_date_time,
             auto_pause_start_date_time, auto_pause_end_date_time, closed_reason, manual_pause_reason_id, manual_pause_reason,
              auto_pause_reason_id, auto_pause_reason, remote_auto_pause_start_date_time, remote_auto_pause_end_date_time, 
               reg_date_time, shop_operation_info_seq)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
          """;
    }

    public static BatchPreparedStatementSetter getPreparedStatementSetterOfOperationInfo(
        List<ShopOperationInfoEntity> entities
    ) {
      return new BatchPreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
          ShopOperationInfoEntity entity = entities.get(i);
          ZonedDateTime nowDateTime = ZonedDateTimeUtils.nowOfSeoul();
          setPreparedStatement(ps, entity, nowDateTime);
          ps.setTimestamp(20, LocalDateTimeUtils.convertToTimestamp(nowDateTime));
        }

        @Override
        public int getBatchSize() {
          return entities.size();
        }
      };
    }

    public static BatchPreparedStatementSetter getPreparedStatementSetterOfOperationInfoHistory(
        List<ShopOperationInfoEntity> entities
    ) {
      return new BatchPreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
          ShopOperationInfoEntity entity = entities.get(i);

          setPreparedStatement(ps, entity, ZonedDateTimeUtils.nowOfSeoul());
          ps.setLong(20, entity.getSeq());
        }

        @Override
        public int getBatchSize() {
          return entities.size();
        }
      };
    }

    public static void setPreparedStatement(
        PreparedStatement ps, ShopOperationInfoEntity entity, ZonedDateTime nowDateTime
    ) throws SQLException {
      String closedReason = entity.getClosedReason() == null
          ? null
          : entity.getClosedReason().name();

      ps.setString(1, entity.getShopId());
      ps.setString(2, LocalDateUtils.convertToString(entity.getOperationDate()));
      ps.setString(3, entity.getRegistrableStatus().name());
      ps.setTimestamp(4, convertToTimestamp(entity.getOperationStartDateTime()));
      ps.setTimestamp(5, convertToTimestamp(entity.getOperationEndDateTime()));
      ps.setTimestamp(6, convertToTimestamp(entity.getRemoteOperationStartDateTime()));
      ps.setTimestamp(7, convertToTimestamp(entity.getRemoteOperationEndDateTime()));
      ps.setTimestamp(8, convertToTimestamp(entity.getManualPauseStartDateTime()));
      ps.setTimestamp(9, convertToTimestamp(entity.getManualPauseEndDateTime()));
      ps.setTimestamp(10, convertToTimestamp(entity.getAutoPauseStartDateTime()));
      ps.setTimestamp(11, convertToTimestamp(entity.getAutoPauseEndDateTime()));
      ps.setString(12, closedReason);
      ps.setString(13, entity.getManualPauseReasonId());
      ps.setString(14, entity.getManualPauseReason());
      ps.setString(15, entity.getAutoPauseReasonId());
      ps.setString(16, entity.getAutoPauseReason());
      ps.setTimestamp(17, convertToTimestamp(entity.getRemoteAutoPauseStartDateTime()));
      ps.setTimestamp(18, convertToTimestamp(entity.getRemoteAutoPauseEndDateTime()));
      ps.setTimestamp(19, LocalDateTimeUtils.convertToTimestamp(nowDateTime));
    }
  }

}
