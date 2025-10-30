package com.example.pethelper.mapper;

import com.example.pethelper.dto.PostDto;
import com.example.pethelper.entity.Post;
import com.example.pethelper.entity.Tag;
import com.example.pethelper.entity.User;
import com.example.pethelper.entity.Visibility;

import java.util.List;
import java.util.stream.Collectors;

public class PostMapper {

    public static PostDto mapToPostDto(Post post) {
        PostDto dto = new PostDto();
        dto.setPostId(post.getPostId());
        dto.setPostContent(post.getPostContent());
        dto.setPostPhotos(post.getPostPhotos());
        dto.setPostLikes(post.getPostLikes());
        dto.setPostDate(post.getPostDate());
        dto.setUserId(post.getUser() != null ? post.getUser().getUserId() : null);
        dto.setVisibility(post.getVisibility() != null ? post.getVisibility().name() : null);
        dto.setUserName(post.getUser() != null ? post.getUser().getUserName() : null);
        dto.setLikeCount(post.getLikeCount());

        // ✅ Добавляем список тегов (только названия)
        if (post.getTags() != null) {
            dto.setTagNames(
                    post.getTags().stream()
                            .map(Tag::getName)
                            .collect(Collectors.toSet())
            );
        }
        dto.setTagNames(
                post.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toSet())
        );

        return dto;
    }

    public static Post mapToPost(PostDto dto, User user) {
        Post post = new Post();
        post.setPostId(dto.getPostId());
        post.setPostContent(dto.getPostContent());
        post.setPostPhotos(dto.getPostPhotos());
        post.setPostLikes(dto.getPostLikes());
        post.setPostDate(dto.getPostDate());
        post.setUser(user);

        if (dto.getVisibility() != null) {
            post.setVisibility(Visibility.valueOf(dto.getVisibility()));
        } else {
            post.setVisibility(Visibility.PUBLIC);
        }

        return post;
    }
}
