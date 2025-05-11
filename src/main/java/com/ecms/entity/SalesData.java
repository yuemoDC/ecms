package com.ecms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

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
    @JsonFormat(pattern = "yyyy-MM-dd") // 自定义日期格式（可选）
    private Date salesDate;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalSales;

    @Column(nullable = false)
    private Integer totalOrders;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal avgOrderValue;

    @ElementCollection
    @CollectionTable(name = "region_sales", joinColumns = @JoinColumn(name = "sales_id"))
    @MapKeyColumn(name = "region")
    @Column(name = "sales_count")
    private Map<String, Integer> regionSales;  // 区域销售分布，使用Map类型



    // 添加 productId 字段
    @Column
    private Integer productId;
}
