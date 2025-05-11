package com.ecms.controller;

import com.ecms.entity.ForecastFile;
import com.ecms.service.ForecastFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/prophet")
@CrossOrigin(origins = "http://localhost:8082") // 允许来自 http://localhost:8081 的跨域请求
public class MerchantGetPredictionController {

    @Autowired
    private ForecastFileService forecastFileService;

    @GetMapping("/result")

    public ResponseEntity<?> getForecastResult(@RequestParam("merchant_id") Integer merchantId) {
        ForecastFile forecastFile = forecastFileService.getLatestFileByMerchantId(merchantId);
        if (forecastFile == null || forecastFile.getFilePath() == null) {
            return ResponseEntity.status(404).body("未找到预测结果文件");
        }

        String filePath = forecastFile.getFilePath();
        File csvFile = new File(filePath);
        if (!csvFile.exists()) {
            return ResponseEntity.status(404).body("文件路径无效或文件不存在");
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (headerLine == null) {
                return ResponseEntity.badRequest().body("CSV 文件为空");
            }

            String[] headers = headerLine.split(",");
            // 找出目标字段在 CSV 中的索引
            Map<String, Integer> indexMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                indexMap.put(headers[i].trim(), i);
            }

            // 检查是否包含必要字段
            String[] requiredFields = {"ds", "yhat", "yhat_lower", "yhat_upper"};
            for (String field : requiredFields) {
                if (!indexMap.containsKey(field)) {
                    return ResponseEntity.badRequest().body("CSV 文件缺少字段: " + field);
                }
            }

            List<Map<String, String>> result = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                Map<String, String> row = new HashMap<>();
                for (String field : requiredFields) {
                    int idx = indexMap.get(field);
                    if (idx < values.length) {
                        row.put(field, values[idx].trim());
                    }
                }
                result.add(row);
            }

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("读取文件失败: " + e.getMessage());
        }
    }

}
