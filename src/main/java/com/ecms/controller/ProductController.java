package com.ecms.controller;

import com.ecms.entity.Product;
import com.ecms.service.MerchantService;
import com.ecms.service.ProductService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:8081") // 允许来自前端的跨域请求
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private MerchantService merchantService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // 根据商家ID获取产品
    @GetMapping("/merchant/{merchantId}")
    public List<Product> getProductsByMerchant(@PathVariable Integer merchantId) {
        return productService.getProductsByMerchantId(merchantId);
    }

    // 获取当前登录用户（商家）的产品
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentMerchantProducts(@RequestHeader(value = "Authorization", required = false) String token) {
        // 从token中提取用户ID
        // 实际应用中应使用JWT或其他token解析方法
        Integer userId = extractUserIdFromToken(token);

        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "未授权，请先登录");
            return ResponseEntity.status(401).body(response);
        }

        // 获取商家ID
        Integer merchantId = merchantService.getMerchantIdByUserId(userId);

        if (merchantId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "当前用户不是商家");
            return ResponseEntity.status(403).body(response);
        }

        // 获取商家的产品
        List<Product> products = productService.getProductsByMerchantId(merchantId);
        return ResponseEntity.ok(products);
    }

    // 根据当前登录用户（商家）和关键字搜索产品
    @GetMapping("/current/search")
    public ResponseEntity<?> searchCurrentMerchantProducts(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam String keyword) {
        // 从token中提取用户ID
        Integer userId = extractUserIdFromToken(token);

        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "未授权，请先登录");
            return ResponseEntity.status(401).body(response);
        }

        // 获取商家ID
        Integer merchantId = merchantService.getMerchantIdByUserId(userId);

        if (merchantId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "当前用户不是商家");
            return ResponseEntity.status(403).body(response);
        }

        // 搜索商家的产品
        List<Product> products = productService.searchProductsByMerchant(merchantId, keyword);
        return ResponseEntity.ok(products);
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

    // 搜索所有产品
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

    /**
     * 从token中提取用户ID
     * 在实际应用中应使用JWT等token解析方法
     */
    private Integer extractUserIdFromToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        // 这里简单处理，实际应用中应解析JWT token
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 如果使用的是localStorage中存储的用户ID，则直接解析
            if (token.matches("\\d+")) {
                return Integer.parseInt(token);
            }

            // 如果是格式类似 "dummy-token-username" 的token，则需要进一步处理
            // 在实际项目中，应从数据库查询用户ID
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
