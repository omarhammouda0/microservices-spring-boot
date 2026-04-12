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
    private final ProductEventPublisher productEventPublisher;

    @RabbitListener (queues = "order.created.queue")
    @Transactional
    public void handleOrderCreated (OrderCreatedEvent orderCreatedEvent) {

        var incomingEventId  = orderCreatedEvent.eventId () ;
        var orderId = orderCreatedEvent.orderId () ;

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

        productEventPublisher.publishOrderConfirmed ( orderId );

    }

    @RabbitListener(queues = "order.cancelled.queue")
    @Transactional
    public void handleOrderCancelled(OrderCancelledEvent orderCancelledEvent) {

        boolean isEventAlreadyProcessed = processedEventRepository.existsById ( orderCancelledEvent.eventId ( ) );

        if (isEventAlreadyProcessed) {
            log.warn ( "The event for this order has been already processed" );
            return;
        }

        orderCancelledEvent.items ( ).forEach ( item -> {
            var inventory = inventoryRepository.findByProductId ( item.productId ( ) )
                    .orElseThrow ( () -> new InventoryNotFoundException ( item.productId ( ) ) );

            int newQuantity = inventory.getQuantity ( ) + item.quantity ( );
            inventory.setQuantity ( newQuantity );
            inventoryRepository.save ( inventory );
            log.info ( "Restored {} units of product {}. New quantity: {}" ,
                    item.quantity ( ) , item.productId ( ) , newQuantity );
        } );


        var event = ProcessedEvent.builder ( )
                .id ( orderCancelledEvent.eventId ( ) )
                .timestamp ( Instant.now ( ) )
                .build ( );

        processedEventRepository.save ( event );
        log.info("Successfully processed cancellation for order: {}", orderCancelledEvent.orderId ());

    }



}
