package com.example.pethelper.service;


import com.example.pethelper.dto.UserDto;
import com.example.pethelper.entity.Tag;
import com.example.pethelper.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagService {
    Tag findOrCreateTag(String tagName);
    Set<Tag> findOrCreateTags(Set<String> tagNames);
    Set<Tag> getAllTags();
    void followTag(UserDto user, String name);
    void unfollowTag(UserDto user, String name);
    boolean isUserFollowingTag(UserDto user, String name);
    Set<Tag> getFollowedTags(Long userId);
    Optional<Tag> findById(Long id);
    Optional<Tag> findByName(String name);
}
