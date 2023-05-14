package co.wadcorp.waiting.data.domain.customer;

import java.util.List;
import java.util.Optional;

public interface ShopCustomerRepository {

  ShopCustomerEntity save(ShopCustomerEntity shopCustomerEntity);

  Optional<ShopCustomerEntity> findById(ShopCustomerId shopCustomerId);

  List<ShopCustomerEntity> findAll();

}
