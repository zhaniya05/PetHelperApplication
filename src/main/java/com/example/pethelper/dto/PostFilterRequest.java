package com.example.pethelper.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostFilterRequest {
    private String search;
    private String date;
    private String likes;
    private String sort;
    private Integer minLikes;
    private Integer maxLikes;
    private LocalDate startDate;
    private LocalDate endDate;
    private String userName;
}
