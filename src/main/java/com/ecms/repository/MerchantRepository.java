package com.ecms.repository;

import com.ecms.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Integer> {
    /**
     * 根据用户ID查找商家
     *
     * @param userId 用户ID
     * @return 商家实体，如果没有找到则返回null
     */
    Merchant findByUserId(Integer userId);
}
