package com.productervice.repository;

import com.productervice.entity.Inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory,Long> {

    @Query ( "select i from Inventory i where i.product.id = :product_id" )
    Optional<Inventory> findByProductId(@Param ( "product_id" ) Long productId);


}
