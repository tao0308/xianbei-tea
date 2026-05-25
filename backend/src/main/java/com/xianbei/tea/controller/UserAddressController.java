package com.xianbei.tea.controller;

import com.xianbei.tea.common.JwtUtil;
import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.UserAddress;
import com.xianbei.tea.repository.UserAddressRepository;
import io.jsonwebtoken.Claims;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/address")
public class UserAddressController {
    private final UserAddressRepository addressRepository;
    private final JwtUtil jwtUtil;

    public UserAddressController(UserAddressRepository addressRepository, JwtUtil jwtUtil) {
        this.addressRepository = addressRepository;
        this.jwtUtil = jwtUtil;
    }

    private Long getUserId(String authHeader) {
        return jwtUtil.parseToken(authHeader.substring(7)).get("userId", Long.class);
    }

    @GetMapping
    public Result<List<UserAddress>> list(@RequestHeader("Authorization") String header) {
        return Result.success(addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(getUserId(header)));
    }

    @PostMapping
    public Result<UserAddress> create(@RequestBody UserAddress addr, @RequestHeader("Authorization") String header) {
        Long userId = getUserId(header);
        addr.setUserId(userId);
        addr.setId(null);
        if (addr.getIsDefault()) clearOtherDefaults(userId);
        return Result.success(addressRepository.save(addr));
    }

    @PutMapping("/{id}")
    public Result<UserAddress> update(@PathVariable Long id, @RequestBody UserAddress addr,
                                       @RequestHeader("Authorization") String header) {
        Long userId = getUserId(header);
        UserAddress existing = addressRepository.findById(id).orElse(null);
        if (existing == null || !existing.getUserId().equals(userId)) return Result.error(404, "地址不存在");
        existing.setName(addr.getName());
        existing.setPhone(addr.getPhone());
        existing.setAddress(addr.getAddress());
        if (addr.getIsDefault() != null && addr.getIsDefault() && !existing.getIsDefault()) {
            clearOtherDefaults(userId);
            existing.setIsDefault(true);
        } else if (addr.getIsDefault() != null) {
            existing.setIsDefault(addr.getIsDefault());
        }
        return Result.success(addressRepository.save(existing));
    }

    @PutMapping("/{id}/default")
    public Result<Void> setDefault(@PathVariable Long id, @RequestHeader("Authorization") String header) {
        Long userId = getUserId(header);
        UserAddress addr = addressRepository.findById(id).orElse(null);
        if (addr == null || !addr.getUserId().equals(userId)) return Result.error(404, "地址不存在");
        clearOtherDefaults(userId);
        addr.setIsDefault(true);
        addressRepository.save(addr);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestHeader("Authorization") String header) {
        Long userId = getUserId(header);
        UserAddress addr = addressRepository.findById(id).orElse(null);
        if (addr == null || !addr.getUserId().equals(userId)) return Result.error(404, "地址不存在");
        addressRepository.deleteById(id);
        return Result.success(null);
    }

    private void clearOtherDefaults(Long userId) {
        UserAddress current = addressRepository.findByUserIdAndIsDefaultTrue(userId);
        if (current != null) { current.setIsDefault(false); addressRepository.save(current); }
    }
}
