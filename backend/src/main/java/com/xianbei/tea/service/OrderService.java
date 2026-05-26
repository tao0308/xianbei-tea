package com.xianbei.tea.service;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.dto.CreateOrderRequest;
import com.xianbei.tea.dto.OrderItemRequest;
import com.xianbei.tea.entity.*;
import com.xianbei.tea.repository.*;
import com.xianbei.tea.service.OrderEventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AddonRepository addonRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderEventService orderEventService;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                        AddonRepository addonRepository,
                        InventoryRepository inventoryRepository,
                        OrderEventService orderEventService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.addonRepository = addonRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderEventService = orderEventService;
    }

    public Result<List<Order>> list(String status) {
        List<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderRepository.findByStatusOrderByCreatedAtDesc(status);
            orders.forEach(o -> o.getItems().size());
        } else {
            orders = orderRepository.findAllWithItems();
        }
        return Result.success(orders);
    }

    public List<Order> listRaw(String status) {
        if (status != null && !status.isEmpty()) {
            return orderRepository.findByStatusOrderByCreatedAtDesc(status);
        }
        return orderRepository.findAllWithItems();
    }

    public Result<List<Order>> search(String keyword) {
        List<Order> orders = orderRepository.search(keyword);
        orders.forEach(o -> o.getItems().size());
        return Result.success(orders);
    }

    public Result<Order> getById(Long id) {
        return orderRepository.findById(id)
                .map(o -> { o.getItems().size(); return Result.success(o); })
                .orElse(Result.error(404, "订单不存在"));
    }

    @Transactional
    public Result<String> updateStatus(Long id, String status) {
        var opt = orderRepository.findById(id);
        if (opt.isEmpty()) return Result.error(404, "订单不存在");
        Order order = opt.get();
        order.setStatus(status);
        orderRepository.save(order);
        orderEventService.notifyNewOrder(order.getOrderNo());
        String statusText = switch (status) {
            case "MAKING" -> "开始制作";
            case "DONE" -> "制作完成";
            case "PICKED_UP" -> "已取餐";
            default -> "状态更新";
        };
        return Result.success(statusText);
    }

    @Transactional
    public Result<Order> createOrder(CreateOrderRequest req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            return Result.error(400, "请至少选择一件饮品");
        }

        Order order = new Order();
        order.setCustomerName(req.getCustomerName());
        order.setCustomerPhone(req.getCustomerPhone());
        order.setRemark(req.getRemark());
        order.setOrderType(req.getOrderType() != null ? req.getOrderType() : "PICKUP");
        order.setAddress(req.getAddress());
        order.setPaymentMethod(req.getPaymentMethod());
        order.setDeliveryFee(req.getDeliveryFee() != null ? req.getDeliveryFee() : BigDecimal.ZERO);
        order.setStatus("PENDING");

        String lastNo = orderRepository.findLastOrderNo();
        int nextNum = 1;
        if (lastNo != null) {
            try { nextNum = Integer.parseInt(lastNo.substring(1)) + 1; } catch (Exception ignored) {}
        }
        while (orderRepository.findByOrderNo("A" + String.format("%03d", nextNum)).isPresent()) {
            nextNum++;
        }
        order.setOrderNo("A" + String.format("%03d", nextNum));

        List<OrderItem> items = req.getItems().stream().map(itemReq -> {
            Boolean isCombo = itemReq.getIsCombo() != null && itemReq.getIsCombo();
            OrderItem item = new OrderItem();
            item.setOrder(order);
            BigDecimal unitPrice;
            if (isCombo) {
                item.setProductId(0L);
                item.setProductName(itemReq.getProductName() != null ? itemReq.getProductName() : "套餐");
                unitPrice = itemReq.getPrice() != null ? itemReq.getPrice() : BigDecimal.ZERO;
            } else {
                Product product = productRepository.findById(itemReq.getProductId())
                        .orElseThrow(() -> new RuntimeException("饮品不存在: " + itemReq.getProductId()));
                item.setProductId(product.getId());
                item.setProductName(product.getName());
                unitPrice = product.getPrice();
            }
            // 加料价格加到单价上
            if (itemReq.getAddonIds() != null) {
                for (Long aid : itemReq.getAddonIds()) {
                    Addon a = addonRepository.findById(aid).orElse(null);
                    if (a != null) unitPrice = unitPrice.add(a.getPrice());
                }
            }
            item.setPrice(unitPrice);
            item.setQuantity(itemReq.getQuantity());
            item.setTasteNotes(itemReq.getTasteNotes());
            item.setAddons(itemReq.getAddons());
            return item;
        }).collect(Collectors.toList());

        BigDecimal itemsTotal = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(itemsTotal.add(order.getDeliveryFee()));
        order.setItems(items);
        orderRepository.save(order);
        orderEventService.notifyNewOrder(order.getOrderNo());

        // 库存扣减
        deductInventoryForAddons(req.getItems());

        return Result.success(order);
    }

    private void deductInventoryForAddons(List<OrderItemRequest> items) {
        for (var itemReq : items) {
            if (itemReq.getAddonIds() == null || itemReq.getAddonIds().isEmpty()) continue;
            int qty = itemReq.getQuantity() != null ? itemReq.getQuantity() : 1;
            for (Long addonId : itemReq.getAddonIds()) {
                Addon addon = addonRepository.findById(addonId).orElse(null);
                if (addon == null) continue;
                Inventory inv = inventoryRepository.findByName(addon.getName());
                if (inv != null) {
                    inv.setStock(Math.max(0, inv.getStock() - qty));
                    inventoryRepository.save(inv);
                }
            }
        }
    }
}
