package com.xianbei.tea.repository;

import com.xianbei.tea.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusOrderByCreatedAtDesc(String status);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items ORDER BY o.createdAt DESC")
    List<Order> findAllWithItems();

    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= CURRENT_DATE")
    long countTodayOrders();

    Optional<Order> findByOrderNo(String orderNo);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.customerName LIKE %:keyword% OR o.orderNo LIKE %:keyword% OR o.customerPhone LIKE %:keyword%")
    List<Order> search(@Param("keyword") String keyword);
}
