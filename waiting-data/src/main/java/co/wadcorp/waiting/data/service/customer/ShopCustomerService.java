package co.wadcorp.waiting.data.service.customer;

import co.wadcorp.waiting.data.domain.customer.ShopCustomerEntity;
import co.wadcorp.waiting.data.domain.customer.ShopCustomerId;
import co.wadcorp.waiting.data.domain.customer.ShopCustomerRepository;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.query.waiting.WaitingHistoryQueryRepository;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingHistoryDetailStatusDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShopCustomerService {

  private final ShopCustomerRepository shopCustomerRepository;
  private final WaitingHistoryQueryRepository waitingHistoryQueryRepository;

  public ShopCustomerEntity saveShopCustomer(ShopCustomerEntity shopCustomerEntity) {
    return shopCustomerRepository.save(shopCustomerEntity);
  }

  public ShopCustomerEntity getShopCustomerById(String shopId, Long customerSeq) {
    ShopCustomerId shopCustomerId = createShopCustomerId(shopId, customerSeq);

    return shopCustomerRepository.findById(shopCustomerId)
        .orElseGet(() -> ShopCustomerEntity.ofDefault(shopId, customerSeq));
  }

  public ShopCustomerEntity getShopCustomerById(String shopId, Long customerSeq,
      String customerName) {
    ShopCustomerId shopCustomerId = createShopCustomerId(shopId, customerSeq);

    return shopCustomerRepository.findById(shopCustomerId)
        .orElseGet(() -> ShopCustomerEntity.ofDefault(shopId, customerSeq, customerName));
  }

  public void cancel(String shopId, Long customerSeq) {
    ShopCustomerEntity customer = getShopCustomerById(shopId, customerSeq);
    customer.increaseCancelCount();
  }

  public void noShow(String shopId, Long customerSeq) {
    ShopCustomerEntity customer = getShopCustomerById(shopId, customerSeq);
    customer.increaseNoShowCount();
    customer.increaseCancelCount();
  }

  public void sitting(String shopId, Long customerSeq) {
    ShopCustomerEntity customer = getShopCustomerById(shopId, customerSeq);
    customer.increaseSittingCount();
  }

  public void undo(String shopId, Long customerSeq, String waitingId) {
    ShopCustomerEntity customer = getShopCustomerById(shopId, customerSeq);
    List<WaitingHistoryDetailStatusDto> histories = waitingHistoryQueryRepository.findLastWaitingHistoryDetailStatuses(
        waitingId, 2);

    WaitingHistoryDetailStatusDto detailStatusDto = getLastDetailStatusBeforeUndo(histories);
    if (detailStatusDto.isSitting()) {
      customer.decreaseSittingCount();
    }
    if (detailStatusDto.isCancel()) {
      customer.decreaseCancelCount();
    }
    if (detailStatusDto.isCancelByNoShow()) {
      customer.decreaseNoShowCount();
    }
  }

  private ShopCustomerId createShopCustomerId(String shopId, Long customerSeq) {
    return ShopCustomerId.builder()
        .shopId(shopId)
        .customerSeq(customerSeq)
        .build();
  }

  private WaitingHistoryDetailStatusDto getLastDetailStatusBeforeUndo(
      List<WaitingHistoryDetailStatusDto> histories) {
    return histories.stream()
        .filter(this::isNotUndoOrNotUndoByCustomer)
        .findFirst()
        .orElseThrow(
            () -> new AppException(HttpStatus.BAD_REQUEST, "복귀 이전 변경 이력이 존재하지 않아 복귀가 불가합니다.")
        );
  }

  private boolean isNotUndoOrNotUndoByCustomer(WaitingHistoryDetailStatusDto detailStatusDto) {
    return !detailStatusDto.isUndo() && !detailStatusDto.isUndoByCustomer();
  }

}
