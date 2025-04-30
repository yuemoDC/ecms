package com.ecms.service;

import com.ecms.entity.SalesData;
import com.ecms.repository.SalesDataRepository;
import com.ecms.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.sql.Date;
import java.time.ZoneId;
import java.util.List;
import java.util.ArrayList;

@Service
public class SalesDataService {
    private static final Logger logger = LoggerFactory.getLogger(SalesDataService.class);

    @Autowired
    private SalesDataRepository salesDataRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    /**
     * Get sales data for a merchant
     * @param merchantId merchant ID
     * @return list of sales data
     */
    public List<SalesData> getSalesDataByMerchantId(Integer merchantId) {
        logger.info("Retrieving sales data for merchant ID={}", merchantId);

        // Check if merchant exists first
        if (merchantId == null || !merchantRepository.existsById(merchantId)) {
            logger.warn("Merchant ID={} does not exist", merchantId);
            return new ArrayList<>();
        }

        return salesDataRepository.findByMerchantId(merchantId);
    }

    /**
     * Get sales data for a merchant and product
     * @param merchantId merchant ID
     * @param productId product ID
     * @return list of sales data
     */
    public List<SalesData> getSalesDataByMerchantIdAndProductId(Integer merchantId, Integer productId) {
        logger.info("Retrieving sales data for merchant ID={}, product ID={}", merchantId, productId);

        // Check if merchant exists
        if (merchantId == null || !merchantRepository.existsById(merchantId)) {
            logger.warn("Merchant ID={} does not exist", merchantId);
            return new ArrayList<>();
        }

        // In a real implementation, you would parse the regionSales JSON field to extract product-specific data
        return new ArrayList<>();
    }

    /**
     * Get all sales data
     * @return list of all sales data
     */
    public List<SalesData> getAllSalesData() {
        return salesDataRepository.findAll();
    }

    /**
     * Create sales data record
     * @param salesData sales data to create
     * @return created sales data
     */
    public SalesData createSalesData(SalesData salesData) {
        // Verify merchant exists before saving
        if (salesData.getMerchantId() == null || !merchantRepository.existsById(salesData.getMerchantId())) {
            logger.error("Cannot create sales data: Merchant ID={} does not exist", salesData.getMerchantId());
            return null;
        }

        return salesDataRepository.save(salesData);
    }

    /**
     * Update sales data record
     * @param id sales data ID
     * @param salesData updated sales data
     * @return updated sales data
     */
    public SalesData updateSalesData(Integer id, SalesData salesData) {
        if (!salesDataRepository.existsById(id)) {
            logger.warn("Sales data ID={} does not exist", id);
            return null;
        }

        // Verify merchant exists before updating
        if (salesData.getMerchantId() == null || !merchantRepository.existsById(salesData.getMerchantId())) {
            logger.error("Cannot update sales data: Merchant ID={} does not exist", salesData.getMerchantId());
            return null;
        }

        salesData.setSalesId(id);
        return salesDataRepository.save(salesData);
    }

    /**
     * Delete sales data record
     * @param id sales data ID to delete
     */
    public void deleteSalesData(Integer id) {
        if (salesDataRepository.existsById(id)) {
            salesDataRepository.deleteById(id);
        } else {
            logger.warn("Cannot delete: Sales data ID={} does not exist", id);
        }
    }
}
