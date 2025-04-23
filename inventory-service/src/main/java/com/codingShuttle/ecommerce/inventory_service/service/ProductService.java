package com.codingShuttle.ecommerce.inventory_service.service;


import com.codingShuttle.ecommerce.inventory_service.dto.OrderRequestDto;
import com.codingShuttle.ecommerce.inventory_service.dto.OrderRequestItemDto;
import com.codingShuttle.ecommerce.inventory_service.dto.ProductDto;
import com.codingShuttle.ecommerce.inventory_service.entity.Product;
import com.codingShuttle.ecommerce.inventory_service.repo.ProductRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepo productRepo;
    private final ModelMapper modelMapper;

    public List<ProductDto> getAllInventory(){
        log.info("Fetching all inventory items");
        List<Product> inventories = productRepo.findAll();
        return inventories.stream()
                .map(product -> modelMapper.map(product,ProductDto.class))
                .toList();
    }

    public ProductDto getProductById(Long id){
        log.info("Fetching product with id {}",id);
        Optional<Product> inventory = productRepo.findById(id);
        return inventory.map(item->modelMapper.map(item,ProductDto.class))
                .orElseThrow(()->new RuntimeException("inventory not found"));
    }

    @Transactional
    public Double reduceStocks(OrderRequestDto orderRequestDto) {
        log.info("Reducing the stocks");
        Double totalPrice = 0.0;
        for(OrderRequestItemDto orderRequestItemDto: orderRequestDto.getItems()){
            Long productId = orderRequestItemDto.getProductId();
            Integer quantity = orderRequestItemDto.getQuantity();

            Product product = productRepo.findById(productId).orElseThrow(()-> new RuntimeException("Product not found with id: "+productId));

            if(product.getStock() < quantity){
                throw new RuntimeException("Product cannot be fulfilled for given quantity");
            }

            product.setStock(product.getStock() - quantity);
            productRepo.save(product);
            totalPrice += quantity*product.getPrice();

        }
        return totalPrice;
    }
}
