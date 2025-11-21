package com.example.pethelper.repository;

import com.example.pethelper.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    boolean existsByUser_UserIdAndComment_CommentId(Long userId, Long commentId);

    Optional<CommentLike> findByUser_UserIdAndComment_CommentId(Long userId, Long commentId);

    int countByComment_CommentId(Long commentId);
}