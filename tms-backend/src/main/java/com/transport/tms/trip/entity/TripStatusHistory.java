package com.transport.tms.trip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "trip_status_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String tripId;

    @Column(length = 32)
    private String oldStatus;

    @Column(nullable = false, length = 32)
    private String newStatus;

    @Column(nullable = false, length = 128)
    private String changedBy;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime changedAt;
}
