package com.example.pethelper.mapper;

import com.example.pethelper.dto.FollowDto;
import com.example.pethelper.entity.Follow;
import com.example.pethelper.entity.FollowStatus;
import com.example.pethelper.entity.User;

public class FollowMapper {

    public static FollowDto mapToFollowDto(Follow follow) {
        return new FollowDto(
                follow.getFollowId(),
                follow.getFollower() != null ? follow.getFollower().getUserId() : null,
                follow.getFollower() != null ? follow.getFollower().getUserName() : null,
                follow.getFollowing() != null ? follow.getFollowing().getUserId() : null,
                follow.getFollowing() != null ? follow.getFollowing().getUserName() : null,
                follow.getStatus() != null ? follow.getStatus().name() : null,
                follow.getCreatedAt()
        );
    }

    public static Follow mapToFollow(FollowDto followDto, User follower, User following) {
        Follow follow = new Follow();
        follow.setFollowId(followDto.getId());
        follow.setFollower(follower);
        follow.setFollowing(following);
        if (followDto.getStatus() != null) {
            follow.setStatus(FollowStatus.valueOf(followDto.getStatus()));
        } else {
            follow.setStatus(FollowStatus.PENDING);
        }
        follow.setCreatedAt(followDto.getCreatedAt());
        return follow;
    }
}
