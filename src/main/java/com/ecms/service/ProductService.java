package com.ecms.service; // 定义包路径

import com.ecms.entity.Product; // 导入 Product 实体
import com.ecms.exception.BusinessException; // 导入自定义异常类
import com.ecms.repository.ProductRepository; // 导入 ProductRepository 接口
import org.springframework.stereotype.Service; // 导入 Service 注解
import org.springframework.transaction.annotation.Transactional; // 导入 Transactional 注解

import java.util.List; // 导入 List 接口
import org.springframework.beans.factory.annotation.Autowired; // 导入 Autowired 注解

import org.springframework.data.jpa.domain.Specification; // 导入 Specification 接口
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // 导入 JpaSpecificationExecutor 接口

@Service // 标识这个类是一个服务层组件
public class ProductService {

    @Autowired // 自动注入 ProductRepository
    private ProductRepository productRepository;

    // 获取所有产品
    public List<Product> getAllProducts() {
        return productRepository.findAll(); // 调用 repository 方法获取所有产品
    }

    // 创建新产品
    public Product createProduct(Product product) {
        return productRepository.save(product); // 保存新产品并返回
    }

    // 更新产品
    public Product updateProduct(Integer id, Product product) {
        product.setProductId(id); // 设置产品 ID，以确保更新正确的产品
        return productRepository.save(product); // 保存更新后的产品
    }

    // 删除产品
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id); // 调用 repository 方法删除指定 ID 的产品
    }

    // 根据关键字搜索产品
    public List<Product> searchProducts(String keyword) {
        return productRepository.findAll((root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isEmpty()) {
                return criteriaBuilder.conjunction(); // 如果没有关键词，返回所有记录
            }
            // 使用关键字进行模糊查询
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("productName"), "%" + keyword + "%"), // 按产品名称模糊查询
                    criteriaBuilder.like(root.get("description"), "%" + keyword + "%") // 按描述模糊查询
            );
        });
    }
}