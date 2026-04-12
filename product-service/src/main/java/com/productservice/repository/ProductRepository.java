package com.productservice.repository;

import com.productservice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {


    Page<Product> findAllByIsActiveTrue(Pageable pageable);

    Page<Product> findByUserIdAndIsActiveTrue(Long userId, Pageable pageable);


    List<Product> findByIsActiveTrue();

    List<Product> findByUserId(Long userId);

    Optional<Product> findByIdAndIsActiveTrue(Long id);

    boolean existsByIdAndIsActiveTrue(Long id);

    @Query("SELECT p FROM Product p WHERE p.isActive = true")
    List<Product> getAllActiveProducts();

    @Query("SELECT p.isActive FROM Product p WHERE p.id = :id")
    Boolean isProductActive(Long id);
}