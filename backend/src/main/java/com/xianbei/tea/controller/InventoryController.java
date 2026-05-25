package com.xianbei.tea.controller;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.Inventory;
import com.xianbei.tea.service.InventoryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public Result<List<Inventory>> list() {
        return inventoryService.list();
    }

    @GetMapping("/low-stock")
    public Result<List<Inventory>> lowStock() {
        return inventoryService.lowStock();
    }

    @PostMapping
    public Result<String> create(@RequestBody Inventory item) {
        return inventoryService.create(item);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Inventory item) {
        return inventoryService.update(id, item);
    }

    @PutMapping("/{id}/stock")
    public Result<String> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        return inventoryService.updateStock(id, body.get("stock"));
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        return inventoryService.delete(id);
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        return inventoryService.stats();
    }
}
