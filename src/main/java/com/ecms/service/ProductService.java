package com.ecms.service;

import com.ecms.entity.Product;
import com.ecms.exception.BusinessException;
import com.ecms.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // 获取所有产品
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 根据商家ID获取产品
    public List<Product> getProductsByMerchantId(Integer merchantId) {
        return productRepository.findByMerchantId(merchantId);
    }

    // 创建新产品
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // 更新产品
    public Product updateProduct(Integer id, Product product) {
        product.setProductId(id);
        return productRepository.save(product);
    }

    // 删除产品
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    // 根据关键字搜索产品
    public List<Product> searchProducts(String keyword) {
        return productRepository.findAll((root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("productName"), "%" + keyword + "%"),
                    criteriaBuilder.like(root.get("description"), "%" + keyword + "%")
            );
        });
    }

    // 根据商家ID和关键字搜索产品
    public List<Product> searchProductsByMerchant(Integer merchantId, String keyword) {
        return productRepository.findAll((root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isEmpty()) {
                return criteriaBuilder.equal(root.get("merchantId"), merchantId);
            }
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("merchantId"), merchantId),
                    criteriaBuilder.or(
                            criteriaBuilder.like(root.get("productName"), "%" + keyword + "%"),
                            criteriaBuilder.like(root.get("description"), "%" + keyword + "%")
                    )
            );
        });
    }
}
