package com.example.pethelper.service;

import com.example.pethelper.dto.CommentDto;
import java.util.List;

public interface CommentService {
    List<CommentDto> getCommentsByPost(Long postId);
    CommentDto addComment(Long postId, CommentDto dto, String username);
    void deleteComment(Long commentId, String username);
    void likeComment(Long commentId, String email);
}
