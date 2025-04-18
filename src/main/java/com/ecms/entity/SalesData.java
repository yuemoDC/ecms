package com.ecms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "sales_data")
public class SalesData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer salesId;

    @Column(nullable = false)
    private Integer merchantId;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date salesDate;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalSales;

    @Column(nullable = false)
    private Integer totalOrders;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal avgOrderValue;

    @Column(columnDefinition = "JSON")
    private String regionSales;  // 区域销售分布
}
