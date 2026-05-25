package com.xianbei.tea.repository;

import com.xianbei.tea.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByStatusOrderByCreatedAtDesc(Integer status);
    List<Post> findAllByOrderByCreatedAtDesc();
}
