package com.example.pethelper.service.impl;

import com.example.pethelper.dto.FollowDto;
import com.example.pethelper.dto.UserDto;
import com.example.pethelper.entity.Follow;
import com.example.pethelper.entity.FollowStatus;
import com.example.pethelper.entity.User;
import com.example.pethelper.mapper.FollowMapper;
import com.example.pethelper.mapper.UserMapper;
import com.example.pethelper.repository.FollowRepository;
import com.example.pethelper.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;

    @Override
    public void sendFollowRequest(UserDto follower, UserDto following) {
        User follower1 = UserMapper.mapToUser(follower);
        User following1 = UserMapper.mapToUser(following);
        Optional<Follow> existing = followRepository.findByFollowerAndFollowing(follower1, following1);
        if (existing.isPresent()) {
            return;
        }

        Follow follow = new Follow();
        follow.setFollower(follower1);
        follow.setFollowing(following1);
        follow.setStatus(FollowStatus.PENDING);
        followRepository.save(follow);
    }

    @Override
    public void acceptFollowRequest(Long id) {
        Follow follow = followRepository.findById(id).orElseThrow();
        follow.setStatus(FollowStatus.ACCEPTED);
        followRepository.save(follow);
    }

    @Override
    public void rejectFollowRequest(Long id) {
        Follow follow = followRepository.findById(id).orElseThrow();
        follow.setStatus(FollowStatus.REJECTED);
        followRepository.save(follow);
    }

    @Override
    public boolean isFollowing(UserDto follower, UserDto following) {
        User follower1 = UserMapper.mapToUser(follower);
        User following1 = UserMapper.mapToUser(following);
        return followRepository.findByFollowerAndFollowing(follower1, following1)
                .filter(f -> f.getStatus() == FollowStatus.ACCEPTED)
                .isPresent();
    }

    @Override
    public List<FollowDto> getPendingRequests(UserDto user) {
        User user1 = UserMapper.mapToUser(user);
        List<Follow> requests = followRepository.findByFollowingAndStatus(user1, FollowStatus.PENDING);
        return requests.stream()
                .map(FollowMapper::mapToFollowDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FollowDto> getFollowers(UserDto user) {
        User user1 = UserMapper.mapToUser(user);
        List<Follow> followers = followRepository.findByFollowingAndStatus(user1, FollowStatus.ACCEPTED);
        return followers.stream()
                .map(FollowMapper::mapToFollowDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FollowDto> getFollowing(UserDto user) {
        User user1 = UserMapper.mapToUser(user);
        List<Follow> following = followRepository.findByFollowerAndStatus(user1, FollowStatus.ACCEPTED);
        return following.stream()
                .map(FollowMapper::mapToFollowDto)
                .collect(Collectors.toList());
    }
}
