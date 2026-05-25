package com.xianbei.tea.service;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.Addon;
import com.xianbei.tea.repository.AddonRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AddonService {
    private final AddonRepository addonRepository;

    public AddonService(AddonRepository addonRepository) {
        this.addonRepository = addonRepository;
    }

    public Result<List<Addon>> list() {
        return Result.success(addonRepository.findAll());
    }

    public Result<List<Addon>> publicList() {
        return Result.success(addonRepository.findByStatusOrderByIdAsc(1));
    }

    public Result<String> create(Addon addon) {
        addon.setId(null);
        addonRepository.save(addon);
        return Result.success("新增成功");
    }

    public Result<String> update(Long id, Addon addon) {
        var opt = addonRepository.findById(id);
        if (opt.isEmpty()) return Result.error(404, "加料选项不存在");
        Addon a = opt.get();
        a.setName(addon.getName());
        a.setPrice(addon.getPrice());
        a.setStatus(addon.getStatus());
        addonRepository.save(a);
        return Result.success("更新成功");
    }

    public Result<String> delete(Long id) {
        if (!addonRepository.existsById(id)) return Result.error(404, "加料选项不存在");
        addonRepository.deleteById(id);
        return Result.success("删除成功");
    }
}
