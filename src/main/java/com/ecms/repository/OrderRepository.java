package com.ecms.repository;

import com.ecms.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单仓库接口
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
    /**
     * 按商家ID查找订单，并按创建时间排序
     */
    List<Order> findByMerchantIdOrderByCreatedAt(Integer merchantId);
    List<Order> findByProductIdOrderByCreatedAtAsc(Integer productId);


    /**
     * 根据商家ID查找所有订单
     * @param merchantId 商家ID
     * @return 订单列表
     */
    List<Order> findByMerchantId(Integer merchantId);

    /**
     * 根据商家ID和时间范围查找订单
     * @param merchantId 商家ID
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 订单列表
     */
    List<Order> findByMerchantIdAndCreatedAtBetween(Integer merchantId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 根据商家ID和创建时间在指定日期之后查找订单
     * @param merchantId 商家ID
     * @param date 指定日期
     * @return 订单列表
     */
    List<Order> findByMerchantIdAndCreatedAtAfter(Integer merchantId, LocalDateTime date);

    /**
     * 根据创建时间在指定日期之后查找所有订单
     * @param date 指定日期
     * @return 订单列表
     */
    List<Order> findByCreatedAtAfter(LocalDateTime date);

    /**
     * 根据订单状态查找订单
     * @param status 订单状态
     * @return 订单列表
     */
    List<Order> findByOrderStatus(String status);

    /**
     * 根据商家ID和订单状态查找订单
     * @param merchantId 商家ID
     * @param status 订单状态
     * @return 订单列表
     */
    List<Order> findByMerchantIdAndOrderStatus(Integer merchantId, String status);

    /**
     * 根据产品ID查找订单
     * @param productId 产品ID
     * @return 订单列表
     */
    List<Order> findByProductId(Integer productId);

    /**
     * 根据商家ID和产品ID查找订单
     * @param merchantId 商家ID
     * @param productId 产品ID
     * @return 订单列表
     */
    List<Order> findByMerchantIdAndProductId(Integer merchantId, Integer productId);

    /**
     * 根据用户ID查找订单
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> findByCustomerId(Integer userId);
}
