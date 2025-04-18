package com.ecms.service;

import com.ecms.entity.Merchant;
import com.ecms.entity.MerchantApplication;
import com.ecms.repository.MerchantApplicationRepository;
import com.ecms.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MerchantApplicationService {

    @Autowired
    private MerchantApplicationRepository repository;

    @Autowired
    private MerchantRepository merchantRepository;

    public List<MerchantApplication> getAllApplications() {
        return repository.findAll();
    }

    public MerchantApplication getApplicationById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public MerchantApplication createApplication(MerchantApplication application) {
        return repository.save(application);
    }

    public MerchantApplication updateApplication(Integer id, MerchantApplication application) {
        if (repository.existsById(id)) {
            application.setApplicationId(id);
            return repository.save(application);
        }
        return null;
    }

    public void deleteApplication(Integer id) {
        repository.deleteById(id);
    }

    @Transactional
    public MerchantApplication approveApplication(Integer id) {
        Optional<MerchantApplication> optional = repository.findById(id);
        if (optional.isPresent()) {
            MerchantApplication application = optional.get();

            if (application.getLicense() == null || application.getLicense().isEmpty()) {
                throw new IllegalArgumentException("商家资质证明不能为空");
            }

            if (application.getUserId() == null) {
                throw new IllegalArgumentException("用户ID不能为空");
            }

            application.setStatus(MerchantApplication.Status.approved);
            repository.save(application);

            Merchant merchant = new Merchant();
            merchant.setApplicationId(application.getApplicationId());
            merchant.setUserId(application.getUserId());
            merchant.setMerchantName(application.getMerchantName());
            merchant.setContactInfo(application.getContactInfo());
            merchant.setBusinessScope(application.getBusinessScope());
            merchant.setLicense(application.getLicense());
            merchantRepository.save(merchant);

            return application;
        }
        return null;
    }

    public MerchantApplication rejectApplication(Integer id) {
        Optional<MerchantApplication> optional = repository.findById(id);
        if (optional.isPresent()) {
            MerchantApplication application = optional.get();
            application.setStatus(MerchantApplication.Status.rejected);
            return repository.save(application);
        }
        return null;
    }
}
