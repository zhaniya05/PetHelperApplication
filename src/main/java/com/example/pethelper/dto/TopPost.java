package com.example.pethelper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopPost {
    private Long postId;
    private String postContent;
    private String userName;
    private Integer likeCount;
    private Integer commentCount;
}
