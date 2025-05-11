package com.ecms.controller;

import com.ecms.entity.SalesData;
import com.ecms.repository.SalesDataRepository;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/merchant-sales")
@CrossOrigin(origins = "http://localhost:8082")
public class MerchantDisplayController {

    @Autowired
    private SalesDataRepository salesDataRepository;

    /**
     * 导出所有销售数据到CSV文件
     * @return 返回操作结果
     */
    @PostMapping("/export")
    public String exportSalesData() {
        // 获取所有销售数据
        List<SalesData> salesDataList = salesDataRepository.findAll();
        if (salesDataList.isEmpty()) {
            return "No sales data available to export.";
        }

        // 创建文件夹，如果没有的话
        File folder = new File("data");
        if (!folder.exists()) {
            folder.mkdir();  // 创建文件夹
        }

        // 设置 CSV 文件路径
        String fileName = "data/sales_data.csv";  // 这里指定数据保存的位置在 data 文件夹中

        // 检查文件路径是否有效
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile(); // 如果文件不存在，则创建新文件
            }

            // 准备CSV文件写入
            try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
                // 写入CSV表头
                String[] header = {"Sales ID", "Merchant ID", "Sales Date", "Total Sales", "Total Orders", "Avg Order Value", "Region Sales", "Product ID"};
                writer.writeNext(header);

                // 写入每一行销售数据
                for (SalesData salesData : salesDataList) {
                    String salesDate = salesData.getSalesDate().toString(); // 格式化销售日期
                    BigDecimal totalSales = salesData.getTotalSales();
                    Integer totalOrders = salesData.getTotalOrders();
                    BigDecimal avgOrderValue = salesData.getAvgOrderValue();
                    String regionSales = formatRegionSales(salesData.getRegionSales());
                    Integer productId = salesData.getProductId();

                    String[] data = {
                            String.valueOf(salesData.getSalesId()),
                            String.valueOf(salesData.getMerchantId()),
                            salesDate,
                            totalSales.toString(),
                            String.valueOf(totalOrders),
                            avgOrderValue.toString(),
                            regionSales,
                            String.valueOf(productId)
                    };

                    writer.writeNext(data);
                }

                return "Sales data exported successfully to " + fileName;
            } catch (IOException e) {
                e.printStackTrace();
                return "Error writing sales data to CSV. Please check the file path and permissions.";
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "Error creating CSV file.";
        }
    }

    /**
     * 将区域销售数据格式化为字符串，适合CSV写入
     * @param regionSales 区域销售数据
     * @return 格式化后的区域销售数据字符串
     */
    private String formatRegionSales(Map<String, Integer> regionSales) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : regionSales.entrySet()) {
            sb.append(entry.getKey()).append(":").append(entry.getValue()).append(";");
        }
        return sb.toString();
    }

    @GetMapping("/read-csv")

    public List<SalesData> readSalesDataFromCsv(@RequestParam("merchantId") Integer merchantId) {
        List<SalesData> salesDataList = new ArrayList<>();
        String fileName = "data/sales_data.csv";

        // 定义日期格式化器
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            String[] line;
            boolean isHeader = true;

            while ((line = reader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false; // 跳过表头
                    continue;
                }

                // 跳过字段数量不足的行（应至少有 7 个字段）
                if (line.length < 7) {
                    System.err.println("跳过格式异常行：" + Arrays.toString(line));
                    continue;
                }

                try {
                    SalesData data = new SalesData();

                    // 解析数据字段
                    data.setSalesId(Integer.parseInt(line[0]));
                    data.setMerchantId(Integer.parseInt(line[1]));

                    // 只读取当前商家的数据
                    if (!data.getMerchantId().equals(merchantId)) {
                        continue; // 如果不是当前商家的数据，则跳过
                    }

                    // 解析日期字段，将字符串转为 LocalDate，再转为 java.sql.Date
                    LocalDate localDate = LocalDate.parse(line[2], dateFormatter);
                    data.setSalesDate(java.sql.Date.valueOf(localDate));

                    // 处理可能为空的字段，默认值为 0
                    data.setTotalSales(parseBigDecimal(line[3]));
                    data.setTotalOrders(parseInteger(line[4]));
                    data.setAvgOrderValue(parseBigDecimal(line[5]));
                    data.setRegionSales(null); // 暂时不处理第 6 列 JSON
                    data.setProductId(parseInteger(line[6])); // 原来是 line[7]

                    // 将解析的数据添加到列表
                    salesDataList.add(data);
                } catch (NumberFormatException | DateTimeException e) {
                    // 捕获异常并跳过有问题的行
                    System.err.println("跳过解析异常行：" + Arrays.toString(line));
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return salesDataList;
    }


    // 辅助方法：处理 BigDecimal 类型的空字符串
    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;  // 如果为空，返回 0
        }
        return new BigDecimal(value);
    }

    // 辅助方法：处理 Integer 类型的空字符串
    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;  // 如果为空，返回 0
        }
        return Integer.parseInt(value);
    }
    @GetMapping("/export-csv")
    public void downloadSalesDataCsv(@RequestParam("merchantId") Integer merchantId, HttpServletResponse response) throws IOException {
        List<SalesData> salesDataList = readSalesDataFromCsv(merchantId);

        // 设置响应头，返回 CSV 文件
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"sales_data_" + merchantId + ".csv\"");

        // 写入 CSV 内容到响应体
        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            String[] header = {"Sales ID", "Merchant ID", "Sales Date", "Total Sales", "Total Orders", "Avg Order Value", "Region Sales", "Product ID"};
            writer.writeNext(header);

            for (SalesData salesData : salesDataList) {
                String[] data = {
                        String.valueOf(salesData.getSalesId()),
                        String.valueOf(salesData.getMerchantId()),
                        salesData.getSalesDate().toString(),
                        salesData.getTotalSales().toString(),
                        String.valueOf(salesData.getTotalOrders()),
                        salesData.getAvgOrderValue().toString(),
                        "", // regionSales 暂不处理
                        String.valueOf(salesData.getProductId())
                };
                writer.writeNext(data);
            }

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error writing CSV to response.");
        }
    }




}
