package com.example.pethelper.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long postId;
    private String postContent;
    private List<String> postPhotos;
    private int postLikes;
    private LocalDate postDate;
    private Long userId;
}
