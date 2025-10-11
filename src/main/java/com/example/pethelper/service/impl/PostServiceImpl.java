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

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;




    @Override
    public List<PostDto> getAllPosts(String currentUserEmail) {
        return getAllPosts(currentUserEmail, null, null, null, null);
    }

    @Override
    public List<PostDto> getAllPosts(String currentUserEmail, String search, String dateFilter, String likesFilter, String sort) {
        List<Post> posts = postRepository.findAll();
        User currentUser = userRepository.findByEmail(currentUserEmail).orElse(null);

        // Применяем фильтры и сортировку
        List<Post> filteredPosts = applyFiltersAndSorting(posts, search, dateFilter, likesFilter, sort);

        return filteredPosts.stream().map(post -> {
            PostDto dto = PostMapper.mapToPostDto(post);
            dto.setLikeCount(post.getLikeCount());
            if (currentUser != null) {
                dto.setLikedByCurrentUser(post.isLikedByUser(currentUser));
            }
            return dto;
        }).collect(Collectors.toList());
    }

    private List<Post> applyFiltersAndSorting(List<Post> posts, String search, String dateFilter, String likesFilter, String sort) {
        Stream<Post> stream = posts.stream();

        // Фильтр по поиску
        if (search != null && !search.isBlank()) {
            String lowerSearch = search.toLowerCase();
            stream = stream.filter(post ->
                    (post.getPostContent() != null && post.getPostContent().toLowerCase().contains(lowerSearch))
            );
        }

        // ФИЛЬТР ПО ДАТЕ
        if (dateFilter != null && !dateFilter.isBlank()) {
            stream = applyDateFilter(stream, dateFilter);
        }

        // ФИЛЬТР ПО ЛАЙКАМ
        if (likesFilter != null && !likesFilter.isBlank()) {
            stream = applyLikesFilter(stream, likesFilter);
        }

        // Сортировка
        if (sort != null && !sort.isBlank()) {
            stream = applySorting(stream, sort);
        }

        return stream.collect(Collectors.toList());
    }

    private Stream<Post> applyDateFilter(Stream<Post> stream, String dateFilter) {
        LocalDate now = LocalDate.now();

        switch (dateFilter.toLowerCase()) {
            case "today":
                return stream.filter(post -> post.getPostDate().isEqual(now));

            case "week":
                LocalDate weekAgo = now.minusWeeks(1);
                return stream.filter(post ->
                        !post.getPostDate().isBefore(weekAgo) && !post.getPostDate().isAfter(now)
                );

            case "month":
                LocalDate monthAgo = now.minusMonths(1);
                return stream.filter(post ->
                        !post.getPostDate().isBefore(monthAgo) && !post.getPostDate().isAfter(now)
                );

            case "year":
                LocalDate yearAgo = now.minusYears(1);
                return stream.filter(post ->
                        !post.getPostDate().isBefore(yearAgo) && !post.getPostDate().isAfter(now)
                );

            default:
                return stream;
        }
    }

    private Stream<Post> applyLikesFilter(Stream<Post> stream, String likesFilter) {
        switch (likesFilter.toLowerCase()) {
            case "popular":
                return stream.filter(post -> post.getLikeCount() >= 10);

            case "trending":
                return stream.filter(post -> post.getLikeCount() >= 5);

            case "viral":
                return stream.filter(post -> post.getLikeCount() >= 20);

            case "none":
                return stream.filter(post -> post.getLikeCount() == 0);

            case "some":
                return stream.filter(post -> post.getLikeCount() > 0 && post.getLikeCount() < 5);

            default:
                // Фильтр по числовому диапазону (например: "5-10", "10+")
                if (likesFilter.contains("-")) {
                    String[] range = likesFilter.split("-");
                    if (range.length == 2) {
                        try {
                            int min = Integer.parseInt(range[0].trim());
                            int max = Integer.parseInt(range[1].trim());
                            return stream.filter(post ->
                                    post.getLikeCount() >= min && post.getLikeCount() <= max
                            );
                        } catch (NumberFormatException e) {
                            return stream;
                        }
                    }
                } else if (likesFilter.endsWith("+")) {
                    try {
                        int min = Integer.parseInt(likesFilter.replace("+", "").trim());
                        return stream.filter(post -> post.getLikeCount() >= min);
                    } catch (NumberFormatException e) {
                        return stream;
                    }
                }
                return stream;
        }
    }

    private Stream<Post> applySorting(Stream<Post> stream, String sort) {
        switch (sort.toLowerCase()) {
            case "likes_desc":
                return stream.sorted((a, b) -> Integer.compare(b.getLikeCount(), a.getLikeCount()));

            case "likes_asc":
                return stream.sorted((a, b) -> Integer.compare(a.getLikeCount(), b.getLikeCount()));

            case "date_desc":
                return stream.sorted((a, b) -> b.getPostDate().compareTo(a.getPostDate()));

            case "date_asc":
                return stream.sorted((a, b) -> a.getPostDate().compareTo(b.getPostDate()));

            case "alphabetical":
                return stream.sorted((a, b) -> {
                    String contentA = a.getPostContent() != null ? a.getPostContent() : "";
                    String contentB = b.getPostContent() != null ? b.getPostContent() : "";
                    return contentA.compareToIgnoreCase(contentB);
                });

            default:
                return stream;
        }
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
