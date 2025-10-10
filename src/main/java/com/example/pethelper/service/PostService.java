package com.example.pethelper.service;

import com.example.pethelper.dto.PostDto;
import com.example.pethelper.entity.Post;

import java.util.List;

public interface PostService {

    List<PostDto> getAllPosts(String currentUserEmail);
    // List<PostDto> getAllPosts(String currentUserEmail, String search, String dateFilter, String likesFilter, String sort);
    PostDto createPost(PostDto postDto);
    PostDto getPostById(Long postId);
    void deletePost(Long postUserId, Long userId, Long postId);
    List<PostDto> getPostsByUser(Long userId);
//    PostDto saveLike(Long postId);
//    PostDto removeLike(Long postId);
    PostDto toggleLike(Long postId, String username);

}
