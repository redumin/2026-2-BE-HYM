package com.gdghongik.commerce.infrastructure.persistence;

import com.gdghongik.commerce.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    @Query("select distinct o from Order o left join fetch o.orderItems")
    List<Order> findAllWithItems();

}
