package com.example.pethelper.service;

import com.example.pethelper.dto.PostDto;

import java.util.List;

public interface PostService {

    List<PostDto> getAllPosts();
    PostDto createPost(PostDto postDto);
    PostDto getPostById(Long postId);
    void deletePost(Long postId);
    List<PostDto> getPetsByUser(Long userId);
    PostDto saveLike(Long postId);
    PostDto removeLike(Long postId);

}
