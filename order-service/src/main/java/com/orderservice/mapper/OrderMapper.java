package com.orderservice.mapper;

import com.orderservice.dto.*;
import com.orderservice.entity.Order;
import com.orderservice.entity.OrderItem;
import com.orderservice.enums.OrderStatus;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class OrderMapper {

    public Order toOrder(PlaceOrderRequestDTO requestDTO, Long userId, Double totalAmount) {
        return Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .build();
    }

    public OrderResponseDTO toOrderResponseDto (Order order) {

        return new OrderResponseDTO(
                order.getId () ,
                order.getUserId (),

                order.getItems ()
                        .stream ()
                        .map ( this::toOrderItemResponseDto )
                        .toList ( ) ,

                order.getStatus (),
                order.getTotalAmount (),
                order.getCreatedAt (),
                order.getUpdatedAt ()
        );

    }

    public OrderItem toOrderItem(OrderItemRequestDTO itemDTO, InventoryResponseDTO inventory, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(itemDTO.productId());
        orderItem.setQuantity(itemDTO.quantity());
        orderItem.setItemPrice(inventory.price());
        orderItem.setOrder(order);
        return orderItem;
    }

    public OrderItemResponseDTO toOrderItemResponseDto(OrderItem orderItem) {
        return new OrderItemResponseDTO(
                orderItem.getId (),
                orderItem.getProductId (),
                orderItem.getQuantity (),
                orderItem.getItemPrice (),
                orderItem.getCreatedAt ()
        );
    }

    public List<OrderItem> toOrderItemList(
            List<OrderItemRequestDTO> itemDTOs,
            Map<OrderItemRequestDTO, InventoryResponseDTO> inventoryMap,
            Order order) {

        return itemDTOs.stream()
                .map(itemDTO -> {
                    InventoryResponseDTO inventory = inventoryMap.get(itemDTO);
                    return toOrderItem(itemDTO, inventory, order);
                })
                .collect( Collectors.toList());
    }

    public PlaceOrderResponseDTO toPlaceOrderResponseDTO(Order order) {
        return new PlaceOrderResponseDTO(
                order.getId(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getTotalAmount()
        );
    }
}