package com.ecms.controller;

import com.ecms.entity.User; // 导入 User 实体类
import com.ecms.service.UserService; // 导入 UserService 接口
import org.springframework.beans.factory.annotation.Autowired; // 导入 @Autowired 注释
import org.springframework.http.ResponseEntity; // 导入 ResponseEntity 类型
import org.springframework.web.bind.annotation.*; // 导入所有 Spring 的 Web 注释
import java.util.HashMap; // 导入 HashMap
import java.util.Map; // 导入 Map 接口

@RestController // 声明这是一个 REST 控制器
@RequestMapping("/api/auth") // 映射所有请求的基本路径为 /api/auth
@CrossOrigin(origins = "http://localhost:8082") // 允许来自 http://localhost:8081 的跨域请求
public class UserController {

    @Autowired // 自动注入 UserService 的实例
    private UserService userService;

    // 登录的方法
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username"); // 获取用户名
        String password = loginRequest.get("password"); // 获取密码

        try {
            // 调用服务层登录方法
            User user = userService.login(username, password);
            if (user != null) { // 登录成功
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("token", "dummy-token-" + username); // 示例 token，实际应使用JWT
                response.put("username", username); // 返回用户名
                response.put("userid",user.getUserId()); //返回id
                response.put("role", user.getRole());//返回用户角色（admin/merchant）
                return ResponseEntity.ok(response);
            } else { // 登录失败
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户名或密码错误"); // 错误消息
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) { // 捕获异常
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage()); // 返回异常消息
            return ResponseEntity.ok(response);
        }
    }

    // 注册的方法
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registerRequest) {
        String username = registerRequest.get("username"); // 获取用户名
        String password = registerRequest.get("password"); // 获取密码

        try {
            // 调用服务层注册方法
            User user = userService.register(username, password);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "注册成功"); // 成功消息
            return ResponseEntity.ok(response);
        } catch (Exception e) { // 捕获异常
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage()); // 返回异常消息
            return ResponseEntity.ok(response);
        }
    }
}