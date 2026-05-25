package com.xianbei.tea.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {
    private String customerName;
    private String customerPhone;
    private String remark;
    private String orderType;
    private String address;
    private String paymentMethod;
    private BigDecimal deliveryFee;
    private List<OrderItemRequest> items;
}
