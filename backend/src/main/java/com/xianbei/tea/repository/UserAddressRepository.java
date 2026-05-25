package com.xianbei.tea.repository;

import com.xianbei.tea.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    List<UserAddress> findByUserIdOrderByIsDefaultDescCreatedAtDesc(Long userId);
    UserAddress findByUserIdAndIsDefaultTrue(Long userId);
}
