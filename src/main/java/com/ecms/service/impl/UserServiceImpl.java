package com.ecms.service.impl;

import com.ecms.entity.User;
import com.ecms.repository.UserRepository;
import com.ecms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service // 该注解标记这个类为一个服务组件，被Spring管理
public class UserServiceImpl implements UserService {

    @Autowired // 自动注入UserRepository依赖
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        // 返回所有用户的列表
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Integer id) {
        // 根据用户ID获取用户，如果用户不存在则返回null
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getUserByUsername(String username) {
        // 根据用户名获取用户
        return userRepository.findByUsername(username);
    }

    @Override
    public User createUser(User user) {
        // 检查用户名是否已存在
        if (getUserByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username already exists"); // 抛出异常，表示用户名已存在
        }

        // 设置用户的初始信息
        user.setCreatedAt(LocalDateTime.now()); // 设置创建时间
        user.setUpdatedAt(LocalDateTime.now()); // 设置更新时间

        // 保存用户并返回
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        // 根据用户ID查找现有用户
        User existingUser = getUserById(user.getUserId());
        if (existingUser == null) {
            throw new RuntimeException("User not found"); // 抛出异常，表示用户不存在
        }

        // 如果密码被修改，直接设置新密码
        if (!user.getPassword().equals(existingUser.getPassword())) {
            user.setPassword(user.getPassword());
        }

        user.setUpdatedAt(LocalDateTime.now()); // 更新用户的最后修改时间
        // 保存更新后的用户信息并返回
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Integer id) {
        // 根据ID删除用户
        userRepository.deleteById(id);
    }

    @Override
    public User login(String username, String password) {
        // 根据用户名获取用户进行登录验证
        User user = getUserByUsername(username);
        // 检查用户名和密码是否匹配
        if (user != null && user.getPassword().equals(password)) {
            return user; // 登录成功则返回用户
        }
        return null; // 登录失败返回null
    }

    @Override
    public User register(User user) {
        // 检查用户名是否已存在
        if (getUserByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username already exists"); // 抛出异常，表示用户名已存在
        }

        // 设置用户的创建和更新时间
        user.setCreatedAt(LocalDateTime.now()); // 设置创建时间
        user.setUpdatedAt(LocalDateTime.now()); // 设置更新时间

        // 保存新用户并返回
        return userRepository.save(user);
    }

    @Override
    public User register(String username, String password) {
        // 检查用户名是否已存在
        if (getUserByUsername(username) != null) {
            throw new RuntimeException("Username already exists"); // 抛出异常，表示用户名已存在
        }

        // 创建新用户并设置信息
        User user = new User();
        user.setUsername(username); // 设置用户名
        user.setPassword(password); // 设置密码
        user.setRole("merchant"); // 设置用户角色为商家
        user.setCreatedAt(LocalDateTime.now()); // 设置创建时间
        user.setUpdatedAt(LocalDateTime.now()); // 设置更新时间

        // 保存新用户并返回
        return userRepository.save(user);
    }
}