package com.example.pethelper.mapper;

import com.example.pethelper.dto.PostDto;
import com.example.pethelper.entity.Post;
import com.example.pethelper.entity.User;

public class PostMapper {

    public static PostDto mapToPostDto(Post post) {
        PostDto dto = new PostDto();
        dto.setPostId(post.getPostId());
        dto.setPostContent(post.getPostContent());
        dto.setPostPhotos(post.getPostPhotos());
        dto.setPostLikes(post.getPostLikes());
        dto.setPostDate(post.getPostDate());
      //  dto.setLiked(false);
        dto.setUserName(post.getUser() != null ? post.getUser().getUserName() : null);
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
        return post;
    }
}

