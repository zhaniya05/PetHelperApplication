package com.example.pethelper.service.impl;


import com.example.pethelper.dto.CommentDto;
import com.example.pethelper.entity.*;
import com.example.pethelper.mapper.CommentMapper;
import com.example.pethelper.repository.CommentLikeRepository;
import com.example.pethelper.repository.CommentRepository;
import com.example.pethelper.repository.PostRepository;
import com.example.pethelper.repository.UserRepository;
import com.example.pethelper.service.CommentService;
import com.example.pethelper.service.UserActivityService;
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
    private final UserActivityService userActivityService;
    private final CommentLikeRepository commentLikeRepository;
    private final ExperienceService experienceService;

    @Override
    public List<CommentDto> getCommentsByPost(Long postId, Long currentUserId) {
        return commentRepository.findByPost_PostId(postId)
                .stream()
                .map(comment -> CommentMapper.mapToCommentDto(comment, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long postId, CommentDto dto, String email, Long currentUserId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = CommentMapper.mapToComment(dto, user, post);
        Comment savedComment = commentRepository.save(comment);

        userActivityService.logActivity(
                user,
                ActivityType.COMMENT_CREATED,
                "Added comment to post",
                "COMMENT",
                savedComment.getCommentId()
        );
        experienceService.awardExperience(user.getUserId(), "COMMENT_CREATED");

        return CommentMapper.mapToCommentDto(savedComment, currentUserId);
    }
    @Override
    public void deleteComment(Long commentId, String email) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Access denied");
        }

        // ✅ ДОБАВЬТЕ ЛОГИРОВАНИЕ УДАЛЕНИЯ КОММЕНТАРИЯ
        userActivityService.logActivity(
                comment.getUser(),
                ActivityType.COMMENT_DELETED,
                "Deleted comment",
                "COMMENT",
                commentId
        );

        commentRepository.delete(comment);
    }


    @Override
    public void likeComment(Long commentId, String email) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (commentLikeRepository.existsByUser_UserIdAndComment_CommentId(user.getUserId(), commentId)) {
            throw new RuntimeException("You have already liked this comment");
        }

        CommentLike like = new CommentLike();
        like.setUser(user);
        like.setComment(comment);
        commentLikeRepository.save(like);

        comment.setLikesCount(comment.getLikesCount() + 1);
        commentRepository.save(comment);

        userActivityService.logActivity(
                user,
                ActivityType.LIKE_ADDED,
                "Liked comment",
                "COMMENT",
                commentId
        );
    }

    public void unlikeComment(Long commentId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CommentLike like = commentLikeRepository.findByUser_UserIdAndComment_CommentId(user.getUserId(), commentId)
                .orElseThrow(() -> new RuntimeException("Like not found"));

        commentLikeRepository.delete(like);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setLikesCount(Math.max(0, comment.getLikesCount() - 1));
        commentRepository.save(comment);
    }
}
