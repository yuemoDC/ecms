package com.ecms.service;

import com.ecms.entity.Merchant;
import com.ecms.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantService {

    @Autowired
    private MerchantRepository merchantRepository;

    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }

    public Merchant getMerchantById(Integer id) {
        return merchantRepository.findById(id).orElse(null);
    }

    public Merchant createMerchant(Merchant merchant) {
        return merchantRepository.save(merchant);
    }

    public Merchant updateMerchant(Integer id, Merchant merchant) {
        if (merchantRepository.existsById(id)) {
            return merchantRepository.save(merchant);
        }
        return null;
    }

    public void deleteMerchant(Integer id) {
        merchantRepository.deleteById(id);
    }

    /**
     * 根据用户ID获取商家ID
     *
     * @param userId 用户ID
     * @return 商家ID，如果用户不是商家则返回null
     */
    public Integer getMerchantIdByUserId(Integer userId) {
        // 根据用户ID查询商家
        Merchant merchant = merchantRepository.findByUserId(userId);
        return merchant != null ? merchant.getMerchantId() : null;
    }
}
