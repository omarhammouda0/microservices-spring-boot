package com.orderservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;


@Entity
@Table(name = "order_items")
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder

public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn ( name = "order_id" , nullable = false)
    private Order order;

    @Column (nullable = false)
    private Long productId;

    @Column (nullable = false)
    private Integer quantity;

    @Column (nullable = false)
    private Double itemPrice;

    @CreatedDate
    @Column (nullable = false)
    private Instant createdAt;

}
