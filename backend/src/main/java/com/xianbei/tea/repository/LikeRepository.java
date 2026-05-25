package com.xianbei.tea.repository;

import com.xianbei.tea.entity.LikeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeRecord, Long> {
    Optional<LikeRecord> findByPostIdAndUserId(Long postId, Long userId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    long countByPostId(Long postId);
}
