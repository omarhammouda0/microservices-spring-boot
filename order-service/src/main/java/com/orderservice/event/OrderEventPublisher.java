package com.orderservice.event;

import com.orderservice.entity.OrderItem;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Component
public class OrderEventPublisher {

    private RabbitTemplate rabbitTemplate;

    public void publishOrderCreated(OrderItem item , Long orderId) {

        var orderEvent = new OrderCreatedEvent (

                UUID.randomUUID ( ) ,
                orderId ,
                item.getProductId ( ) ,
                item.getQuantity ( ) ,
                Instant.now ( ) ,
                "order-service"

        );

        rabbitTemplate.convertAndSend ( "order.exchange", "order.created", orderEvent );

    }

    public void publishOrderCancelled(Long orderId , List<OrderItem> items ) {

        var orderEvent = new OrderCancelledEvent(
                UUID.randomUUID(),
                orderId,
                items.stream()
                        .map(item -> new OrderCancelledEvent.OrderCancelledItem(
                                item.getProductId(),
                                item.getQuantity()
                        ))
                        .toList()
        );

        rabbitTemplate.convertAndSend ( "order.exchange", "order.cancelled", orderEvent );

    }


}
