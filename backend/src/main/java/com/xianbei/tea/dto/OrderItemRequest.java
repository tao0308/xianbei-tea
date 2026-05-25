package com.xianbei.tea.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItemRequest {
    private Long productId;
    private Integer quantity;
    private String tasteNotes;
    private String addons;
    private List<Long> addonIds;
    private Boolean isCombo;
    private String productName; // 套餐名称（isCombo=true 时使用）
    private BigDecimal price; // 套餐价格（isCombo=true 时使用）
}
