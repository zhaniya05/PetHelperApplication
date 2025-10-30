package com.example.pethelper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStats {
    private Long postsLastWeek;
    private Long commentsLastWeek;
    private Long newUsersLastWeek;
    private Double growthRate;
}