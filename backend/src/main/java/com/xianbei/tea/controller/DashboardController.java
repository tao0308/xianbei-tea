package com.xianbei.tea.controller;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.Order;
import com.xianbei.tea.entity.Product;
import com.xianbei.tea.repository.OrderRepository;
import com.xianbei.tea.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public DashboardController(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        List<Order> allOrders = orderRepository.findAllWithItems();
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();

        // 今日订单
        List<Order> todayOrders = allOrders.stream()
                .filter(o -> o.getCreatedAt() != null && o.getCreatedAt().toLocalDate().equals(today))
                .collect(Collectors.toList());

        long todayCount = todayOrders.size();
        BigDecimal todayRevenue = todayOrders.stream()
                .map(Order::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 总订单数
        long totalOrders = allOrders.size();

        // 热门饮品排行
        Map<String, Integer> productSales = new HashMap<>();
        for (Order order : allOrders) {
            if (order.getItems() != null) {
                order.getItems().forEach(item -> {
                    String name = item.getProductName();
                    productSales.merge(name, item.getQuantity(), Integer::sum);
                });
            }
        }

        List<Map<String, Object>> topProducts = productSales.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("name", e.getKey());
                    m.put("count", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());

        // 最近7天销售额
        List<Map<String, Object>> weeklySales = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();
            BigDecimal dayRevenue = allOrders.stream()
                    .filter(o -> o.getCreatedAt() != null && !o.getCreatedAt().isBefore(start) && o.getCreatedAt().isBefore(end))
                    .map(Order::getTotalPrice)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Map<String, Object> m = new HashMap<>();
            m.put("date", date.toString());
            m.put("revenue", dayRevenue);
            weeklySales.add(m);
        }

        // 各状态订单数
        long pendingOrders = allOrders.stream().filter(o -> "PENDING".equals(o.getStatus())).count();
        long makingOrders = allOrders.stream().filter(o -> "MAKING".equals(o.getStatus())).count();

        Map<String, Object> data = new HashMap<>();
        data.put("todayCount", todayCount);
        data.put("todayRevenue", todayRevenue);
        data.put("totalOrders", totalOrders);
        data.put("pendingOrders", pendingOrders);
        data.put("makingOrders", makingOrders);
        data.put("topProducts", topProducts);
        data.put("weeklySales", weeklySales);
        return Result.success(data);
    }

    @GetMapping("/recent-orders")
    public Result<List<Order>> recentOrders(@RequestParam(defaultValue = "3") int minutes) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(minutes);
        List<Order> recent = orderRepository.findAll().stream()
                .filter(o -> o.getCreatedAt() != null && o.getCreatedAt().isAfter(since) && "PENDING".equals(o.getStatus()))
                .peek(o -> o.getItems().size())
                .collect(Collectors.toList());
        return Result.success(recent);
    }
}
