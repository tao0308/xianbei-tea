package com.xianbei.tea.controller;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.Product;
import com.xianbei.tea.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Result<List<Product>> list(@RequestParam(required = false) Long categoryId,
                                      @RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return productService.search(keyword);
        }
        return productService.list(categoryId);
    }

    @PostMapping
    public Result<String> create(@RequestBody Product product) {
        return productService.create(product);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Product product) {
        return productService.update(id, product);
    }

    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        return productService.updateStatus(id, body.get("status"));
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        return productService.delete(id);
    }
}
