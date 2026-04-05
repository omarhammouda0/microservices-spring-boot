package com.productservice.event;

import com.productservice.entity.ProcessedEvent;
import com.productservice.exception.types.InSufficentStockLevel;
import com.productservice.exception.types.InventoryNotFoundException;
import com.productservice.repository.InventoryRepository;
import com.productservice.repository.ProcessedEventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@AllArgsConstructor
@Component
@Slf4j

public class OrderEventConsumer {

    private final ProcessedEventRepository processedEventRepository;
    private final InventoryRepository inventoryRepository;

    @RabbitListener (queues = "order.created.queue")
    @Transactional
    public void handleOrderCreated (OrderCreatedEvent orderCreatedEvent) {

        var incomingEventId  = orderCreatedEvent.eventId () ;

        if (processedEventRepository.existsById (incomingEventId)) {
            log.info ( "The event with id {} has already been processed" , incomingEventId );
            return;
        }

        var productId = orderCreatedEvent.productId () ;
        var itemInventory =  inventoryRepository.findByProductId ( orderCreatedEvent.productId () )
                .orElseThrow (  () -> new InventoryNotFoundException (orderCreatedEvent.productId () ) );
        var currentQuantity = itemInventory.getQuantity () ;
        var itemQuantity = orderCreatedEvent.quantity () ;

        if (currentQuantity < itemQuantity ) {
            throw new InSufficentStockLevel ( productId );
        }

        currentQuantity -= itemQuantity ;
        itemInventory.setQuantity ( currentQuantity );
        inventoryRepository.save ( itemInventory );

        var event = ProcessedEvent.builder ()
                .id (orderCreatedEvent.eventId () )
                .timestamp ( Instant.now () )
                .build () ;

        processedEventRepository.save ( event );
    }



}
