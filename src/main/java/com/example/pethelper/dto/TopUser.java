package com.example.pethelper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopUser {
    private Long userId;
    private String userName;
    private Long postCount;
    private Long totalLikes;
    private Long commentCount;
}
