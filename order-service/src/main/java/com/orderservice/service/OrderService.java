package com.orderservice.service;

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
import java.util.Objects;


/**
 * Application service for order lifecycle management.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Place orders with up-front stock validation via
 *         {@link ProductServiceClient} (Feign + circuit breaker)</li>
 *     <li>Admin-controlled status transitions
 *         ({@code PENDING → CONFIRMED → DELIVERED → RETURNED})
 *         validated by {@link HelperService#checkAdminStatusTransition}</li>
 *     <li>User or admin driven cancellation that re-publishes an
 *         {@code OrderCancelled} event so product-service restores inventory</li>
 *     <li>Self-access queries: a user may only see their own orders
 *         (unless ADMIN, which may list all orders)</li>
 * </ul>
 *
 * <p>Order mutations publish RabbitMQ events. Consumers on the product-service
 * side use idempotency tracking ({@code ProcessedEvent}) to safely handle
 * retries, so at-least-once delivery is tolerated.
 */
@AllArgsConstructor
@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderEventPublisher orderEventPublisher;
    private final HelperService helperService;
    private final ProductServiceClient productServiceClient;

    /**
     * Fetches the current inventory snapshot for a product via Feign.
     *
     * @param productId product id
     * @return current inventory DTO, or a circuit-breaker fallback if product-service is down
     */
    public InventoryResponseDTO getInventory(Long productId){
        return productServiceClient.getInventoryByProductId ( productId );

    }

    /**
     * Validates that every requested line item has enough stock, building a
     * map of {@code orderItem -> inventorySnapshot} used downstream for
     * price calculation.
     *
     * @param placeOrderRequestDTO the order request
     * @return a map keyed by order item with its corresponding inventory snapshot
     * @throws StockInsufficient if any line item's requested quantity exceeds current stock
     */
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

    /**
     * Calculates the total order amount from the (item, inventory) pairs
     * produced by {@link #stockCheck}.
     *
     * @param map map of order items keyed with their inventory snapshot
     * @return sum of {@code quantity * unitPrice} across all items
     */
    public Double calculateTotalAmount(Map<OrderItemRequestDTO, InventoryResponseDTO> map) {

        return map.entrySet ( )
                .stream ( )
                .mapToDouble ( entry -> entry.getKey ( ).quantity ( ) * entry.getValue ( ).price ( ) )
                .sum ( );

    }

    /**
     * Places a new order for the given user.
     *
     * <p>Flow:
     * <ol>
     *     <li>Validate stock for every requested item (fails fast on shortage)</li>
     *     <li>Compute total from the captured snapshot prices</li>
     *     <li>Persist the {@code Order} in {@code PENDING} state with its items</li>
     *     <li>Publish one {@code OrderCreated} event per line item so
     *         product-service reserves the inventory asynchronously</li>
     * </ol>
     *
     * @param placeOrderRequestDTO validated order request
     * @param userId               caller id (owner of the new order)
     * @param userRole             caller role
     * @return a lightweight response with id, status, timestamps, and total
     * @throws StockInsufficient if any item's requested quantity exceeds stock
     */
    @Transactional
    public PlaceOrderResponseDTO placeOrder(PlaceOrderRequestDTO placeOrderRequestDTO ,
                                            Long userId , String userRole) {


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


        savedOrderItems.forEach (
                item -> {
                    orderEventPublisher.publishOrderCreated ( item , savedOrder.getId ( ) );
                }
        );

        return orderMapper.toPlaceOrderResponseDTO ( savedOrder );
    }

    /**
     * Fetches a single order the caller owns.
     *
     * @param orderId order id
     * @param userId  caller id (must match the order's owner)
     * @return the full order DTO with line items
     * @throws OrderNotFoundException                                   if the order does not exist
     * @throws com.orderservice.exception.types.NotAuthorizedException if the caller does not own the order
     */
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrder(Long orderId , Long userId) {

        var order = orderRepository.findById ( orderId ).orElseThrow (
                () -> new OrderNotFoundException ( orderId )
        );

        helperService.checkUserIdentity ( order.getUserId ( ) , userId );

        return orderMapper.toOrderResponseDto ( order );
    }

    /**
     * Returns a paginated list of all orders. Admin-only.
     *
     * @param pageable pagination/sort parameters
     * @param userRole caller role (must be {@code ADMIN})
     * @return a page of order DTOs
     * @throws com.orderservice.exception.types.NotAuthorizedException if caller is not ADMIN
     */
    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getOrders(Pageable pageable , String userRole) {

        helperService.checkIfAdmin ( userRole );

        return orderRepository.findAll ( pageable )
                .map ( orderMapper::toOrderResponseDto );
    }

    /**
     * Returns a paginated list of orders for the given user.
     *
     * <p>Non-admin callers may only request their own orders. An ADMIN may
     * request anyone's orders.
     *
     * @param userId           owner whose orders are being listed
     * @param pageable         pagination/sort parameters
     * @param requestingUserId caller id
     * @param userRole         caller role
     * @return a page of order DTOs
     * @throws com.orderservice.exception.types.NotAuthorizedException if caller is not the owner and not ADMIN
     */
    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getOrdersByUser
            (Long userId , Pageable pageable , Long requestingUserId , String userRole) {

        if (! Objects.equals ( requestingUserId , userId )   ) {
            helperService.checkIfAdmin ( userRole );
        }

        return orderRepository.getAllOrdersForUser ( userId , pageable )
                .map ( orderMapper::toOrderResponseDto );

    }

    /**
     * Cancels an order owned by the caller.
     *
     * <p>Only orders in {@code PENDING} or {@code CONFIRMED} state can be
     * cancelled. On success, an {@code OrderCancelled} event is published so
     * product-service restores the reserved inventory.
     *
     * @param orderId target order
     * @param userId  caller id (must own the order)
     * @return the updated order DTO
     * @throws OrderNotFoundException                                     if the order does not exist
     * @throws com.orderservice.exception.types.NotAuthorizedException   if the caller does not own the order
     * @throws InvalidStatusTransition                                    if the order is not in a cancellable state
     */
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

    /**
     * Admin-only status transition for an order.
     *
     * <p>Allowed transitions (enforced by
     * {@link HelperService#checkAdminStatusTransition}):
     * <ul>
     *     <li>{@code CONFIRMED → DELIVERED}</li>
     *     <li>{@code CONFIRMED → FAILED}</li>
     *     <li>{@code DELIVERED → RETURNED}</li>
     * </ul>
     *
     * @param orderId         target order
     * @param newOrderStatus  desired new status
     * @param currentUserRole caller role (must be {@code ADMIN})
     * @return the updated order DTO
     * @throws OrderNotFoundException                                     if the order does not exist
     * @throws com.orderservice.exception.types.NotAuthorizedException   if caller is not ADMIN
     * @throws InvalidStatusTransition                                    if the requested transition is illegal
     */
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



