package com.example.pethelper.service.impl;

import com.example.pethelper.dto.PostDto;
import com.example.pethelper.dto.PostFilterRequest;
import com.example.pethelper.dto.UserDto;
import com.example.pethelper.entity.Post;
import com.example.pethelper.entity.PostLike;
import com.example.pethelper.entity.User;
import com.example.pethelper.entity.Tag;
import com.example.pethelper.entity.Visibility;
import com.example.pethelper.exception.ResourceNotFoundException;
import com.example.pethelper.mapper.PostMapper;
import com.example.pethelper.mapper.UserMapper;
import com.example.pethelper.repository.PostLikeRepository;
import com.example.pethelper.repository.PostRepository;
import com.example.pethelper.repository.UserRepository;
import com.example.pethelper.service.FollowService;
import com.example.pethelper.service.PostService;
import com.example.pethelper.service.TagService;
import com.example.pethelper.service.UserActivityService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.temporal.ChronoUnit;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.example.pethelper.entity.ActivityType;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserActivityService userActivityService;
    private final TagService tagService;

    private final FollowService followService;




    @Override
    public List<PostDto> getAllPosts(String currentUserEmail) {
        return getAllPosts(currentUserEmail, new PostFilterRequest());
    }

    @Override
    public List<PostDto> getAllPosts(String currentUserEmail, PostFilterRequest filterRequest) {
        List<Post> posts = postRepository.findAll();
        User currentUser = userRepository.findByEmail(currentUserEmail).orElse(null);

        // Применяем комбинированные фильтры и сортировку
        List<Post> filteredPosts = applyCombinedFiltersAndSorting(posts, filterRequest);

        return filteredPosts.stream().map(post -> {
            PostDto dto = PostMapper.mapToPostDto(post);
            dto.setLikeCount(post.getLikeCount());
            if (currentUser != null) {
                dto.setLikedByCurrentUser(post.isLikedByUser(currentUser));
            }
            return dto;
        }).collect(Collectors.toList());
    }

    private List<Post> applyCombinedFiltersAndSorting(List<Post> posts, PostFilterRequest filters) {
        Stream<Post> stream = posts.stream();

        // 🔍 ПОИСК ПО КЛЮЧЕВЫМ СЛОВАМ
        if (filters.getSearch() != null && !filters.getSearch().isBlank()) {
            String lowerSearch = filters.getSearch().toLowerCase();
            stream = stream.filter(post ->
                    (post.getPostContent() != null && post.getPostContent().toLowerCase().contains(lowerSearch)) ||
                            (post.getUser() != null && post.getUser().getUserName() != null &&
                                    post.getUser().getUserName().toLowerCase().contains(lowerSearch))
            );
        }

        // 📅 ФИЛЬТР ПО ДАТЕ (комбинированный)
        stream = applyDateFilters(stream, filters);

        // ❤️ ФИЛЬТР ПО ЛАЙКАМ (комбинированный)
        stream = applyLikesFilters(stream, filters);

        // 👤 ФИЛЬТР ПО АВТОРУ
        if (filters.getUserName() != null && !filters.getUserName().isBlank()) {
            String lowerUsername = filters.getUserName().toLowerCase();
            stream = stream.filter(post ->
                    post.getUser() != null && post.getUser().getUserName() != null &&
                            post.getUser().getUserName().toLowerCase().contains(lowerUsername)
            );
        }

        // 🔄 СОРТИРОВКА (приоритетная)
        stream = applyAdvancedSorting(stream, filters.getSort());

        return stream.collect(Collectors.toList());
    }

    private Stream<Post> applyDateFilters(Stream<Post> stream, PostFilterRequest filters) {
        LocalDate now = LocalDate.now();

        // Приоритет: пользовательский диапазон дат > предустановленные фильтры
        if (filters.getStartDate() != null && filters.getEndDate() != null) {
            return stream.filter(post ->
                    !post.getPostDate().isBefore(filters.getStartDate()) &&
                            !post.getPostDate().isAfter(filters.getEndDate())
            );
        }

        // Предустановленные фильтры дат
        if (filters.getDate() != null && !filters.getDate().isBlank()) {
            switch (filters.getDate().toLowerCase()) {
                case "today":
                    return stream.filter(post -> post.getPostDate().isEqual(now));
                case "week":
                    LocalDate weekAgo = now.minusWeeks(1);
                    return stream.filter(post -> !post.getPostDate().isBefore(weekAgo));
                case "month":
                    LocalDate monthAgo = now.minusMonths(1);
                    return stream.filter(post -> !post.getPostDate().isBefore(monthAgo));
                case "year":
                    LocalDate yearAgo = now.minusYears(1);
                    return stream.filter(post -> !post.getPostDate().isBefore(yearAgo));
                case "last_week":
                    LocalDate lastWeekStart = now.minusWeeks(1);
                    LocalDate lastWeekEnd = now.minusDays(1);
                    return stream.filter(post ->
                            !post.getPostDate().isBefore(lastWeekStart) &&
                                    !post.getPostDate().isAfter(lastWeekEnd)
                    );
            }
        }

        return stream;
    }

    private Stream<Post> applyLikesFilters(Stream<Post> stream, PostFilterRequest filters) {
        // Приоритет: числовой диапазон > предустановленные фильтры
        if (filters.getMinLikes() != null || filters.getMaxLikes() != null) {
            int min = filters.getMinLikes() != null ? filters.getMinLikes() : 0;
            Integer max = filters.getMaxLikes();

            if (max != null) {
                return stream.filter(post ->
                        post.getLikeCount() >= min && post.getLikeCount() <= max
                );
            } else {
                return stream.filter(post -> post.getLikeCount() >= min);
            }
        }

        // Предустановленные фильтры лайков
        if (filters.getLikes() != null && !filters.getLikes().isBlank()) {
            switch (filters.getLikes().toLowerCase()) {
                case "none":
                    return stream.filter(post -> post.getLikeCount() == 0);
                case "some":
                    return stream.filter(post -> post.getLikeCount() > 0 && post.getLikeCount() < 5);
                case "trending":
                    return stream.filter(post -> post.getLikeCount() >= 5);
                case "popular":
                    return stream.filter(post -> post.getLikeCount() >= 10);
                case "viral":
                    return stream.filter(post -> post.getLikeCount() >= 20);
                case "top_rated":
                    return stream.filter(post -> post.getLikeCount() >= 15);
            }
        }

        return stream;
    }

    private Stream<Post> applyAdvancedSorting(Stream<Post> stream, String sort) {
        if (sort == null || sort.isBlank() || sort.equalsIgnoreCase("recommended")) {
            // 🧠 Рекомендательная сортировка: лайки + свежесть
            LocalDate now = LocalDate.now();
            return stream.sorted((a, b) -> {
                long daysA = ChronoUnit.DAYS.between(a.getPostDate(), now);
                long daysB = ChronoUnit.DAYS.between(b.getPostDate(), now);

                double scoreA = a.getLikeCount() * 2 + Math.max(0, 30 - daysA);
                double scoreB = b.getLikeCount() * 2 + Math.max(0, 30 - daysB);

                return Double.compare(scoreB, scoreA); // по убыванию
            });
        }

        // остальные сортировки
        switch (sort.toLowerCase()) {
            case "likes_desc,date_desc":
                return stream.sorted((a, b) -> {
                    int likeCompare = Integer.compare(b.getLikeCount(), a.getLikeCount());
                    return likeCompare != 0 ? likeCompare : b.getPostDate().compareTo(a.getPostDate());
                });
            case "date_desc,likes_desc":
                return stream.sorted((a, b) -> {
                    int dateCompare = b.getPostDate().compareTo(a.getPostDate());
                    return dateCompare != 0 ? dateCompare : Integer.compare(b.getLikeCount(), a.getLikeCount());
                });
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
            case "user_name":
                return stream.sorted((a, b) -> {
                    String userA = a.getUser() != null && a.getUser().getUserName() != null ? a.getUser().getUserName() : "";
                    String userB = b.getUser() != null && b.getUser().getUserName() != null ? b.getUser().getUserName() : "";
                    return userA.compareToIgnoreCase(userB);
                });
            default:
                return stream.sorted((a, b) -> b.getPostDate().compareTo(a.getPostDate()));
        }
    }


    @Transactional
    @Override
    public PostDto createPost(PostDto postDto) {
        // 1️⃣ Находим пользователя
        User user = userRepository.findByUserName(postDto.getUserName())
                .orElseThrow(() -> new RuntimeException("User not found with username: " + postDto.getUserName()));

        // 2️⃣ Маппим DTO → Entity
        Post post = PostMapper.mapToPost(postDto, user);

        // 3️⃣ Обрабатываем теги, если есть
        if (postDto.getTagNames() != null && !postDto.getTagNames().isEmpty()) {
            Set<Tag> tags = tagService.findOrCreateTags(postDto.getTagNames());
            post.setTags(tags);

            // 🔥 Двусторонняя связь (чтобы Hibernate сохранил в post_tags)
            tags.forEach(tag -> tag.getPosts().add(post));
        }

        // 4️⃣ Сохраняем пост
        Post savedPost = postRepository.save(post);

        // 5️⃣ Логируем активность пользователя
        userActivityService.logActivity(
                user,
                ActivityType.POST_CREATED,
                "Created new post: " + (postDto.getPostContent().length() > 50 ?
                        postDto.getPostContent().substring(0, 50) + "..." : postDto.getPostContent()),
                "POST",
                savedPost.getPostId()
        );

        // 6️⃣ Возвращаем DTO
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
            // ✅ Находим пользователя для логирования
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // ✅ Логируем удаление поста
            userActivityService.logActivity(
                    user,
                    ActivityType.POST_DELETED,
                    "Deleted post",
                    "POST",
                    postId
            );

            postRepository.deleteById(postId);
            postRepository.flush();
        } else {
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


            userActivityService.logActivity(
                    user,
                    ActivityType.POST_UNLIKED,
                    "Unliked post",
                    "POST",
                    postId
            );

        } else {
            PostLike like = new PostLike();
            like.setPost(post);
            like.setUser(user);
            postLikeRepository.save(like);


            userActivityService.logActivity(
                    user,
                    ActivityType.POST_LIKED,
                    "Liked post",
                    "POST",
                    postId
            );
        }

        return PostMapper.mapToPostDto(post);
    }
    @Override
    public List<PostDto> getVisiblePosts(UserDto viewer, UserDto postOwner) {
        User viewer1 = UserMapper.mapToUser(viewer);
        User postOwner1 = UserMapper.mapToUser(postOwner);

        List<Post> allPosts = postRepository.findByUser(postOwner1);

        // If the viewer is the owner — show all posts
        if (viewer.getUserId().equals(postOwner.getUserId())) {
            return allPosts.stream().map(PostMapper::mapToPostDto).collect(Collectors.toList());
        }

        // If following and accepted — show all posts
        if (followService.isFollowing(viewer, postOwner)) {
            return allPosts.stream().map(PostMapper::mapToPostDto).collect(Collectors.toList());
        }

        // Otherwise, show only public posts
        return allPosts.stream()
                .filter(p -> p.getVisibility() == Visibility.PUBLIC)
                .map(PostMapper::mapToPostDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDto> getPostsByTag(String tagName, String currentUserEmail) {
        // ✅ Используем правильное имя метода
        List<Post> posts = postRepository.findByTags_NameOrderByPostDateDesc(tagName);
        // или: List<Post> posts = postRepository.findByTags_NameOrderByPostDateDesc(tagName);

        User currentUser = userRepository.findByEmail(currentUserEmail).orElse(null);

        // Конвертируем в DTO и устанавливаем информацию о лайках
        return posts.stream().map(post -> {
            PostDto dto = PostMapper.mapToPostDto(post);
            dto.setLikeCount(post.getLikeCount());
            if (currentUser != null) {
                dto.setLikedByCurrentUser(post.isLikedByUser(currentUser));
            }
            return dto;
        }).collect(Collectors.toList());
    }

}
