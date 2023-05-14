package co.wadcorp.waiting.data.infra.customer;

import co.wadcorp.waiting.data.domain.customer.ShopCustomerEntity;
import co.wadcorp.waiting.data.domain.customer.ShopCustomerId;
import co.wadcorp.waiting.data.domain.customer.ShopCustomerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaShopCustomerRepository extends ShopCustomerRepository, JpaRepository<ShopCustomerEntity, ShopCustomerId> {

}
