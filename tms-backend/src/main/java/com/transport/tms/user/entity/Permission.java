package com.transport.tms.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
    @Column(length = 64)
    private String id; // e.g. TRIP_CREATE, INVOICE_VIEW

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, length = 64)
    private String module;

    private String description;
}
