package com.example.pethelper.service.impl;

import com.example.pethelper.dto.PetDto;
import com.example.pethelper.dto.PostDto;
import com.example.pethelper.entity.Pet;
import com.example.pethelper.entity.Post;
import com.example.pethelper.entity.User;
import com.example.pethelper.exception.ResourceNotFoundException;
import com.example.pethelper.mapper.PetMapper;
import com.example.pethelper.mapper.PostMapper;
import com.example.pethelper.repository.PostRepository;
import com.example.pethelper.repository.UserRepository;
import com.example.pethelper.service.PostService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;
    private UserRepository userRepository;


    @Override
    public List<PostDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();

        return posts.stream().map((post) -> PostMapper.mapToPostDto(post))
                .collect(Collectors.toList());
    }

//    @Override
//    public List<PostDto> getAllPosts() {
//        List<Post> posts = postRepository.findAll();
//
//        return posts.stream().map(post -> {
//            PostDto dto = PostMapper.mapToPostDto(post);
//            userRepository.findById(post.getUser().getUserId()).ifPresent(user -> dto.setUsername(user.getUserName()));
//            return dto;
//        }).collect(Collectors.toList());
//    }


    @Override
    public PostDto createPost(PostDto postDto) {
        User user = userRepository.findByUserName(postDto.getUserName())
                .orElseThrow(() -> new RuntimeException("User not found with username: " + postDto.getUserName()));
        Post post = PostMapper.mapToPost(postDto, user);
        Post savedPost = postRepository.save(post);
        return PostMapper.mapToPostDto(savedPost);
    }

    @Override
    public PostDto getPostById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new ResourceNotFoundException("Post does not exist with a given ID: " + postId));
        return PostMapper.mapToPostDto(post);
    }

    @Override
    public PostDto saveLike(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));
        post.setPostLikes(post.getPostLikes() + 1);
        Post updatedPost = postRepository.save(post);
        return PostMapper.mapToPostDto(updatedPost);
    }

    @Override
    public PostDto removeLike(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));
        post.setPostLikes(Math.max(0, post.getPostLikes() - 1));
        Post updatedPost = postRepository.save(post);
        return PostMapper.mapToPostDto(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
//        Post post = postRepository.findById(postId)
//                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        postRepository.deleteById(postId);
        postRepository.flush();
    }



    @Override
    public List<PostDto> getPetsByUser(Long userId) {
        return postRepository.findByUserUserId(userId)
                .stream()
                .map(PostMapper::mapToPostDto)
                .collect(Collectors.toList());
    }

}
