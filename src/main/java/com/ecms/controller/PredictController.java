package com.ecms.controller;

import com.ecms.entity.ForecastFile;
import com.ecms.service.ForecastFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@CrossOrigin(origins = "http://localhost:8082")
@RestController
@RequestMapping("/api/prophet")
public class PredictController {

    @Autowired
    private ForecastFileService forecastFileService;

    @GetMapping("/forecast")
    public ResponseEntity<Map<String, Object>> getSalesForecast(@RequestParam("merchant_id") int merchantId,
                                                                @RequestParam(value = "days", defaultValue = "90") int days) {
        Map<String, Object> response = new HashMap<>();
        try {
            String currentDir = System.getProperty("user.dir");
            String pythonScriptPath = currentDir + "/Pythonscripts/Prophet.py";
            String pythonExe = "D:\\APP\\anaconda\\envs\\pyspark\\python.exe";

            // 运行 Python 脚本进行预测
            String command = pythonExe + " " + pythonScriptPath + " " + merchantId + " " + days;
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();

            // 从数据库读取文件路径
            String filePath = getFilePathFromDatabase(merchantId);
            if (filePath == null) {
                response.put("message", "未找到预测结果文件路径");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // 检查文件是否存在
            File file = new File(filePath);
            if (!file.exists()) {
                response.put("message", "文件不存在: " + filePath);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // 读取 CSV 文件内容
            List<Map<String, String>> dataList = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String headerLine = reader.readLine(); // 第一行为标题
            if (headerLine == null) {
                response.put("message", "文件内容为空");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            String[] headers = headerLine.split(",");
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    row.put(headers[i], values[i]);
                }
                dataList.add(row);
            }
            reader.close();

            // 返回预测结果数据
            response.put("filePath", filePath);
            response.put("data", dataList);
            response.put("message", "预测成功");
            return ResponseEntity.ok(response);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            response.put("message", "执行预测时发生错误: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 从数据库中获取文件路径
    private String getFilePathFromDatabase(int merchantId) {
        String filePath = null;
        // 创建数据库连接
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ecms", "root", "Guy9nzh9ng123!");
             Statement stmt = conn.createStatement()) {
            String query = "SELECT file_path FROM forecast_files WHERE merchant_id = " + merchantId;
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                filePath = rs.getString("file_path");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return filePath;
    }
}
