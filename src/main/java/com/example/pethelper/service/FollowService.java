package com.example.pethelper.service;

import com.example.pethelper.dto.FollowDto;
import com.example.pethelper.dto.UserDto;
import com.example.pethelper.entity.Follow;
import com.example.pethelper.entity.User;

import java.util.List;

public interface FollowService {
    void sendFollowRequest(UserDto follower, UserDto following);
    void acceptFollowRequest(Long requestId);
    void rejectFollowRequest(Long requestId);
    boolean isFollowing(User follower, User following);
    List<FollowDto> getPendingRequests(UserDto user);
    List<FollowDto> getFollowers(UserDto user);
    List<FollowDto> getFollowing(UserDto user);
}
