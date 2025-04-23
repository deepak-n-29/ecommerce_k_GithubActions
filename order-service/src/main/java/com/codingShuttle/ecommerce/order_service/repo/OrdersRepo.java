package com.codingShuttle.ecommerce.order_service.repo;

import com.codingShuttle.ecommerce.order_service.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepo extends JpaRepository<Orders,Long> {
}
