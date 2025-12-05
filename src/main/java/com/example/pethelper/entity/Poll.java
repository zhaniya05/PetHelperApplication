package com.example.pethelper.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polls")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pollId;

    private String question;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id ASC") // для сохранения порядка опций
    private List<PollOption> options = new ArrayList<>();

    // Вспомогательный метод для проверки, голосовал ли пользователь
    public boolean hasUserVoted(Long userId) {
        if (options == null || options.isEmpty()) return false;

        return options.stream()
                .anyMatch(option -> option.isVotedByUser(userId));
    }

    // Метод для получения выбранной пользователем опции
    public PollOption getUserVote(Long userId) {
        if (options == null || options.isEmpty()) return null;

        return options.stream()
                .filter(option -> option.isVotedByUser(userId))
                .findFirst()
                .orElse(null);
    }

    // Метод для получения общего количества голосов
    public int getTotalVotes() {
        if (options == null || options.isEmpty()) return 0;

        return options.stream()
                .mapToInt(PollOption::getVoteCount)
                .sum();
    }
}
