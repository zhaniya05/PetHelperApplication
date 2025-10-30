package com.example.pethelper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeneralStats {
    private Long totalPosts;
    private Long totalComments;
    private Long totalPets;
    private Long totalUsers;
    private Long totalLikes;
    private Double averageLikesPerPost;
}