package com.xianbei.tea.controller;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.dto.LoginRequest;
import com.xianbei.tea.dto.PasswordResetRequest;
import com.xianbei.tea.dto.RegisterRequest;
import com.xianbei.tea.service.AuthService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/forgot-password")
    public Result<String> forgotPassword(@RequestBody PasswordResetRequest req) {
        return authService.forgotPassword(req);
    }

    @PostMapping("/reset-password")
    public Result<String> resetPassword(@RequestBody PasswordResetRequest req) {
        return authService.forgotPassword(req);
    }
}
