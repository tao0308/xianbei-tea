package com.xianbei.tea.controller;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.Comment;
import com.xianbei.tea.entity.Post;
import com.xianbei.tea.service.PostService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Result<List<Post>> list() {
        return postService.list();
    }

    @PostMapping
    public Result<String> create(@RequestBody Post post) {
        post.setCreatedBy(0L);
        return postService.create(post);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        return postService.delete(id);
    }

    @GetMapping("/{id}/comments")
    public Result<List<Comment>> comments(@PathVariable Long id) {
        return postService.comments(id);
    }

    @PostMapping("/{id}/comments")
    public Result<Comment> addComment(@PathVariable Long id, @RequestBody Comment request) {
        String content = request.getContent();
        if (content == null || content.trim().isEmpty()) {
            return Result.error(400, "评论内容不能为空");
        }
        // 管理员评论：名称统一为"管理员"，不显示头像，支持回复指定评论
        Long parentId = request.getParentId();
        return postService.addComment(id, 0L, "管理员", content, parentId);
    }
}
