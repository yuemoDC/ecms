package com.ecms.entity; // 定义包路径

import jakarta.persistence.*; // 导入 JPA 注解
import lombok.Data; // 导入 Lombok 的 @Data 注解
import java.time.LocalDateTime; // 导入 LocalDateTime 类

@Entity // 表示这个类是一个 JPA 实体，映射到数据库表
@Table(name = "users") // 指明这个实体映射到数据库中的 "users" 表
@Data // 使用 Lombok 提供的 @Data 注解自动生成 getters 和 setters
public class User {
    @Id // 表示这个字段为主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增长策略
    private Integer userId; // 用户的唯一标识符

    @Column(nullable = false, unique = true) // 此字段不能为空且必须唯一
    private String username; // 用户名

    @Column(nullable = false) // 此字段不能为空
    private String password; // 用户密码

    @Column(nullable = false) // 此字段不能为空
    private String role; // 用户角色，例如 "admin" 或 "user"

    @Column(updatable = false) // 该字段在更新时不应修改
    private LocalDateTime createdAt; // 用户创建时间

    private LocalDateTime updatedAt; // 用户最后更新时间
}