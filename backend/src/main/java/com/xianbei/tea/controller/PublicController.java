package com.xianbei.tea.controller;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.dto.CreateOrderRequest;
import com.xianbei.tea.entity.*;
import com.xianbei.tea.repository.*;
import com.xianbei.tea.service.AddonService;
import com.xianbei.tea.service.OrderService;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
public class PublicController {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AddonService addonService;
    private final OrderService orderService;
    private final BannerRepository bannerRepository;
    private final ComboRepository comboRepository;
    private final ComboItemRepository comboItemRepository;

    public PublicController(CategoryRepository categoryRepository,
                            ProductRepository productRepository,
                            AddonService addonService,
                            OrderService orderService,
                            BannerRepository bannerRepository,
                            ComboRepository comboRepository,
                            ComboItemRepository comboItemRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.addonService = addonService;
        this.orderService = orderService;
        this.bannerRepository = bannerRepository;
        this.comboRepository = comboRepository;
        this.comboItemRepository = comboItemRepository;
    }

    @GetMapping("/categories")
    public Result<List<Category>> categories() {
        return Result.success(categoryRepository.findAllByOrderBySortAsc());
    }

    @GetMapping("/products")
    public Result<List<Product>> products(@RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            return Result.success(productRepository.findByCategoryId(categoryId));
        }
        return Result.success(productRepository.findAll());
    }

    @GetMapping("/addons")
    public Result<List<Addon>> addons() {
        return addonService.publicList();
    }

    @PostMapping("/orders")
    public Result<Order> createOrder(@RequestBody CreateOrderRequest req) {
        return orderService.createOrder(req);
    }

    @GetMapping("/banners")
    public Result<List<Banner>> banners() {
        return Result.success(bannerRepository.findByEnabledTrueOrderBySortOrderAsc());
    }

    @GetMapping("/combos")
    public Result<List<Map<String, Object>>> combos() {
        List<Combo> combos = comboRepository.findByEnabledTrueOrderBySortOrderAsc();
        List<Map<String, Object>> result = combos.stream().map(c -> {
            List<ComboItem> items = comboItemRepository.findByComboId(c.getId());
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId());
            m.put("name", c.getName());
            m.put("description", c.getDescription());
            m.put("price", c.getPrice());
            m.put("imageUrl", c.getImageUrl());
            m.put("items", items);
            return m;
        }).collect(Collectors.toList());
        return Result.success(result);
    }
}
