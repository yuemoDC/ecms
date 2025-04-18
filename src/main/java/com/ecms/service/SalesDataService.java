package com.ecms.service;

import com.ecms.entity.SalesData;
import com.ecms.repository.SalesDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalesDataService {

    @Autowired
    private SalesDataRepository salesDataRepository;

    public List<SalesData> getSalesDataByMerchantId(Integer merchantId) {
        return salesDataRepository.findByMerchantId(merchantId);
    }

    public List<SalesData> getAllSalesData() {
        return salesDataRepository.findAll();
    }

    public SalesData createSalesData(SalesData salesData) {
        return salesDataRepository.save(salesData);
    }

    public SalesData updateSalesData(Integer id, SalesData salesData) {
        if (salesDataRepository.existsById(id)) {
            salesData.setSalesId(id);
            return salesDataRepository.save(salesData);
        }
        return null;
    }

    public void deleteSalesData(Integer id) {
        if (salesDataRepository.existsById(id)) {
            salesDataRepository.deleteById(id);
        }
    }
}
