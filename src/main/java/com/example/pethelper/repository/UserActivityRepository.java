package com.example.pethelper.repository;

import com.example.pethelper.entity.User;
import com.example.pethelper.entity.UserActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    List<UserActivity> findByUserOrderByActivityDateDesc(User user);

    Page<UserActivity> findByUserOrderByActivityDateDesc(User user, Pageable pageable);

    List<UserActivity> findByUserAndActivityDateAfterOrderByActivityDateDesc(
            User user, LocalDateTime dateAfter);

    @Query("SELECT ua FROM UserActivity ua WHERE ua.user = :user AND ua.targetEntity = :entityType ORDER BY ua.activityDate DESC")
    List<UserActivity> findByUserAndTargetEntityOrderByActivityDateDesc(
            @Param("user") User user,
            @Param("entityType") String entityType);
}