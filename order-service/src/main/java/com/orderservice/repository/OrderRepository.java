package com.orderservice.repository;

import com.orderservice.entity.Order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface OrderRepository extends JpaRepository<Order,Long> {

    @Query ("select o from Order o where o.userId= :user_id")
    Page <Order> getAllOrdersForUser (@Param ( "user_id" ) Long userId ,
                                      Pageable pageable );



}
