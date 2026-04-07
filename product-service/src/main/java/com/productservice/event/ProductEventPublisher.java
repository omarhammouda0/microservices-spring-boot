package com.productservice.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@AllArgsConstructor
@Component
@Slf4j

public class ProductEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishOrderConfirmed(Long orderId) {

        log.info ( "Order with id {} has been confirmed" , orderId );

        var orderConfirmation = new OrderConfirmedEvent ( UUID.randomUUID ( ) , orderId );

        rabbitTemplate.convertAndSend ( "order.exchange" , "order.confirmed" , orderConfirmation );

    }



}
