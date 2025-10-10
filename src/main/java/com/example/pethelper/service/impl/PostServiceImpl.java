package com.example.pethelper.service.impl;

import com.example.pethelper.dto.PostDto;
import com.example.pethelper.entity.Post;
import com.example.pethelper.entity.PostLike;
import com.example.pethelper.entity.User;
import com.example.pethelper.exception.ResourceNotFoundException;
import com.example.pethelper.mapper.PostMapper;
import com.example.pethelper.repository.PostLikeRepository;
import com.example.pethelper.repository.PostRepository;
import com.example.pethelper.repository.UserRepository;
import com.example.pethelper.service.PostService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;


    @Override
    public List<PostDto> getAllPosts(String currentUserEmail) {
        List<Post> posts = postRepository.findAll();
        User currentUser = userRepository.findByEmail(currentUserEmail).orElse(null);

        return posts.stream().map(post -> {
            PostDto dto = PostMapper.mapToPostDto(post);
            dto.setLikeCount(post.getLikeCount());
            if (currentUser != null) {
                dto.setLikedByCurrentUser(post.isLikedByUser(currentUser));
            }
            return dto;
        }).collect(Collectors.toList());
    }



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
    @Transactional
    public void deletePost(Long postUserId, Long userId, Long postId) {

        if (Objects.equals(postUserId, userId)) {
            postRepository.deleteById(postId);
            postRepository.flush();
        }
        else {
            throw new RuntimeException("Access denied " + postUserId + " " + userId);
        }
    }



    @Override
    public List<PostDto> getPostsByUser(Long userId) {
        return postRepository.findByUserUserId(userId)
                .stream()
                .map(PostMapper::mapToPostDto)
                .collect(Collectors.toList());
    }


    @Override
    public PostDto toggleLike(Long postId, String email) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<PostLike> existingLike = postLikeRepository.findByPostAndUser(post, user);

        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
        } else {
            PostLike like = new PostLike();
            like.setPost(post);
            like.setUser(user);
            postLikeRepository.save(like);
        }

        return PostMapper.mapToPostDto(post);
    }

}
