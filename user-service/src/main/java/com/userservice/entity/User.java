package com.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.Instant;

@Entity
@Table (name = "users")

@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder



public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private  Long id;

    @Column (nullable = false)
    private String name;

    @Column (unique = true , nullable = false)
    private String email;

    @Column (nullable = false , name = "active")
    private Boolean isActive ;

    @CreatedDate
    @Column(name = "created_at" ,  updatable = false)
    private Instant createdAt;

}
