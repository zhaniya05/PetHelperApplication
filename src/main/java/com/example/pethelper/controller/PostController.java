package com.example.pethelper.controller;

import com.example.pethelper.dto.FollowDto;
import com.example.pethelper.dto.PostDto;
import com.example.pethelper.dto.PostFilterRequest;
import com.example.pethelper.dto.UserDto;
import com.example.pethelper.entity.Tag;
import com.example.pethelper.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final FollowService followService;
    private final NotificationService notificationService;
    private final TagService tagService; // ✅ добавили сервис для тегов


    // ===== 📜 Отображение всех постов =====
    @GetMapping
    public String showPosts(@RequestParam(required = false) String search,
                            @RequestParam(required = false) String date,
                            @RequestParam(required = false) String likes,
                            @RequestParam(required = false) String sort,
                            @RequestParam(required = false) Integer minLikes,
                            @RequestParam(required = false) Integer maxLikes,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                            @RequestParam(required = false) String userName,
                            Model model,
                            Authentication authentication) {

        UserDto user = userService.findByEmail(authentication.getName());

        PostFilterRequest filterRequest = new PostFilterRequest();
        filterRequest.setSearch(search);
        filterRequest.setDate(date);
        filterRequest.setLikes(likes);
        filterRequest.setSort(sort);
        filterRequest.setMinLikes(minLikes);
        filterRequest.setMaxLikes(maxLikes);
        filterRequest.setStartDate(startDate);
        filterRequest.setEndDate(endDate);
        filterRequest.setUserName(userName);

        List<PostDto> posts = postService.getAllPosts(authentication.getName(), filterRequest);

        model.addAttribute("posts", posts);
        model.addAttribute("user", user);
        model.addAttribute("filterRequest", filterRequest);

        return "posts";
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Authentication authentication, Model model) {
        PostDto post = postService.getPostById(id);
        if (post == null) {
            return "redirect:/posts?error=notfound"; // если пост не найден
        }

        // Получаем текущего пользователя (если нужно для отображения профиля или кнопок)
        UserDto currentUser = userService.findByEmail(authentication.getName());
        model.addAttribute("user", currentUser);

        // Передаём пост в шаблон
        model.addAttribute("post", post);

        return "viewPost";
    }

    @GetMapping("/add")
    public String showAddPostForm(Model model, Authentication authentication) {
        UserDto user = userService.findByEmail(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("post", new PostDto());

        // ✅ Передаём все существующие теги для выбора
        Set<Tag> tags = tagService.getAllTags();
        model.addAttribute("tags", tags);


        return "add-post";
    }



    @PostMapping("/add")
    public String createPost(@ModelAttribute("post") PostDto postDto,
                             @RequestParam(value = "photos", required = false) MultipartFile[] photos,
                             @RequestParam(value = "selectedTags", required = false) List<String> selectedTags,
                             @RequestParam(value = "newTags", required = false) String newTags,
                             Authentication authentication) {

        try {
            // ✅ Получаем текущего пользователя
            UserDto currentUser = userService.findByEmail(authentication.getName());
            postDto.setUserId(currentUser.getUserId());
            postDto.setUserName(currentUser.getUserName());
            postDto.setPostDate(LocalDate.now());

            // ✅ Обработка загруженных фото
            List<String> photoUrls = new ArrayList<>();
            if (photos != null && photos.length > 0) {
                String uploadDir = "post_photos";
                Path uploadPath = Paths.get(System.getProperty("user.dir"), uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                for (MultipartFile photo : photos) {
                    if (!photo.isEmpty() && photo.getOriginalFilename() != null && !photo.getOriginalFilename().isEmpty()) {
                        String contentType = photo.getContentType();
                        if (contentType == null || !contentType.startsWith("image/")) continue;

                        String fileName = System.currentTimeMillis() + "_" +
                                photo.getOriginalFilename().replace(" ", "_");
                        Path path = uploadPath.resolve(fileName);
                        Files.copy(photo.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                        photoUrls.add("/post_photos/" + fileName);
                    }
                }
            }
            postDto.setPostPhotos(photoUrls);

            // ✅ Обработка тегов
            Set<String> tagNames = new HashSet<>();
            if (selectedTags != null) tagNames.addAll(selectedTags);

            if (newTags != null && !newTags.isBlank()) {
                for (String tag : newTags.split(",")) {
                    if (!tag.trim().isEmpty()) tagNames.add(tag.trim());
                }
            }

            postDto.setTagNames(tagNames);

            System.out.println("Selected tags: " + selectedTags);
            System.out.println("New tags: " + newTags);
            System.out.println("Final tagNames: " + postDto.getTagNames());


            // ✅ Сохраняем пост
            PostDto createdPost = postService.createPost(postDto);

            // ✅ Отправляем уведомления фолловерам
            List<FollowDto> followers = followService.getFollowers(currentUser);
            for (FollowDto follower : followers) {
                notificationService.createNotification(
                        follower.getFollowerId(),
                        currentUser.getUserName() + " posted a new update!",
                        "/posts/" + createdPost.getPostId()
                );
            }

            return "redirect:/posts";

        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/posts/add?error=io";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/posts/add?error=unknown";
        }
    }


    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id, Authentication authentication, Model model) {
        UserDto user = userService.findByEmail(authentication.getName());
        Long userId = user.getUserId();

        PostDto post = postService.getPostById(id);
        Long postUserId = post.getUserId();
        postService.deletePost(postUserId, userId, id);
        model.addAttribute("userId", userId);
        model.addAttribute("postUserId", postUserId);

        return "redirect:/posts";
    }


    @PostMapping("/{postId}/like")
    public String toggleLike(@PathVariable Long postId, Authentication authentication) {
        String email = authentication.getName();
        postService.toggleLike(postId, email);
        return "redirect:/posts";
    }
}
