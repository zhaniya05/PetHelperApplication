package com.example.pethelper.repository;

import com.example.pethelper.entity.PollVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollVoteRepository extends JpaRepository<PollVote, Long> {

    // ✅ Кастомный запрос с @Query
    @Query("SELECT COUNT(v) > 0 FROM PollVote v WHERE v.poll.pollId = :pollId AND v.user.userId = :userId")
    boolean existsByPollIdAndUserId(@Param("pollId") Long pollId, @Param("userId") Long userId);

    @Query("SELECT v FROM PollVote v WHERE v.poll.pollId = :pollId AND v.user.userId = :userId")
    Optional<PollVote> findByPollIdAndUserId(@Param("pollId") Long pollId, @Param("userId") Long userId);

    @Query("SELECT COUNT(v) FROM PollVote v WHERE v.poll.pollId = :pollId")
    int countByPollId(@Param("pollId") Long pollId);

    @Query("SELECT COUNT(v) FROM PollVote v WHERE v.option.id = :optionId")
    int countByOptionId(@Param("optionId") Long optionId);

    // Простые методы без связей
    boolean existsById(Long pollId);

    List<PollVote> findAll();
}