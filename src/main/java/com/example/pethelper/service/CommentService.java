package com.example.pethelper.service;

import com.example.pethelper.dto.CommentDto;
import java.util.List;

public interface CommentService {
    List<CommentDto> getCommentsByPost(Long postId, Long currentUserId);
    CommentDto addComment(Long postId, CommentDto dto, String email, Long currentUserId);
    void deleteComment(Long commentId, String username);
    void likeComment(Long commentId, String email);
    void unlikeComment(Long commentId, String email);
}
