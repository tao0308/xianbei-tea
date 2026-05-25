package com.xianbei.tea.repository;

import com.xianbei.tea.entity.ComboItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComboItemRepository extends JpaRepository<ComboItem, Long> {
    List<ComboItem> findByComboId(Long comboId);
    void deleteByComboId(Long comboId);
}
