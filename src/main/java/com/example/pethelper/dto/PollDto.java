package com.example.pethelper.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PollDto {
    private Long id;
    private String question;
    private List<String> options;
    private List<Integer> voteCounts;
    private boolean userVoted;
    private Long selectedOptionId;

    // Новые поля для хранения ID опций
    private List<Long> optionIds;

    // Методы для удобства
    public int getTotalVotes() {
        if (voteCounts == null) return 0;
        return voteCounts.stream().mapToInt(Integer::intValue).sum();
    }

    public boolean hasPoll() {
        return question != null && !question.isBlank() &&
                options != null && !options.isEmpty();
    }

    // Получить процент голосов для опции
    public double getPercentage(int optionIndex) {
        if (voteCounts == null || voteCounts.size() <= optionIndex || getTotalVotes() == 0) {
            return 0;
        }
        return (voteCounts.get(optionIndex) * 100.0) / getTotalVotes();
    }

    // Получить ID опции по индексу (для формы)
    public Long getOptionId(int index) {
        if (optionIds != null && optionIds.size() > index) {
            return optionIds.get(index);
        }
        return null;
    }
}