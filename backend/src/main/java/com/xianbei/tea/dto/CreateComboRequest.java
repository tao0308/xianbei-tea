package com.xianbei.tea.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateComboRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Boolean enabled;
    private Integer sortOrder;
    private List<ComboItemRequest> items;

    @Data
    public static class ComboItemRequest {
        private Long productId;
        private String productName;
        private Integer quantity;
        private String addonIds; // JSON array string: "[1,2]"
    }
}
