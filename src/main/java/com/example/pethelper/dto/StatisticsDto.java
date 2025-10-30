package com.example.pethelper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDto {
    private GeneralStats generalStats;
    private List<TopPost> topRatedPosts;
    private List<TopUser> topUsers;
    private ActivityStats activityStats;
    private List<AnimalTypeStat> typeStats;
}