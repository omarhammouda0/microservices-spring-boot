package com.orderservice.service;

import com.orderservice.client.ProductServiceClient;
import com.orderservice.dto.*;
import com.orderservice.entity.Order;
import com.orderservice.enums.OrderStatus;
import com.orderservice.event.OrderEventPublisher;
import com.orderservice.exception.types.InvalidStatusTransition;
import com.orderservice.exception.types.OrderNotFoundException;
import com.orderservice.exception.types.StockInsufficient;
import com.orderservice.mapper.OrderMapper;
import com.orderservice.repository.OrderItemRepository;
import com.orderservice.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;


@AllArgsConstructor
@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final ProductServiceClient productServiceClient;
    private final OrderEventPublisher orderEventPublisher;
    private final HelperService helperService;

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

        return map.entrySet ( )
                .stream ( )
                .mapToDouble ( entry -> entry.getKey ( ).quantity ( ) * entry.getValue ( ).price ( ) )
                .sum ( );

    }

    @Transactional
    public PlaceOrderResponseDTO placeOrder(PlaceOrderRequestDTO placeOrderRequestDTO ,
                                            Long userId , String userRole) {

        helperService.checkUserRole ( "USER" , userRole );

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

        savedOrderItems.forEach (
                item -> {
                    orderEventPublisher.publishOrderCreated ( item , savedOrder.getId ( ) );
                }
        );

        return orderMapper.toPlaceOrderResponseDTO ( savedOrder );
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO getOrder(Long orderId , Long userId) {

        var order = orderRepository.findById ( orderId ).orElseThrow (
                () -> new OrderNotFoundException ( orderId )
        );

        helperService.checkUserIdentity ( order.getUserId ( ) , userId );

        return orderMapper.toOrderResponseDto ( order );
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getOrders(Pageable pageable , String userRole) {

        helperService.checkIfAdmin ( userRole );

        return orderRepository.findAll ( pageable )
                .map ( orderMapper::toOrderResponseDto );
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getOrdersByUser(Long userId , Pageable pageable) {


        return orderRepository.getAllOrdersForUser ( userId , pageable )
                .map ( orderMapper::toOrderResponseDto );

    }

    @Transactional
    public OrderResponseDTO orderCancel(Long orderId , Long userId) {

        var order = orderRepository.findById ( orderId ).orElseThrow (
                () -> new OrderNotFoundException ( orderId )
        );

        helperService.checkUserIdentity ( order.getUserId ( ) , userId );

        var orderStatus = order.getStatus ( );

        if (orderStatus != OrderStatus.PENDING && orderStatus != OrderStatus.CONFIRMED) {
            throw new InvalidStatusTransition ( orderStatus , OrderStatus.CANCELLED );
        }

        order.setStatus ( OrderStatus.CANCELLED );
        orderRepository.save ( order );
        orderEventPublisher.publishOrderCancelled ( orderId , order.getItems ( ) );

        return orderMapper.toOrderResponseDto ( order );
    }

    @Transactional
    public OrderResponseDTO updateOrderStatus(Long orderId , OrderStatus newOrderStatus , String currentUserRole) {

        var order = orderRepository.findById ( orderId ).orElseThrow (
                () -> new OrderNotFoundException ( orderId )
        );

        helperService.checkIfAdmin ( currentUserRole );
        var oldStatus = order.getStatus ( );

        helperService.checkAdminStatusTransition ( newOrderStatus , oldStatus );

        order.setStatus ( newOrderStatus );
        log.info ( "Order status for order {} changed to {}", orderId , newOrderStatus );

        orderRepository.save ( order );

        return orderMapper.toOrderResponseDto ( order );

    }




}





//Good — finish Order Service CRUD first.
//Looking at the agenda, Order Service still needs:
//
//PUT /orders/{id}/status — update status, ADMIN only
//Status machine validation on REST endpoints — 409 on invalid transition
//
//Before writing — what statuses should ADMIN be able to set via this endpoint? Think about which transitions make sense from
//an admin perspective that a user can't do themselves.
