package com.ecms.service;

import com.ecms.entity.SalesData;
import com.ecms.repository.SalesDataRepository;
import com.ecms.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

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

        if (merchantId == null || !merchantRepository.existsById(merchantId)) {
            logger.warn("Merchant ID={} does not exist", merchantId);
            return new ArrayList<>();
        }

        return salesDataRepository.findByMerchantId(merchantId);
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

    /**
     * Aggregate sales by region
     * @param salesDataList List of sales data to aggregate
     * @return aggregated sales by region
     */
    public Map<String, Integer> aggregateSalesByRegion(List<SalesData> salesDataList) {
        Map<String, Integer> regionAggregatedSales = new HashMap<>();

        for (SalesData salesData : salesDataList) {
            Map<String, Integer> regionSales = salesData.getRegionSales();
            for (Map.Entry<String, Integer> entry : regionSales.entrySet()) {
                regionAggregatedSales.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        return regionAggregatedSales;
    }

    /**
     * Aggregate sales by month
     * @param salesDataList List of sales data to aggregate
     * @return aggregated sales by month
     */
    public Map<String, BigDecimal> aggregateSalesByMonth(List<SalesData> salesDataList) {
        Map<String, BigDecimal> monthlySales = new HashMap<>();

        for (SalesData salesData : salesDataList) {
            // Convert java.util.Date to LocalDate safely
            LocalDate salesDate = convertToLocalDate(salesData.getSalesDate());
            if (salesDate != null) {
                String month = salesDate.getYear() + "-" + String.format("%02d", salesDate.getMonthValue());
                BigDecimal totalSales = salesData.getTotalSales();
                monthlySales.merge(month, totalSales, BigDecimal::add);
            }
        }

        return monthlySales;
    }

    /**
     * Aggregate sales by region and time (month)
     * @param salesDataList List of sales data to aggregate
     * @return aggregated sales by region and month
     */
    public Map<String, Map<String, Integer>> aggregateSalesByRegionAndTime(List<SalesData> salesDataList) {
        Map<String, Map<String, Integer>> aggregatedData = new HashMap<>();

        for (SalesData salesData : salesDataList) {
            // Convert java.util.Date to LocalDate safely
            LocalDate salesDate = convertToLocalDate(salesData.getSalesDate());
            if (salesDate != null) {
                String month = salesDate.getYear() + "-" + String.format("%02d", salesDate.getMonthValue());

                Map<String, Integer> regionSales = salesData.getRegionSales();
                for (Map.Entry<String, Integer> entry : regionSales.entrySet()) {
                    aggregatedData
                            .computeIfAbsent(month, k -> new HashMap<>())
                            .merge(entry.getKey(), entry.getValue(), Integer::sum);
                }
            }
        }

        return aggregatedData;
    }

    /**
     * Convert java.util.Date to LocalDate
     * @param date the date to convert
     * @return LocalDate representation
     */
    private LocalDate convertToLocalDate(Date date) {
        if (date == null) {
            return null;
        }

        // 兼容 java.sql.Date 和 java.util.Date
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        } else {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

}
