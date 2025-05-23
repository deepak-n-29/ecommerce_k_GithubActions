package com.codingShuttle.ecommerce.order_service.service;

import com.codingShuttle.ecommerce.order_service.clients.InventoryOpenFeignClient;
import com.codingShuttle.ecommerce.order_service.dto.OrderRequestDto;
import com.codingShuttle.ecommerce.order_service.entity.OrderItem;
import com.codingShuttle.ecommerce.order_service.entity.OrderStatus;
import com.codingShuttle.ecommerce.order_service.entity.Orders;
import com.codingShuttle.ecommerce.order_service.repo.OrdersRepo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersService{

    private final OrdersRepo ordersRepo;
    private final ModelMapper modelMapper;
    private final InventoryOpenFeignClient inventoryOpenFeignClient;

    public List<OrderRequestDto> getAllOrders(){
        log.info("Fetching all orders");
        List<Orders> orders = ordersRepo.findAll();
        return orders.stream().map(order -> modelMapper.map(order,OrderRequestDto.class)).toList();
    }

    public OrderRequestDto getOrderById(Long id){
        log.info("Fetching order with ID: {}", id);
        Orders order = ordersRepo.findById(id).orElseThrow(()-> new RuntimeException(("Order not found")));
        return modelMapper.map(order,OrderRequestDto.class);
    }

//    @Retry(name = "inventoryRetry",fallbackMethod = "createOrderFallback")
    @CircuitBreaker(name = "inventoryCircuitBreaker", fallbackMethod = "createOrderFallback")
//    @RateLimiter(name = "inventoryRateLimiter", fallbackMethod = "createOrderFallback")
    public OrderRequestDto createOrder(OrderRequestDto orderRequestDto) {
        log.info("Creating the createOrder method");
        Double totalPrice = inventoryOpenFeignClient.reduceStocks(orderRequestDto);

        Orders orders = modelMapper.map(orderRequestDto,Orders.class);
        for(OrderItem orderItem: orders.getItems()){
            orderItem.setOrder(orders);
        }
        orders.setTotalPrice(totalPrice);
        orders.setOrderStatus(OrderStatus.CONFIRMED);

        Orders savedOrder = ordersRepo.save(orders);

        return modelMapper.map(savedOrder,OrderRequestDto.class);
    }
    public OrderRequestDto createOrderFallback(OrderRequestDto orderRequestDto, Throwable throwable) {
        log.error("Fallback occurred due to : {}", throwable.getMessage());

        return new OrderRequestDto();
    }
}
