package com.example.pethelper.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
    private String userName;
    private Long userId;
    private int likeCount;
    private boolean likedByCurrentUser;
    private String visibility;
    private Set<String> tagNames;
    private Set<Long> tagIds;

}
