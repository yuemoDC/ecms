package com.ecms.exception; // 定义包路径

import lombok.Data; // 导入 Lombok 的 @Data 注解
import org.springframework.http.HttpStatus; // 导入 HttpStatus 类
import org.springframework.web.bind.annotation.ExceptionHandler; // 导入 ExceptionHandler 注解
import org.springframework.web.bind.annotation.ResponseStatus; // 导入 ResponseStatus 注解
import org.springframework.web.bind.annotation.RestControllerAdvice; // 导入 RestControllerAdvice 注解

@RestControllerAdvice // 处理控制器中的异常并返回 JSON 响应
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class) // 处理自定义的 BusinessException
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 设置 HTTP 状态为 400（坏请求）
    public ErrorResponse handleBusinessException(BusinessException ex) {
        return new ErrorResponse(ex.getMessage()); // 返回错误响应，包含异常消息
    }

    @ExceptionHandler(Exception.class) // 处理其他所有异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 设置 HTTP 状态为 500（内部服务器错误）
    public ErrorResponse handleException(Exception ex) {
        return new ErrorResponse("服务器内部错误"); // 返回默认的服务器错误消息
    }

    @Data // Lombok 注解，自动生成 getters 和 setters
    public static class ErrorResponse {
        private String message; // 错误信息字段

        // 构造函数，接收错误消息
        public ErrorResponse(String message) {
            this.message = message; // 设置错误消息
        }
    }
}