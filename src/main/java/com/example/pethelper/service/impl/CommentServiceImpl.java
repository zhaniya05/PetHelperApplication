package com.example.pethelper.service.impl;


import com.example.pethelper.dto.CommentDto;
import com.example.pethelper.entity.Comment;
import com.example.pethelper.entity.Post;
import com.example.pethelper.entity.User;
import com.example.pethelper.mapper.CommentMapper;
import com.example.pethelper.repository.CommentRepository;
import com.example.pethelper.repository.PostRepository;
import com.example.pethelper.repository.UserRepository;
import com.example.pethelper.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    public List<CommentDto> getCommentsByPost(Long postId) {
        return commentRepository.findByPost_PostId(postId)
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long postId, CommentDto dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = CommentMapper.mapToComment(dto, user, post);
        commentRepository.save(comment);
        return CommentMapper.mapToCommentDto(comment);
    }

    @Override
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getEmail().equals(username)) {
            throw new RuntimeException("Access denied");
        }

        commentRepository.delete(comment);
    }
}
