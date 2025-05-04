package com.ecms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class DeepSeekService {
    private static final Logger logger = LoggerFactory.getLogger(DeepSeekService.class);

    @Value("${deepseek.api.key}")
    private String apiKey;

    private final String endpoint = "https://api.deepseek.com/v1/chat/completions";

    /**
     * 生成销售建议
     * @param prompt 提示文本
     * @return 生成的建议
     */
    public String generateSuggestion(String prompt) {
        return callDeepSeek(prompt);
    }

    /**
     * 基于预测数据生成销售建议
     * @param dates 预测日期列表
     * @param predictions 预测值列表
     * @return 生成的销售建议
     */
    public String generateSuggestionFromPredictionData(List<String> dates, List<Double> predictions) {
        if (dates == null || predictions == null || dates.isEmpty() || predictions.isEmpty()) {
            logger.warn("预测数据为空，无法生成建议");
            return "暂无足够的数据生成建议，请确保有有效的销售预测数据。";
        }

        try {
            // 计算基本统计量，帮助AI分析
            DoubleSummaryStatistics stats = predictions.stream().mapToDouble(Double::doubleValue).summaryStatistics();

            // 计算趋势（简单线性）
            double firstValue = predictions.get(0);
            double lastValue = predictions.get(predictions.size() - 1);
            double trendPercent = ((lastValue - firstValue) / firstValue) * 100;
            String trendDirection = trendPercent >= 0 ? "上升" : "下降";

            // 构建提示信息，包含更多上下文
            String prompt = String.format(
                    "作为销售分析专家，请根据以下数据为商家提供营销和库存建议：\n\n" +
                            "预测周期：%s 至 %s\n" +
                            "销售趋势：%s %.2f%%\n" +
                            "平均预测销售额：¥%.2f\n" +
                            "最高预测销售额：¥%.2f\n" +
                            "最低预测销售额：¥%.2f\n" +
                            "总预测销售额：¥%.2f\n\n" +
                            "请基于上述销售预测趋势，分析给出：\n" +
                            "1. 库存管理建议\n" +
                            "2. 营销活动安排\n" +
                            "3. 定价策略优化\n" +
                            "4. 新客户开发策略\n\n" +
                            "请保持建议的具体实用性，针对当前销售趋势提出可执行的措施。",
                    dates.get(0), dates.get(dates.size() - 1),
                    trendDirection, Math.abs(trendPercent),
                    stats.getAverage(),
                    stats.getMax(),
                    stats.getMin(),
                    stats.getSum()
            );

            logger.info("为销售趋势生成AI建议，数据点数量: {}", predictions.size());
            return callDeepSeek(prompt);
        } catch (Exception e) {
            logger.error("生成销售建议时出错", e);
            return generateFallbackSuggestion(predictions);
        }
    }

    /**
     * 调用DeepSeek API
     * @param prompt 提示文本
     * @return API响应
     */
    private String callDeepSeek(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-chat");
            requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1000);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            logger.info("调用DeepSeek API生成建议");
            ResponseEntity<Map> response = restTemplate.exchange(endpoint, HttpMethod.POST, requestEntity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                try {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                        if (message != null && message.containsKey("content")) {
                            return (String) message.get("content");
                        }
                    }
                    throw new RuntimeException("无法从API响应中提取内容");
                } catch (Exception e) {
                    logger.error("解析DeepSeek API响应失败", e);
                    return "AI服务响应格式错误，请稍后重试。";
                }
            } else {
                logger.error("DeepSeek API请求失败: {}", response.getStatusCode());
                return "AI服务请求失败，请稍后重试。";
            }
        } catch (Exception e) {
            logger.error("调用DeepSeek API出错", e);
            return "无法连接到AI服务，请检查网络连接或稍后重试。";
        }
    }

    /**
     * 生成备用建议（当API调用失败时）
     * @param predictions 预测数据
     * @return 备用建议
     */
    private String generateFallbackSuggestion(List<Double> predictions) {
        try {
            // 简单分析销售趋势
            double firstValue = predictions.get(0);
            double lastValue = predictions.get(predictions.size() - 1);
            double trendPercent = ((lastValue - firstValue) / firstValue) * 100;

            if (trendPercent >= 10) {
                return "销售预测呈明显上升趋势，建议:\n\n" +
                        "1. 库存管理：适当增加库存量，防止因缺货导致销售机会损失\n" +
                        "2. 营销策略：加大宣传力度，把握销售增长机会\n" +
                        "3. 客户服务：提高服务质量，确保老客户满意度和复购率";
            } else if (trendPercent >= 0) {
                return "销售预测呈轻微上升趋势，建议:\n\n" +
                        "1. 库存管理：维持当前库存水平，确保供应稳定\n" +
                        "2. 营销策略：保持当前营销力度，可尝试新的推广渠道\n" +
                        "3. 产品优化：分析畅销产品特点，加强产品竞争力";
            } else if (trendPercent >= -10) {
                return "销售预测呈轻微下降趋势，建议:\n\n" +
                        "1. 库存管理：适当减少库存，防止积压\n" +
                        "2. 营销策略：可考虑促销活动刺激销售\n" +
                        "3. 客户分析：了解客户需求变化，调整产品策略";
            } else {
                return "销售预测呈明显下降趋势，建议:\n\n" +
                        "1. 库存管理：大幅降低库存，减少资金占用\n" +
                        "2. 营销策略：推出折扣促销或清仓活动\n" +
                        "3. 产品调整：考虑调整产品结构，开发新产品应对市场变化";
            }
        } catch (Exception e) {
            logger.error("生成备用建议失败", e);
            return "根据销售预测数据，建议您关注库存管理和营销策略的调整，以适应市场变化。";
        }
    }
}
