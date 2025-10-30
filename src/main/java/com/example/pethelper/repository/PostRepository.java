package com.example.pethelper.repository;

import com.example.pethelper.entity.Pet;
import com.example.pethelper.entity.Post;
import com.example.pethelper.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserUserId(Long userId);
    List<Post> findByUser(User user);

    Long countByPostDate(LocalDate date);

    Long countByUser(User user);

    Long countByPostDateAfter(LocalDate localDate);

}
