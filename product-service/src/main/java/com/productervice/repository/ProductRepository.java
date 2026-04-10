package com.productervice.repository;

import com.productervice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ProductRepository extends JpaRepository <Product, Long> {


    @Query ( "SELECT p FROM Product p WHERE p.userId = :user_id" )
    List <Product> findByUserId(@Param ( "user_id" ) Long userId);

    @Query("select p from Product p where p.isActive = true")
    List<Product> getAllActiveProducts();
}

