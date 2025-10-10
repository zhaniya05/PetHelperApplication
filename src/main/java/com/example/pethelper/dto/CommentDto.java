package com.example.pethelper.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long commentId;
    private String commentContent;
    private LocalDate commentDate;
    private Long userId;
}

