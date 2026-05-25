package com.xianbei.tea.service;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.Category;
import com.xianbei.tea.repository.CategoryRepository;
import com.xianbei.tea.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public Result<List<Category>> list() {
        return Result.success(categoryRepository.findAllByOrderBySortAsc());
    }

    public Result<String> create(Category category) {
        category.setId(null);
        categoryRepository.save(category);
        return Result.success("新增成功");
    }

    public Result<String> update(Long id, Category category) {
        var opt = categoryRepository.findById(id);
        if (opt.isEmpty()) return Result.error(404, "分类不存在");
        Category c = opt.get();
        c.setName(category.getName());
        c.setSort(category.getSort());
        c.setStatus(category.getStatus());
        categoryRepository.save(c);
        return Result.success("更新成功");
    }

    public Result<String> delete(Long id) {
        if (!categoryRepository.existsById(id)) return Result.error(404, "分类不存在");
        if (productRepository.countByCategoryId(id) > 0) {
            return Result.error(400, "该分类下还有饮品，无法删除");
        }
        categoryRepository.deleteById(id);
        return Result.success("删除成功");
    }
}
