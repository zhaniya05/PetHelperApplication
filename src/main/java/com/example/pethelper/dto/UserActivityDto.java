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
public class UserActivityDto {
    private Long id;
    private String activityType;
    private String description;
    private String targetEntity;
    private Long targetId;
    private LocalDateTime activityDate;
    private String formattedDate;
}