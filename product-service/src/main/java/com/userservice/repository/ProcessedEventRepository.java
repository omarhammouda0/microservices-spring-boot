package com.userservice.repository;

import com.userservice.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {

    @Query ("select e from ProcessedEvent e ")
    boolean existsByOrderId ( Long orderId );



}
