package com.example.pethelper.repository;

import com.example.pethelper.entity.Follow;
import com.example.pethelper.entity.FollowStatus;
import com.example.pethelper.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
    List<Follow> findByFollowingAndStatus(User following, FollowStatus status);
    List<Follow> findByFollowerAndStatus(User follower, FollowStatus status);
}

