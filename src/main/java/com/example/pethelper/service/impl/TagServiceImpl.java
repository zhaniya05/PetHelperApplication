package com.example.pethelper.service.impl;

import com.example.pethelper.dto.PostDto;
import com.example.pethelper.dto.UserDto;
import com.example.pethelper.entity.Post;
import com.example.pethelper.entity.Tag;
import com.example.pethelper.entity.User;
import com.example.pethelper.mapper.PostMapper;
import com.example.pethelper.mapper.UserMapper;
import com.example.pethelper.repository.TagRepository;
import com.example.pethelper.repository.UserRepository;
import com.example.pethelper.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Override
    public Tag findOrCreateTag(String tagName) {
        return tagRepository.findByNameIgnoreCase(tagName.trim())
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(tagName.trim());
                    return tagRepository.save(newTag);
                });
    }

    @Override
    public Set<Tag> findOrCreateTags(Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        if (tagNames == null) return tags;

        for (String name : tagNames) {
            if (name != null && !name.trim().isEmpty()) {
                tags.add(findOrCreateTag(name));
            }
        }
        return tags;
    }

    @Override
    public Set<Tag> getAllTags() {
        return new HashSet<>(tagRepository.findAll());
    }

//    @Override
//    @Transactional
//    public void followTag(UserDto user, String name) {
//        Tag tag = tagRepository.findByNameIgnoreCase(name)
//                .orElseThrow(() -> new RuntimeException("Tag not found"));
//        User user1 = UserMapper.mapToUser(user);
//        user1.getFollowedTags().add(tag);
//        userRepository.save(user1);
//    }
//
//    @Override
//    @Transactional
//    public void unfollowTag(UserDto user, String name) {
//        Tag tag = tagRepository.findByNameIgnoreCase(name)
//                .orElseThrow(() -> new RuntimeException("Tag not found"));
//
//        User user1 = UserMapper.mapToUser(user);
//        user1.getFollowedTags().remove(tag);
//        userRepository.save(user1);
//    }

    @Override
    @Transactional
    public void followTag(UserDto userDto, String name) {
        Tag tag = tagRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("Tag not found"));

        User user = userRepository.findById(userDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getFollowedTags().add(tag);
    }

    @Override
    @Transactional
    public void unfollowTag(UserDto userDto, String name) {
        Tag tag = tagRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("Tag not found"));

        User user = userRepository.findById(userDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getFollowedTags().remove(tag);
    }


//    @Override
//    public boolean isUserFollowingTag(UserDto user, String name) {
//        User user1 = UserMapper.mapToUser(user);
//
//        return user1.getFollowedTags().stream()
//                .anyMatch(tag -> tag.getName().equals(name));
//    }


    @Override
    public boolean isUserFollowingTag(UserDto userDto, String name) {
        User user = userRepository.findById(userDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getFollowedTags()
                .stream()
                .anyMatch(tag -> tag.getName().equals(name));
    }


    @Override
    public Set<Tag> getFollowedTags(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getFollowedTags();
    }

    @Override
    public Optional<Tag> findById(Long id) {
        return tagRepository.findById(id);
    }

    @Override
    public Optional<Tag> findByName(String name) {
        return tagRepository.findByNameIgnoreCase(name);
    }
}
