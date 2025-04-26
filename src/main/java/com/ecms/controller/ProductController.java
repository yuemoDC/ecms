package com.ecms.controller;

import com.ecms.entity.Product;
import com.ecms.service.ProductService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8081") // 允许来自 http://localhost:8081 的请求
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // 根据商家ID获取产品
    @GetMapping("/merchant/{merchantId}")
    public List<Product> getProductsByMerchant(@PathVariable Integer merchantId) {
        return productService.getProductsByMerchantId(merchantId);
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Integer id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
    }

    // 新增模糊查询方法
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }

    // 根据商家ID和关键字搜索产品
    @GetMapping("/merchant/{merchantId}/search")
    public List<Product> searchProductsByMerchant(
            @PathVariable Integer merchantId,
            @RequestParam String keyword) {
        return productService.searchProductsByMerchant(merchantId, keyword);
    }
}
