package com.xianbei.tea.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "combo_item")
public class ComboItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "combo_id", nullable = false)
    private Long comboId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "addon_ids", length = 500)
    private String addonIds; // JSON array like "[1,2,3]"
}
