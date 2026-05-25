package com.xianbei.tea.service;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.Comment;
import com.xianbei.tea.entity.LikeRecord;
import com.xianbei.tea.entity.Post;
import com.xianbei.tea.entity.SysUser;
import com.xianbei.tea.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, CommentRepository commentRepository,
                       LikeRepository likeRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
    }

    public Result<List<Post>> list() {
        return Result.success(postRepository.findAllByOrderByCreatedAtDesc());
    }

    public Result<List<Post>> activePosts() {
        return Result.success(postRepository.findByStatusOrderByCreatedAtDesc(1));
    }

    public Result<String> create(Post post) {
        post.setId(null);
        postRepository.save(post);
        return Result.success("发布成功");
    }

    @Transactional
    public Result<Map<String, Object>> toggleLike(Long postId, Long userId) {
        var existing = likeRepository.findByPostIdAndUserId(postId, userId);
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) return Result.error(404, "帖子不存在");

        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            post.setLikeCount(post.getLikeCount() - 1);
            postRepository.save(post);
            return Result.success(Map.of("liked", false, "count", post.getLikeCount()));
        } else {
            LikeRecord like = new LikeRecord();
            like.setPostId(postId);
            like.setUserId(userId);
            likeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);
            return Result.success(Map.of("liked", true, "count", post.getLikeCount()));
        }
    }

    public Result<Boolean> checkLike(Long postId, Long userId) {
        return Result.success(likeRepository.existsByPostIdAndUserId(postId, userId));
    }

    public Result<List<Comment>> comments(Long postId) {
        return Result.success(commentRepository.findByPostIdOrderByCreatedAtAsc(postId));
    }

    @Transactional
    public Result<Comment> addComment(Long postId, Long userId, String userName, String content, Long parentId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) return Result.error(404, "帖子不存在");

        String avatarUrl = userId > 0 ? userRepository.findById(userId).map(SysUser::getAvatarUrl).orElse("") : "";

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setUserName(userName);
        comment.setAvatarUrl(avatarUrl);
        comment.setContent(content);
        comment.setParentId(parentId);
        commentRepository.save(comment);

        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
        return Result.success(comment);
    }

    // 保持旧方法兼容
    public Result<Comment> addComment(Long postId, Long userId, String userName, String content) {
        return addComment(postId, userId, userName, content, null);
    }

    public Result<String> delete(Long id) {
        if (!postRepository.existsById(id)) return Result.error(404, "帖子不存在");
        postRepository.deleteById(id);
        return Result.success("已删除");
    }
}
