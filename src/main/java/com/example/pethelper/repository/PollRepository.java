package com.example.pethelper.repository;

import com.example.pethelper.entity.Poll;
import com.example.pethelper.entity.PollVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PollRepository extends JpaRepository<Poll, Long> {
    // Найти опрос по ID поста
    Optional<Poll> findByPostPostId(Long postId);

    @Query("SELECT COUNT(v) FROM PollVote v WHERE v.poll.id = :pollId")
    int countByPollId(@Param("pollId") Long pollId);

    @Query("SELECT v FROM PollVote v WHERE v.poll.id = :pollId AND v.user.userId = :userId")
    Optional<PollVote> findByPollIdAndUserId(@Param("pollId") Long pollId, @Param("userId") Long userId);

}
