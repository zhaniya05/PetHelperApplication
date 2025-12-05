package com.example.pethelper.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "poll_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PollOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @ManyToOne
    @JoinColumn(name = "poll_id")
    private Poll poll;

    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PollVote> votes = new ArrayList<>();


    // Геттер для количества голосов
    public int getVoteCount() {
        return votes != null ? votes.size() : 0;
    }

    // Метод для проверки, голосовал ли пользователь за эту опцию
    public boolean isVotedByUser(Long userId) {
        if (votes == null || votes.isEmpty()) return false;

        return votes.stream()
                .anyMatch(vote -> vote.getUser() != null &&
                        vote.getUser().getUserId().equals(userId));
    }
}