package com.xianbei.tea.repository;

import com.xianbei.tea.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findByEnabledTrueOrderBySortOrderAsc();
    List<Banner> findAllByOrderBySortOrderAsc();
}
