package com.xianbei.tea.controller;

import com.xianbei.tea.common.JwtUtil;
import com.xianbei.tea.common.Result;
import com.xianbei.tea.dto.CreateOrderRequest;
import com.xianbei.tea.dto.CustomerRegisterRequest;
import com.xianbei.tea.dto.LoginRequest;
import com.xianbei.tea.entity.*;
import com.xianbei.tea.repository.*;
import com.xianbei.tea.service.CustomerAuthService;
import com.xianbei.tea.service.CustomerOrderService;
import com.xianbei.tea.service.PostService;
import io.jsonwebtoken.Claims;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    private final CustomerAuthService authService;
    private final CustomerOrderService orderService;
    private final PostService postService;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AddonRepository addonRepository;
    private final JwtUtil jwtUtil;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CustomerController(CustomerAuthService authService, CustomerOrderService orderService,
                              PostService postService, CategoryRepository categoryRepository,
                              ProductRepository productRepository, AddonRepository addonRepository,
                              JwtUtil jwtUtil, CommentRepository commentRepository,
                              UserRepository userRepository) {
        this.authService = authService;
        this.orderService = orderService;
        this.postService = postService;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.addonRepository = addonRepository;
        this.jwtUtil = jwtUtil;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    // ===== 顾客认证 =====
    @PostMapping("/auth/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/auth/register")
    public Result<Map<String, Object>> register(@RequestBody CustomerRegisterRequest req) {
        return authService.register(req);
    }

    // ===== 获取用户信息 =====
    @GetMapping("/me")
    public Result<Map<String, Object>> me(@RequestHeader("Authorization") String header) {
        Claims claims = jwtUtil.parseToken(header.substring(7));
        Long userId = claims.get("userId", Long.class);
        SysUser user = userRepository.findById(userId).orElse(null);
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("username", claims.get("username"));
        data.put("realName", user != null ? user.getRealName() : claims.get("username"));
        data.put("phone", user != null ? user.getPhone() : "");
        data.put("email", user != null ? user.getEmail() : "");
        data.put("avatarUrl", user != null ? user.getAvatarUrl() : "");
        return Result.success(data);
    }

    // ===== 更新个人信息 =====
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody Map<String, String> body,
                                        @RequestHeader("Authorization") String header) {
        Claims claims = jwtUtil.parseToken(header.substring(7));
        Long userId = claims.get("userId", Long.class);
        var opt = userRepository.findById(userId);
        if (opt.isEmpty()) return Result.error(404, "用户不存在");
        SysUser user = opt.get();
        if (body.containsKey("realName")) user.setRealName(body.get("realName"));
        if (body.containsKey("phone")) user.setPhone(body.get("phone"));
        if (body.containsKey("email")) user.setEmail(body.get("email"));
        if (body.containsKey("avatarUrl")) user.setAvatarUrl(body.get("avatarUrl"));
        userRepository.save(user);
        return Result.success("更新成功");
    }

    // ===== 菜单 =====
    @GetMapping("/categories")
    public Result<List<Category>> categories() {
        return Result.success(categoryRepository.findAllByOrderBySortAsc());
    }

    @GetMapping("/products")
    public Result<List<Product>> products(@RequestParam(required = false) Long categoryId) {
        if (categoryId != null) return Result.success(productRepository.findByCategoryId(categoryId));
        return Result.success(productRepository.findAll());
    }

    @GetMapping("/addons")
    public Result<List<Addon>> addons() {
        return Result.success(addonRepository.findByStatusOrderByIdAsc(1));
    }

    // ===== 下单 =====
    @PostMapping("/orders")
    public Result<Order> createOrder(@RequestBody CreateOrderRequest req,
                                     @RequestHeader("Authorization") String header) {
        try {
            Claims claims = jwtUtil.parseToken(header.substring(7));
            Long userId = claims.get("userId", Long.class);
            return orderService.createOrder(req, userId);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "下单失败: " + e.getMessage());
        }
    }

    @GetMapping("/orders")
    public Result<List<Order>> myOrders(@RequestHeader("Authorization") String header) {
        Claims claims = jwtUtil.parseToken(header.substring(7));
        Long userId = claims.get("userId", Long.class);
        return orderService.myOrders(userId);
    }

    // ===== 活动帖子 =====
    @GetMapping("/posts")
    public Result<List<Post>> posts() {
        return postService.activePosts();
    }

    @PostMapping("/posts/{id}/like")
    public Result<Map<String, Object>> toggleLike(@PathVariable Long id,
                                                   @RequestHeader("Authorization") String header) {
        Claims claims = jwtUtil.parseToken(header.substring(7));
        return postService.toggleLike(id, claims.get("userId", Long.class));
    }

    @GetMapping("/posts/{id}/like")
    public Result<Boolean> checkLike(@PathVariable Long id,
                                      @RequestHeader("Authorization") String header) {
        Claims claims = jwtUtil.parseToken(header.substring(7));
        return postService.checkLike(id, claims.get("userId", Long.class));
    }

    @GetMapping("/posts/{id}/comments")
    public Result<List<Comment>> comments(@PathVariable Long id) {
        return postService.comments(id);
    }

    @PostMapping("/posts/{id}/comments")
    public Result<Comment> addComment(@PathVariable Long id,
                                       @RequestBody Map<String, String> body,
                                       @RequestHeader("Authorization") String header) {
        Claims claims = jwtUtil.parseToken(header.substring(7));
        Long userId = claims.get("userId", Long.class);
        // 使用真实姓名（昵称）而不是登录用户名
        String userName = userRepository.findById(userId)
                .map(u -> u.getRealName() != null && !u.getRealName().isEmpty() ? u.getRealName() : u.getUsername())
                .orElse(claims.get("username", String.class));
        String parentIdStr = body.get("parentId");
        Long parentId = parentIdStr != null && !parentIdStr.isEmpty() ? Long.parseLong(parentIdStr) : null;
        return postService.addComment(id, userId, userName, body.get("content"), parentId);
    }
}
