package com.example.pethelper.mapper;

import com.example.pethelper.dto.CommentDto;
import com.example.pethelper.entity.Comment;
import com.example.pethelper.entity.Post;
import com.example.pethelper.entity.User;

public class CommentMapper {

    public static CommentDto mapToCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setCommentId(comment.getCommentId());
        dto.setCommentContent(comment.getCommentContent());
        dto.setCommentDate(comment.getCommentDate());
        dto.setUserId(comment.getUser().getUserId());
        dto.setUserName(comment.getUser().getUserName());
        dto.setPostId(comment.getPost().getPostId());
        dto.setLikesCount(comment.getLikesCount());

        return dto;
    }

    public static Comment mapToComment(CommentDto dto, User user, Post post) {
        Comment comment = new Comment();
        comment.setCommentContent(dto.getCommentContent());
        comment.setUser(user);
        comment.setPost(post);
        return comment;
    }
}
