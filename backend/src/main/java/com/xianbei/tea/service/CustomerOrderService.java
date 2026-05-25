package com.xianbei.tea.service;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.dto.CreateOrderRequest;
import com.xianbei.tea.dto.OrderItemRequest;
import com.xianbei.tea.entity.*;
import com.xianbei.tea.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerOrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderEventService orderEventService;
    private final AddonRepository addonRepository;
    private final InventoryRepository inventoryRepository;

    public CustomerOrderService(OrderRepository orderRepository, ProductRepository productRepository,
                                OrderEventService orderEventService,
                                AddonRepository addonRepository,
                                InventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderEventService = orderEventService;
        this.addonRepository = addonRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public Result<Order> createOrder(CreateOrderRequest req, Long customerId) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            return Result.error(400, "请至少选择一件饮品");
        }
        Order order = new Order();
        order.setCustomerName(req.getCustomerName());
        order.setCustomerPhone(req.getCustomerPhone());
        order.setCustomerId(customerId);
        order.setOrderType(req.getOrderType() != null ? req.getOrderType() : "PICKUP");
        order.setAddress(req.getAddress());
        order.setRemark(req.getRemark());
        order.setPaymentMethod(req.getPaymentMethod());
        order.setDeliveryFee(req.getDeliveryFee() != null ? req.getDeliveryFee() : BigDecimal.ZERO);
        order.setStatus("PENDING");

        String lastNo = orderRepository.findLastOrderNo();
        int nextNum = 1;
        if (lastNo != null) {
            try { nextNum = Integer.parseInt(lastNo.substring(1)) + 1; } catch (Exception ignored) {}
        }
        // 确保不重复
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
                        .orElseThrow(() -> new RuntimeException("饮品不存在"));
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

        // ===== 库存扣减：根据加料名称匹配原料 =====
        deductInventoryForAddons(req.getItems());

        orderEventService.notifyNewOrder(order.getOrderNo());
        return Result.success(order);
    }

    /** 根据订单中的加料 ID 列表扣减对应库存 */
    private void deductInventoryForAddons(List<OrderItemRequest> items) {
        for (var itemReq : items) {
            if (itemReq.getAddonIds() == null || itemReq.getAddonIds().isEmpty()) continue;
            int qty = itemReq.getQuantity() != null ? itemReq.getQuantity() : 1;
            for (Long addonId : itemReq.getAddonIds()) {
                Addon addon = addonRepository.findById(addonId).orElse(null);
                if (addon == null) continue;
                // 按加料名称匹配库存原料
                Inventory inv = inventoryRepository.findByName(addon.getName());
                if (inv != null) {
                    inv.setStock(inv.getStock() - qty);
                    if (inv.getStock() < 0) inv.setStock(0);
                    inventoryRepository.save(inv);
                }
            }
        }
    }

    public Result<List<Order>> myOrders(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        orders.forEach(o -> o.getItems().size());
        return Result.success(orders);
    }
}
