package com.xianbei.tea.repository;

import com.xianbei.tea.entity.Addon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AddonRepository extends JpaRepository<Addon, Long> {
    List<Addon> findByStatusOrderByIdAsc(Integer status);
}
