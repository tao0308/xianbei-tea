package com.xianbei.tea.controller;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.Addon;
import com.xianbei.tea.service.AddonService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/addons")
public class AddonController {
    private final AddonService addonService;

    public AddonController(AddonService addonService) {
        this.addonService = addonService;
    }

    @GetMapping
    public Result<List<Addon>> list() {
        return addonService.list();
    }

    @PostMapping
    public Result<String> create(@RequestBody Addon addon) {
        return addonService.create(addon);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Addon addon) {
        return addonService.update(id, addon);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        return addonService.delete(id);
    }
}
