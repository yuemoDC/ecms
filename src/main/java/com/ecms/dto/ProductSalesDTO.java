package com.ecms.dto; // 定义包路径

import lombok.AllArgsConstructor; // 导入 AllArgsConstructor 注解
import lombok.Data; // 导入 Data 注解
import java.math.BigDecimal; // 导入 BigDecimal 类
import java.util.Date; // 导入 Date 类

@Data // 自动生成 getter, setter, toString, equals 和 hashCode 方法
@AllArgsConstructor // 自动生成包含所有字段的构造函数
public class ProductSalesDTO {
    private Date salesDate; // 销售日期
    private BigDecimal totalSalesAmount; // 总销售额
    private Integer orderCount; // 订单数量
    private BigDecimal averageOrderValue; // 平均订单价值

    // 处理除零异常的构造函数
    public ProductSalesDTO(Date salesDate, BigDecimal totalSalesAmount, Integer orderCount) {
        this.salesDate = salesDate;
        this.totalSalesAmount = totalSalesAmount;
        this.orderCount = orderCount;
        // 计算平均订单价值，避免除零异常
        this.averageOrderValue = orderCount == 0 ? BigDecimal.ZERO
                : totalSalesAmount.divide(new BigDecimal(orderCount), 2, BigDecimal.ROUND_HALF_UP);
    }
}