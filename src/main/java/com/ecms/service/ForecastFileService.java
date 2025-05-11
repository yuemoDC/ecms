package com.ecms.service;


import com.ecms.entity.ForecastFile;
import com.ecms.repository.ForecastFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ForecastFileService {

    @Autowired
    private ForecastFileRepository forecastFileRepository;

    public ForecastFile getLatestFileByMerchantId(Integer merchantId) {
        return forecastFileRepository.findTopByMerchantIdOrderByCreatedAtDesc(merchantId);
    }

    public ForecastFile saveForecastFile(Integer merchantId, String filePath) {
        ForecastFile forecastFile = new ForecastFile();
        forecastFile.setMerchantId(merchantId);
        forecastFile.setFilePath(filePath);
        return forecastFileRepository.save(forecastFile);
    }
}
