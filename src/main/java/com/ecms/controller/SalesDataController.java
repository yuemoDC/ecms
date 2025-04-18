package com.ecms.controller;

import com.ecms.entity.SalesData;
import com.ecms.service.SalesDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "http://localhost:8081") // 允许来自 http://localhost:8081 的请求
public class SalesDataController {

    @Autowired
    private SalesDataService salesDataService;

    // 获取商家销售数据
    @GetMapping("/merchant/{merchantId}")
    public List<SalesData> getSalesDataByMerchantId(@PathVariable Integer merchantId) {
        return salesDataService.getSalesDataByMerchantId(merchantId);
    }

    // 获取所有销售数据
    @GetMapping
    public List<SalesData> getAllSalesData() {
        return salesDataService.getAllSalesData();
    }

    // 创建销售数据
    @PostMapping
    public SalesData createSalesData(@RequestBody SalesData salesData) {
        return salesDataService.createSalesData(salesData);
    }

    // 更新销售数据
    @PutMapping("/{id}")
    public SalesData updateSalesData(@PathVariable Integer id, @RequestBody SalesData salesData) {
        return salesDataService.updateSalesData(id, salesData);
    }

    // 删除销售数据
    @DeleteMapping("/{id}")
    public void deleteSalesData(@PathVariable Integer id) {
        salesDataService.deleteSalesData(id);
    }
}
