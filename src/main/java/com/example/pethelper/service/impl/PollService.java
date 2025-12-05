package com.example.pethelper.service.impl;

import com.example.pethelper.entity.*;
import com.example.pethelper.repository.PollOptionRepository;
import com.example.pethelper.repository.PollRepository;
import com.example.pethelper.repository.PollVoteRepository;
import com.example.pethelper.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final PollVoteRepository pollVoteRepository;
    private final UserRepository userRepository;

    @Transactional
    public Poll createPoll(Post post, String question, List<String> options) {
        // Проверяем, не создан ли уже опрос для этого поста
        if (post.getPoll() != null) {
            throw new RuntimeException("Опрос уже существует для этого поста");
        }

        Poll poll = new Poll();
        poll.setPost(post);
        poll.setQuestion(question);
        poll.setOptions(new ArrayList<>());

        // Сохраняем опрос сначала
        Poll savedPoll = pollRepository.save(poll);

        // Создаем и сохраняем опции
        for (String opt : options) {
            PollOption option = new PollOption();
            option.setText(opt.trim());
            option.setPoll(savedPoll);
            option.setVotes(new ArrayList<>());
            pollOptionRepository.save(option);
            savedPoll.getOptions().add(option);
        }

        // Обновляем связь поста с опросом
        post.setPoll(savedPoll);

        return savedPoll;
    }

    @Transactional
    public void vote(Long pollId, Long optionId, Long userId) {
        // ✅ Используем PollVoteRepository для проверки
        if (pollVoteRepository.existsByPollIdAndUserId(pollId, userId)) {
            throw new RuntimeException("User already voted");
        }

        User user = userRepository.findById(userId).orElseThrow();
        Poll poll = pollRepository.findById(pollId).orElseThrow();
        PollOption option = pollOptionRepository.findById(optionId).orElseThrow();

        PollVote vote = new PollVote();
        vote.setPoll(poll);
        vote.setOption(option);
        vote.setUser(user);

        pollVoteRepository.save(vote);
    }



    @Transactional
    public void removeVote(Long pollId, Long userId) {
        // Находим существующий голос
        PollVote existingVote = pollVoteRepository.findByPollIdAndUserId(pollId, userId)
                .orElseThrow(() -> new RuntimeException("Голос не найден"));

        // Удаляем голос
        pollVoteRepository.delete(existingVote);
    }

    @Transactional
    public Poll getPollById(Long pollId) {
        return pollRepository.findById(pollId)
                .orElseThrow(() -> new RuntimeException("Опрос не найден с ID: " + pollId));
    }

    @Transactional
    public Poll getPollWithResults(Long pollId) {
        Poll poll = getPollById(pollId);

        // Подсчитываем голоса для каждой опции
        for (PollOption option : poll.getOptions()) {
            // votes будут загружены благодаря каскаду
            // или можно добавить @EntityGraph для eager loading
        }

        return poll;
    }

    @Transactional
    public int getVoteCount(Long pollId) {
        return pollVoteRepository.countByPollId(pollId);
    }

    @Transactional
    public boolean hasUserVoted(Long pollId, Long userId) {
        return pollVoteRepository.existsByPollIdAndUserId(pollId, userId);
    }
}