// com/ecms/dto/MerchantDTO.java
package com.ecms.dto;

public class MerchantDTO {
    private Integer merchantId;
    private String merchantName;
    private String contactInfo;
    private String businessScope; // 经营范围
    private String status;  // 商家状态

    // 构造方法
    public MerchantDTO(Integer merchantId, String merchantName, String contactInfo, String businessScope, String status) {
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.contactInfo = contactInfo;
        this.businessScope = businessScope;
        this.status = status;
    }

    // Getter 和 Setter 方法
    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getBusinessScope() {
        return businessScope;
    }

    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
