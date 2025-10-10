package com.example.pethelper.mapper;

import com.example.pethelper.dto.CommentDto;
import com.example.pethelper.entity.Comment;
import com.example.pethelper.entity.User;

public class CommentMapper {

    public static CommentDto mapToCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setCommentId(comment.getCommentId());
        dto.setCommentContent(comment.getCommentContent());
        dto.setCommentDate(comment.getCommentDate());
        dto.setUserId(comment.getUser() != null ? comment.getUser().getUserId() : null);
        return dto;
    }

    public static Comment mapToComment(CommentDto dto, User user) {
        Comment comment = new Comment();
        comment.setCommentId(dto.getCommentId());
        comment.setCommentContent(dto.getCommentContent());
        comment.setCommentDate(dto.getCommentDate());
        comment.setUser(user);
        return comment;
    }
}
