package com.bhupesh.orderservice.service;

import com.bhupesh.orderservice.client.InventoryClient;
import com.bhupesh.orderservice.dto.OrderRequest;
import com.bhupesh.orderservice.event.OrderPlacedEvent;
import com.bhupesh.orderservice.exception.OrderNotInStockException;
import com.bhupesh.orderservice.model.Order;
import com.bhupesh.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void placeOrder(OrderRequest orderRequest) {
        if (inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity())) {
            Order order = Order.builder()
                    .orderNumber(UUID.randomUUID().toString())
                    .price(orderRequest.price())
                    .skuCode(orderRequest.skuCode())
                    .quantity(orderRequest.quantity())
                    .build();
            orderRepository.save(order);

            OrderPlacedEvent orderPlacedEvent = OrderPlacedEvent.newBuilder()
                    .setOrderNumber(order.getOrderNumber())
                    .setEmail(orderRequest.userDetails().email())
                    .setFirstName(orderRequest.userDetails().firstName())
                    .setLastName(orderRequest.userDetails().lastName())
                    .build();
            log.info("Starting to send OrderPlacedEvent: {}", orderPlacedEvent);
            kafkaTemplate.send("order-placed", orderPlacedEvent);
            log.info("Ending OrderPlacedEvent: {}", orderPlacedEvent);
        } else {
            throw new OrderNotInStockException("Product with skuCode " + orderRequest.skuCode() + " is out of stock");
        }
    }
}
