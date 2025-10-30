package com.example.pethelper.repository;

import com.example.pethelper.entity.Comment;
import com.example.pethelper.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost_PostId(Long postId);

    Long countByUser(User user);

    Long countByCommentDateAfter(LocalDateTime weekAgo);
}
