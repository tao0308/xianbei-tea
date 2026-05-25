package com.xianbei.tea.controller;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.Order;
import com.xianbei.tea.service.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Result<List<Order>> list(@RequestParam(required = false) String status,
                                    @RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return orderService.search(keyword);
        }
        return orderService.list(status);
    }

    @GetMapping("/{id}")
    public Result<Order> getById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return orderService.updateStatus(id, body.get("status"));
    }

    @GetMapping("/export")
    public void exportCsv(@RequestParam(required = false) String status, HttpServletResponse response) throws Exception {
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=orders.csv");
        response.setHeader("Content-Encoding", "UTF-8");
        // 写入 BOM 使 Excel 正确识别中文
        PrintWriter w = response.getWriter();
        w.write('﻿');
        w.println("订单号,顾客姓名,手机号,取餐方式,支付方式,金额,状态,下单时间,备注,送餐地址");
        List<Order> orders = orderService.listRaw(status);
        for (Order o : orders) {
            w.println(String.join(",",
                csvEscape(o.getOrderNo()),
                csvEscape(o.getCustomerName()),
                csvEscape(o.getCustomerPhone()),
                "PICKUP".equals(o.getOrderType()) ? "自取" : "外卖",
                csvEscape(o.getPaymentMethod()),
                o.getTotalPrice() != null ? o.getTotalPrice().toString() : "0",
                csvEscape(o.getStatus()),
                o.getCreatedAt() != null ? o.getCreatedAt().toString() : "",
                csvEscape(o.getRemark()),
                csvEscape(o.getAddress())
            ));
        }
        w.flush();
    }

    private String csvEscape(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
