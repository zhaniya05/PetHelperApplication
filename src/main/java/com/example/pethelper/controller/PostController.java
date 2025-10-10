package com.example.pethelper.controller;

import com.example.pethelper.dto.PostDto;
import com.example.pethelper.dto.UserDto;
import com.example.pethelper.entity.Post;
import com.example.pethelper.entity.User;
import com.example.pethelper.mapper.PostMapper;
import com.example.pethelper.repository.PostRepository;
import com.example.pethelper.repository.UserRepository;
import com.example.pethelper.service.PostService;
import com.example.pethelper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;


//    @GetMapping
//    public String getAllPosts(Model model) {
//        List<PostDto> posts = postRepository.findAll()
//                .stream()
//                .map(PostMapper::mapToPostDto)
//                .collect(Collectors.toList());
//        model.addAttribute("posts", posts);
//        return "posts";
//    }

    @GetMapping
    public String showPosts(Model model, Authentication authentication) {
        UserDto user = userService.findByEmail(authentication.getName());
        List<PostDto> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        model.addAttribute("user", user);
        return "posts";
    }

    @PostMapping("/{id}/like")
    public String likePost(@PathVariable Long id) {
        postService.saveLike(id);
        return "redirect:/posts";
    }

    @PostMapping("/{id}/unlike")
    public String unlikePost(@PathVariable Long id) {
        postService.removeLike(id);
        return "redirect:/posts";
    }

    @GetMapping("/add")
    public String showAddPostForm(Model model, Authentication authentication) {
        UserDto user = userService.findByEmail(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("post", new PostDto());
        return "add-post";
    }


    @PostMapping("/add")
    public String createPost(@ModelAttribute("post") PostDto postDto,
                             @RequestParam("photos") MultipartFile[] photos,
                             Authentication authentication) throws IOException {

        try {
            UserDto currentUser = userService.findByEmail(authentication.getName());

            postDto.setUserId(currentUser.getUserId());
            postDto.setUserName(currentUser.getUserName());
            postDto.setPostDate(LocalDate.now());

            List<String> photoUrls = new ArrayList<>();
            String uploadDir = "post_photos";

            Path uploadPath = Paths.get(System.getProperty("user.dir"), uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            System.out.println("Number of photos received: " + photos.length);

            for (MultipartFile photo : photos) {
                if (!photo.isEmpty() && photo.getOriginalFilename() != null &&
                        !photo.getOriginalFilename().isEmpty()) {

                    String contentType = photo.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        continue;
                    }

                    String fileName = System.currentTimeMillis() + "_" +
                            photo.getOriginalFilename().replace(" ", "_");
                    Path path = uploadPath.resolve(fileName);

                    Files.copy(photo.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    photoUrls.add("/post_photos/" + fileName);
                }
            }

            postDto.setPostPhotos(photoUrls);
            postService.createPost(postDto);

            return "redirect:/posts";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/posts/add?error";
        }
    }


    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }
}




//import com.example.pethelper.dto.PetDto;
//import com.example.pethelper.dto.PostDto;
//import com.example.pethelper.dto.UserDto;
//import com.example.pethelper.entity.Post;
//import com.example.pethelper.service.PostService;
//import com.example.pethelper.service.UserService;
//import com.fasterxml.jackson.annotation.JsonAnyGetter;
//import lombok.AllArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Controller
//@RequestMapping("/posts")
//@AllArgsConstructor
//public class PostController {
//
//   private final PostService postService;
//   private final UserService userService;
//
//   @GetMapping
//   public String listPosts(Model model, Authentication authentication) {
//
//       UserDto userDto = userService.findByEmail(authentication.getName());
//
//       List<PostDto> posts = postService.getAllPosts();
//       model.addAttribute("posts", posts);
//
//       return "posts";
//   }
//
//
//    @GetMapping("/add/post")
//    public String showAddPetForm() {
//        return "post-form";
//    }
//
//    @PostMapping("/add/post")
//    public String addPet(Authentication authentication,
//                         @RequestParam String title,
//                         @RequestParam String content){
//        String email = authentication.getName();
//        UserDto user = userService.findByEmail(email);
//        PetDto p = new PetDto();
//        p.setPetName(name);
//        p.setPetAge(age);
//        p.setPetBd(birthday);
//        p.setPetBreed(breed);
//        p.setPetType(type);
//        p.setUserId(user.getUserId());
//        petService.createPet(p);
//
//        return "redirect:/pets/main";
//    }
//
//}
