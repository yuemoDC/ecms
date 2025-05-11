// com/ecms/controller/MerchantController.java
package com.ecms.controller;

import com.ecms.entity.Merchant;
import com.ecms.entity.MerchantApplication;
import com.ecms.dto.MerchantDTO;
import com.ecms.service.MerchantApplicationService;
import com.ecms.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/merchants")
@CrossOrigin(origins = "http://localhost:8082") // 允许来自 http://localhost:8081 的请求
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MerchantApplicationService merchantApplicationService;

    // 获取所有商家信息，包含商家名称、联系方式、经营范围
    @GetMapping
    public List<MerchantDTO> getAllMerchants() {
        List<Merchant> merchants = merchantService.getAllMerchants();
        List<MerchantDTO> result = new ArrayList<>();

        // 获取每个商家的入驻申请信息
        for (Merchant merchant : merchants) {
            // 从商家入驻申请表获取商家名称和联系方式等信息
            MerchantApplication application = merchantApplicationService.getApplicationById(merchant.getApplicationId());
            // 构建MerchantDTO对象
            MerchantDTO merchantDTO = new MerchantDTO(
                    merchant.getMerchantId(),
                    application.getMerchantName(),  // 获取商家名称
                    application.getContactInfo(),   // 获取联系方式
                    application.getBusinessScope(), // 获取经营范围
                    merchant.getStatus()           // 获取商家状态
            );
            result.add(merchantDTO);
        }
        return result;
    }

    // 根据ID获取商家信息
    @GetMapping("/{id}")
    public Merchant getMerchantById(@PathVariable Integer id) {
        return merchantService.getMerchantById(id);
    }

    // 创建商家
    @PostMapping
    public Merchant createMerchant(@RequestBody Merchant merchant) {
        return merchantService.createMerchant(merchant);
    }

    // 更新商家信息
    @PutMapping("/{id}")
    public Merchant updateMerchant(@PathVariable Integer id, @RequestBody Merchant merchant) {
        return merchantService.updateMerchant(id, merchant);
    }

    // 删除商家
    @DeleteMapping("/{id}")
    public void deleteMerchant(@PathVariable Integer id) {
        merchantService.deleteMerchant(id);
    }
}
