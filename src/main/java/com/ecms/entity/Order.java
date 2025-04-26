package com.ecms.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @Column(nullable = false)
    private Integer merchantId;

    @Column(nullable = false)
    private Integer customerId;

    @Column(nullable = false)
    private Integer productId;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private String orderStatus;

    private String region;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
