package com.xianbei.tea.service;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.Inventory;
import com.xianbei.tea.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Result<List<Inventory>> list() {
        return Result.success(inventoryRepository.findAllByOrderByNameAsc());
    }

    public Result<List<Inventory>> lowStock() {
        return Result.success(inventoryRepository.findByStockLessThanEqual(0));
    }

    public Result<String> create(Inventory item) {
        item.setId(null);
        inventoryRepository.save(item);
        return Result.success("新增成功");
    }

    public Result<String> update(Long id, Inventory item) {
        var opt = inventoryRepository.findById(id);
        if (opt.isEmpty()) return Result.error(404, "原料不存在");
        Inventory inv = opt.get();
        inv.setName(item.getName());
        inv.setStock(item.getStock());
        inv.setUnit(item.getUnit());
        inv.setLowWarning(item.getLowWarning());
        inventoryRepository.save(inv);
        return Result.success("更新成功");
    }

    public Result<String> updateStock(Long id, Integer stock) {
        var opt = inventoryRepository.findById(id);
        if (opt.isEmpty()) return Result.error(404, "原料不存在");
        Inventory inv = opt.get();
        inv.setStock(stock);
        inventoryRepository.save(inv);
        return Result.success("库存已更新");
    }

    public Result<String> delete(Long id) {
        if (!inventoryRepository.existsById(id)) return Result.error(404, "原料不存在");
        inventoryRepository.deleteById(id);
        return Result.success("删除成功");
    }

    public Result<Map<String, Object>> stats() {
        List<Inventory> all = inventoryRepository.findAll();
        long lowCount = all.stream().filter(i -> i.getStock() <= i.getLowWarning()).count();
        long totalItems = all.size();
        return Result.success(Map.of("lowCount", lowCount, "totalItems", totalItems));
    }
}
