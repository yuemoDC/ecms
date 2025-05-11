package com.ecms.controller;

import com.ecms.entity.MerchantApplication;
import com.ecms.service.MerchantApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant-applications")
@CrossOrigin(origins = "http://localhost:8082")
public class MerchantApplicationController {

    @Autowired
    private MerchantApplicationService merchantApplicationService;



    @GetMapping("/{id}")
    public ResponseEntity<MerchantApplication> getMerchantApplicationById(@PathVariable Integer id) {
        MerchantApplication application = merchantApplicationService.getApplicationById(id);
        return application != null ? ResponseEntity.ok(application) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<MerchantApplication> createMerchantApplication(@RequestBody MerchantApplication merchantApplication) {
        try {
            if (merchantApplication.getUserId() == null) {
                return ResponseEntity.badRequest().build();
            }
            MerchantApplication created = merchantApplicationService.createApplication(merchantApplication);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MerchantApplication> updateMerchantApplication(
            @PathVariable Integer id,
            @RequestBody MerchantApplication merchantApplication) {
        MerchantApplication updated = merchantApplicationService.updateApplication(id, merchantApplication);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMerchantApplication(@PathVariable Integer id) {
        merchantApplicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveMerchantApplication(@PathVariable Integer id) {
        try {
            MerchantApplication application = merchantApplicationService.approveApplication(id);
            return application != null ?
                    ResponseEntity.ok(application) :
                    ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("审核失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<MerchantApplication> rejectMerchantApplication(@PathVariable Integer id) {
        MerchantApplication application = merchantApplicationService.rejectApplication(id);
        return application != null ? ResponseEntity.ok(application) : ResponseEntity.notFound().build();
    }
    @GetMapping
    public List<MerchantApplication> getAllMerchantApplications(@RequestParam(required = false) Integer userId) {
        System.out.println("Received userId: " + userId);// 如果有 userId 参数，则返回该用户的商家申请，否则返回所有商家的申请
        if (userId != null) {
            return merchantApplicationService.getApplicationsByUserId(userId);
        } else {
            return merchantApplicationService.getAllApplications();
        }
    }


}
