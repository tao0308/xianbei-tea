package com.xianbei.tea.repository;

import com.xianbei.tea.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByStockLessThanEqual(Integer threshold);
    List<Inventory> findAllByOrderByNameAsc();
    Inventory findByName(String name);
}
