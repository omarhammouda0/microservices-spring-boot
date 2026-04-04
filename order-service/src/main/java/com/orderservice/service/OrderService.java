package com.orderservice.service;

import com.orderservice.client.ProductServiceClient;
import com.orderservice.dto.InventoryResponseDTO;
import com.orderservice.dto.OrderItemRequestDTO;
import com.orderservice.dto.PlaceOrderRequestDTO;
import com.orderservice.dto.PlaceOrderResponseDTO;
import com.orderservice.entity.Order;
import com.orderservice.enums.OrderStatus;
import com.orderservice.exception.types.StockInsufficient;
import com.orderservice.mapper.OrderMapper;
import com.orderservice.repository.OrderItemRepository;
import com.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final ProductServiceClient  productServiceClient;

    public InventoryResponseDTO getInventory(Long productId) {
        return productServiceClient.getInventory
                ( productId );
    }

    public Map<OrderItemRequestDTO, InventoryResponseDTO> stockCheck
            (PlaceOrderRequestDTO placeOrderRequestDTO) {

        Map<OrderItemRequestDTO, InventoryResponseDTO> map = new HashMap<> ( );

        placeOrderRequestDTO.orderItemRequestDTOList ( )
                .forEach (
                        item -> {

                            var inventory = getInventory ( item.productId ( ) );


                            if (inventory.quantity ( ) < item.quantity ( )) {
                                throw new StockInsufficient ( item.productId ( ) );
                            }

                            map.put ( item , inventory );
                        }
                );

        return map;
    }

    public Double calculateTotalAmount(Map<OrderItemRequestDTO, InventoryResponseDTO> map) {

        var totalAmount = 0.0;

        return map.entrySet ( )
                 .stream ( )
                 .mapToDouble ( entry -> entry.getKey ().quantity ( ) * entry.getValue ( ).price ( ))
                 .sum ();

    }

    @Transactional
    public PlaceOrderResponseDTO placeOrder(PlaceOrderRequestDTO placeOrderRequestDTO , Long userId) {

        var check = stockCheck ( placeOrderRequestDTO );
        var totalAmount = calculateTotalAmount ( check );
        var orderItems = placeOrderRequestDTO.orderItemRequestDTOList ( );

        Order order = Order.builder ( )

                .totalAmount ( totalAmount )
                .userId ( userId )
                .status ( OrderStatus.PENDING )

                .build ( );

        var savedOrder = orderRepository.save ( order );
        var savedOrderItems = orderItemRepository.saveAll
                ( orderMapper.toOrderItemList ( orderItems , check , order ) );

        savedOrder.setItems ( savedOrderItems );

        // Publish event

        return orderMapper.toPlaceOrderResponseDTO ( savedOrder );
    }

}


