package com.xianbei.tea.repository;

import com.xianbei.tea.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<SysUser, Long> {
    Optional<SysUser> findByUsername(String username);
    boolean existsByUsername(String username);
    long countByRole(String role);
    List<SysUser> findByRoleOrderByCreatedAtDesc(String role);
}
