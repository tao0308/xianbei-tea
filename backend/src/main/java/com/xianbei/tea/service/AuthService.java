package com.xianbei.tea.service;

import com.xianbei.tea.common.JwtUtil;
import com.xianbei.tea.common.Result;
import com.xianbei.tea.dto.LoginRequest;
import com.xianbei.tea.dto.PasswordResetRequest;
import com.xianbei.tea.dto.RegisterRequest;
import com.xianbei.tea.entity.SysUser;
import com.xianbei.tea.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public Result<Map<String, Object>> login(LoginRequest req) {
        var userOpt = userRepository.findByUsername(req.getUsername());
        if (userOpt.isEmpty()) {
            return Result.error(400, "用户名或密码错误");
        }
        SysUser user = userOpt.get();
        if (user.getStatus() == 0) {
            return Result.error(400, "账号已被禁用，请联系店长");
        }
        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            return Result.error(400, "用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return Result.success(Map.of(
            "token", token,
            "username", user.getUsername(),
            "realName", user.getRealName(),
            "role", user.getRole()
        ));
    }

    public Result<String> register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return Result.error(400, "用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(req.getUsername());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRealName(req.getRealName());
        user.setPhone(req.getPhone());
        user.setRole("STAFF");
        user.setStatus(1);
        userRepository.save(user);
        return Result.success("注册成功");
    }

    public Result<String> forgotPassword(PasswordResetRequest req) {
        var userOpt = userRepository.findByUsername(req.getUsername());
        if (userOpt.isEmpty()) {
            return Result.error(400, "用户名不存在");
        }
        SysUser user = userOpt.get();
        if (!user.getPhone().equals(req.getPhone())) {
            return Result.error(400, "手机号不匹配");
        }
        user.setPassword(encoder.encode(req.getNewPassword()));
        userRepository.save(user);
        return Result.success("密码重置成功，请用新密码登录");
    }
}
