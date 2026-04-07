package com.orderservice.event;

import com.orderservice.enums.OrderStatus;
import com.orderservice.exception.types.OrderNotFoundException;
import com.orderservice.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Component
public class OrderConfirmedConsumer {

    private final OrderRepository orderRepository;

    @RabbitListener(queues = "order.confirmed.queue")
    @Transactional

    public void handleOrderConfirmedEvent(OrderConfirmedEvent orderConfirmedEvent) {

        var nonPermittedStatus = List.of
                ( OrderStatus.CANCELLED,OrderStatus.FAILED,OrderStatus.DELIVERED , OrderStatus.RETURNED );

        var orderId = orderConfirmedEvent.orderId ( );

        var savedOrder = orderRepository.findById ( orderId )
                .orElseThrow ( () -> new OrderNotFoundException ( orderId ) );

        var orderStatus =  savedOrder.getStatus ();

        if (orderStatus.equals(OrderStatus.CONFIRMED)) {
            log.info("Order with Id {} is already confirmed ", orderId);
            return;
        }

        var canNotOrderStatusModified = nonPermittedStatus
                .stream ()
                .anyMatch ( orderStatus::equals );

        if (canNotOrderStatusModified) {
            log.warn ( "The status for the order with Id {} can not be more modified ", orderId );
            return;
        }

        savedOrder.setStatus ( OrderStatus.CONFIRMED );
        orderRepository.save ( savedOrder );
        log.info ( "Order {} marked as CONFIRMED " , orderId );
    }


}


