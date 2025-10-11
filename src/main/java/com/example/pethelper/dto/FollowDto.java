package com.example.pethelper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowDto {
    private Long id;

    private Long followerId;
    private String followerUsername;

    private Long followingId;
    private String followingUsername;

    private String status;
    private LocalDateTime createdAt;
}


