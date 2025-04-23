package com.codingShuttle.ecommerce.order_service.controller;

import com.codingShuttle.ecommerce.order_service.config.FeaturesEnableConfig;
import com.codingShuttle.ecommerce.order_service.dto.OrderRequestDto;
import com.codingShuttle.ecommerce.order_service.service.OrdersService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core")
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class OrdersController {

    private final OrdersService ordersService;

    private final FeaturesEnableConfig featuresEnableConfig;

    @Value("${my.variable}")
    private String myVariable;

    @GetMapping("/helloOrders")
    public String helloOrders() {
        if(featuresEnableConfig.isUserTrackingEnabled()){
            return ("user tracking enabled wohoo, my variable is: "+myVariable);
        }else {
            return ("user tracking disabled awww, my variable is: "+myVariable);
        }

    }


    @PostMapping("/create-order")
    public ResponseEntity<OrderRequestDto> createOrder(@RequestBody OrderRequestDto orderRequestDto){
        OrderRequestDto orderRequestDto1 = ordersService.createOrder(orderRequestDto);
        return ResponseEntity.ok(orderRequestDto1);
    }

    @GetMapping
    public ResponseEntity<List<OrderRequestDto>> getAllOrders(HttpServletRequest httpServletRequest){
        log.info("Fetching all orders via controller");
        List<OrderRequestDto> orders = ordersService.getAllOrders();
        return ResponseEntity.ok(orders); // Returns 200 OK with the list of orders
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderRequestDto> getOrderById(@PathVariable Long id){
        log.info("Fetching order with ID: {} via controller", id);
        OrderRequestDto order = ordersService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

}
