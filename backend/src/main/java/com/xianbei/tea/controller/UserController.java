package com.xianbei.tea.controller;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.dto.RegisterRequest;
import com.xianbei.tea.entity.SysUser;
import com.xianbei.tea.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Result<List<SysUser>> list(@RequestParam(required = false) String role) {
        if (role != null && !role.isEmpty()) {
            return userService.listByRole(role);
        }
        return userService.list();
    }

    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public Result<String> create(@RequestBody RegisterRequest req) {
        return userService.create(req);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody RegisterRequest req) {
        return userService.update(id, req);
    }

    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        return userService.updateStatus(id, body.get("status"));
    }

    @PutMapping("/{id}/password")
    public Result<String> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return userService.resetPassword(id, body.get("password"));
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        return userService.delete(id);
    }
}
