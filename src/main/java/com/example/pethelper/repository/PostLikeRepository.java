package com.example.pethelper.repository;

import com.example.pethelper.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.pethelper.entity.Post;
import com.example.pethelper.entity.User;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, User user);
    boolean existsByPostAndUser(Post post, User user);
    int countByPost(Post post);
}
