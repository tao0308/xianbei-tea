package com.xianbei.tea.service;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.dto.RegisterRequest;
import com.xianbei.tea.entity.SysUser;
import com.xianbei.tea.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Result<List<SysUser>> list() {
        return Result.success(userRepository.findAll());
    }

    public Result<List<SysUser>> listByRole(String role) {
        return Result.success(userRepository.findByRoleOrderByCreatedAtDesc(role));
    }

    public Result<SysUser> getById(Long id) {
        return userRepository.findById(id)
                .map(Result::success)
                .orElse(Result.error(404, "用户不存在"));
    }

    public Result<String> create(RegisterRequest req) {
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
        return Result.success("新增成功");
    }

    public Result<String> update(Long id, RegisterRequest req) {
        var opt = userRepository.findById(id);
        if (opt.isEmpty()) return Result.error(404, "用户不存在");
        SysUser user = opt.get();
        user.setRealName(req.getRealName());
        user.setPhone(req.getPhone());
        userRepository.save(user);
        return Result.success("更新成功");
    }

    public Result<String> updateStatus(Long id, Integer status) {
        var opt = userRepository.findById(id);
        if (opt.isEmpty()) return Result.error(404, "用户不存在");
        SysUser user = opt.get();
        user.setStatus(status);
        userRepository.save(user);
        return Result.success(status == 1 ? "已启用" : "已禁用");
    }

    public Result<String> resetPassword(Long id, String newPassword) {
        var opt = userRepository.findById(id);
        if (opt.isEmpty()) return Result.error(404, "用户不存在");
        SysUser user = opt.get();
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        return Result.success("密码重置成功");
    }

    public Result<String> delete(Long id) {
        if (!userRepository.existsById(id)) return Result.error(404, "用户不存在");
        userRepository.deleteById(id);
        return Result.success("删除成功");
    }
}
