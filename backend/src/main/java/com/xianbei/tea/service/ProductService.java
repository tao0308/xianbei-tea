package com.xianbei.tea.service;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.Product;
import com.xianbei.tea.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Result<List<Product>> list(Long categoryId) {
        if (categoryId != null) {
            return Result.success(productRepository.findByCategoryId(categoryId));
        }
        return Result.success(productRepository.findAll());
    }

    public Result<List<Product>> search(String keyword) {
        return Result.success(productRepository.search(keyword));
    }

    public Result<String> create(Product product) {
        product.setId(null);
        productRepository.save(product);
        return Result.success("新增成功");
    }

    public Result<String> update(Long id, Product product) {
        var opt = productRepository.findById(id);
        if (opt.isEmpty()) return Result.error(404, "饮品不存在");
        Product p = opt.get();
        p.setName(product.getName());
        p.setCategoryId(product.getCategoryId());
        p.setPrice(product.getPrice());
        p.setDescription(product.getDescription());
        p.setImageUrl(product.getImageUrl());
        productRepository.save(p);
        return Result.success("更新成功");
    }

    public Result<String> updateStatus(Long id, Integer status) {
        var opt = productRepository.findById(id);
        if (opt.isEmpty()) return Result.error(404, "饮品不存在");
        Product p = opt.get();
        p.setStatus(status);
        productRepository.save(p);
        return Result.success(status == 1 ? "已上架" : "已下架");
    }

    public Result<String> delete(Long id) {
        if (!productRepository.existsById(id)) return Result.error(404, "饮品不存在");
        productRepository.deleteById(id);
        return Result.success("删除成功");
    }
}
