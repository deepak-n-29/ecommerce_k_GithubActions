package com.codingShuttle.ecommerce.inventory_service.repo;

import com.codingShuttle.ecommerce.inventory_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product,Long> {
}
