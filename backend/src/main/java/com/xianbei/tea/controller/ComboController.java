package com.xianbei.tea.controller;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.dto.CreateComboRequest;
import com.xianbei.tea.entity.Combo;
import com.xianbei.tea.entity.ComboItem;
import com.xianbei.tea.repository.ComboItemRepository;
import com.xianbei.tea.repository.ComboRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/combos")
public class ComboController {
    private final ComboRepository comboRepository;
    private final ComboItemRepository comboItemRepository;

    public ComboController(ComboRepository comboRepository, ComboItemRepository comboItemRepository) {
        this.comboRepository = comboRepository;
        this.comboItemRepository = comboItemRepository;
    }

    /** 获取全部套餐（含明细） */
    @GetMapping
    public Result<List<Map<String, Object>>> list() {
        List<Combo> combos = comboRepository.findAllByOrderBySortOrderAsc();
        return Result.success(buildComboList(combos));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> get(@PathVariable Long id) {
        Combo combo = comboRepository.findById(id).orElse(null);
        if (combo == null) return Result.error(404, "套餐不存在");
        return Result.success(buildComboMap(combo));
    }

    @PostMapping
    @Transactional
    public Result<Map<String, Object>> create(@RequestBody CreateComboRequest req) {
        Combo combo = new Combo();
        applyComboFields(combo, req);
        combo = comboRepository.save(combo);

        saveItems(combo.getId(), req.getItems());
        return Result.success(buildComboMap(combo));
    }

    @PutMapping("/{id}")
    @Transactional
    public Result<Map<String, Object>> update(@PathVariable Long id, @RequestBody CreateComboRequest req) {
        Combo combo = comboRepository.findById(id).orElse(null);
        if (combo == null) return Result.error(404, "套餐不存在");
        applyComboFields(combo, req);
        combo = comboRepository.save(combo);

        comboItemRepository.deleteByComboId(id);
        saveItems(id, req.getItems());
        return Result.success(buildComboMap(combo));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> delete(@PathVariable Long id) {
        comboItemRepository.deleteByComboId(id);
        comboRepository.deleteById(id);
        return Result.success(null);
    }

    // ===== 内部方法 =====

    private void applyComboFields(Combo combo, CreateComboRequest req) {
        combo.setName(req.getName());
        combo.setDescription(req.getDescription());
        combo.setPrice(req.getPrice());
        combo.setImageUrl(req.getImageUrl());
        combo.setEnabled(req.getEnabled() != null ? req.getEnabled() : true);
        combo.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0);
    }

    private void saveItems(Long comboId, List<CreateComboRequest.ComboItemRequest> items) {
        if (items == null) return;
        for (CreateComboRequest.ComboItemRequest item : items) {
            ComboItem ci = new ComboItem();
            ci.setComboId(comboId);
            ci.setProductId(item.getProductId());
            ci.setProductName(item.getProductName());
            ci.setQuantity(item.getQuantity() != null ? item.getQuantity() : 1);
            ci.setAddonIds(item.getAddonIds());
            comboItemRepository.save(ci);
        }
    }

    private List<Map<String, Object>> buildComboList(List<Combo> combos) {
        return combos.stream().map(this::buildComboMap).collect(Collectors.toList());
    }

    private Map<String, Object> buildComboMap(Combo combo) {
        List<ComboItem> items = comboItemRepository.findByComboId(combo.getId());
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", combo.getId());
        map.put("name", combo.getName());
        map.put("description", combo.getDescription());
        map.put("price", combo.getPrice());
        map.put("imageUrl", combo.getImageUrl());
        map.put("enabled", combo.getEnabled());
        map.put("sortOrder", combo.getSortOrder());
        map.put("createdAt", combo.getCreatedAt());
        map.put("items", items);
        return map;
    }
}
