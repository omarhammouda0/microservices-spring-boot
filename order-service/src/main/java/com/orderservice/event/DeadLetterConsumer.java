package com.orderservice.event;


import com.orderservice.enums.OrderStatus;
import com.orderservice.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
@Component

public class DeadLetterConsumer {

    private final OrderRepository orderRepository;


    @RabbitListener(queues = "order.failed.queue")
    @Transactional
    public void handleOrderFailed(OrderCreatedEvent orderCreatedEvent) {

        var orderId = orderCreatedEvent.orderId ( );
        var savedOrder = orderRepository.findById ( orderId );

        savedOrder.ifPresent ( order -> {
            order.setStatus ( OrderStatus.FAILED );
            orderRepository.save ( order );
            log.warn ( "Order {} marked as FAILED due to inventory reduction failure" , orderId );
                }
        );

    }

}
