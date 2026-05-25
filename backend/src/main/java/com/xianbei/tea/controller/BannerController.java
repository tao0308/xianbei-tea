package com.xianbei.tea.controller;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.Banner;
import com.xianbei.tea.repository.BannerRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/banners")
public class BannerController {
    private final BannerRepository bannerRepository;

    public BannerController(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    @GetMapping
    public Result<List<Banner>> list() {
        return Result.success(bannerRepository.findAllByOrderBySortOrderAsc());
    }

    @PostMapping
    public Result<Banner> create(@RequestBody Banner banner) {
        return Result.success(bannerRepository.save(banner));
    }

    @PutMapping("/{id}")
    public Result<Banner> update(@PathVariable Long id, @RequestBody Banner banner) {
        Banner existing = bannerRepository.findById(id).orElse(null);
        if (existing == null) return Result.error(404, "轮播图不存在");
        existing.setTitle(banner.getTitle());
        existing.setImageUrl(banner.getImageUrl());
        existing.setLinkUrl(banner.getLinkUrl());
        existing.setSortOrder(banner.getSortOrder());
        existing.setEnabled(banner.getEnabled());
        return Result.success(bannerRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bannerRepository.deleteById(id);
        return Result.success(null);
    }
}
