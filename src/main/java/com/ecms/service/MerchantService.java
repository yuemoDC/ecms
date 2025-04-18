// com/ecms/service/MerchantService.java
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
}
