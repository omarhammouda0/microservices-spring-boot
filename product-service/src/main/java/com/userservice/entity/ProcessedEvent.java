package com.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "processed_events ")
@NoArgsConstructor
@AllArgsConstructor

public class ProcessedEvent {

    @Id
    private UUID id;

    @Column
    private Instant timestamp;

}
