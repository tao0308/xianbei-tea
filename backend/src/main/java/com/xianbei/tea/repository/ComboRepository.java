package com.xianbei.tea.repository;

import com.xianbei.tea.entity.Combo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComboRepository extends JpaRepository<Combo, Long> {
    List<Combo> findByEnabledTrueOrderBySortOrderAsc();
    List<Combo> findAllByOrderBySortOrderAsc();
}
