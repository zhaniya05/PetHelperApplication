package com.example.pethelper.mapper;


import com.example.pethelper.dto.PollDto;
import com.example.pethelper.dto.PostDto;
import com.example.pethelper.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PostMapper {

    // ✅ Метод без информации о пользователе (для общего использования)
    public static PostDto mapToPostDto(Post post) {
        return mapToPostDto(post, null);
    }

    // ✅ Метод с информацией о пользователе
    public static PostDto mapToPostDto(Post post, Long currentUserId) {
        PostDto dto = new PostDto();
        dto.setPostId(post.getPostId());
        dto.setPostContent(post.getPostContent());
        dto.setPostPhotos(post.getPostPhotos());
        dto.setPostVideos(post.getPostVideos());
        dto.setPostAudios(post.getPostAudios());
        dto.setPostLikes(post.getPostLikes());
        dto.setPostDate(post.getPostDate());
        dto.setUserId(post.getUser() != null ? post.getUser().getUserId() : null);
        dto.setVisibility(post.getVisibility() != null ? post.getVisibility().name() : null);
        dto.setUserName(post.getUser() != null ? post.getUser().getUserName() : null);
        dto.setLikeCount(post.getLikeCount());

        // ✅ Добавляем список тегов
        if (post.getTags() != null) {
            dto.setTagNames(
                    post.getTags().stream()
                            .map(Tag::getName)
                            .collect(Collectors.toSet())
            );
        }

        // ✅ Добавляем информацию об опросе
        if (post.getPoll() != null) {
            dto.setPoll(mapPollToDto(post.getPoll(), currentUserId));
        }

        return dto;
    }

    // ✅ Отдельный метод для маппинга Poll
    private static PollDto mapPollToDto(Poll poll, Long currentUserId) {
        if (poll == null) return null;

        PollDto pollDto = new PollDto();
        pollDto.setId(poll.getPollId());
        pollDto.setQuestion(poll.getQuestion());

        if (poll.getOptions() != null) {
            // Маппим тексты опций
            List<String> options = poll.getOptions().stream()
                    .map(PollOption::getText)
                    .collect(Collectors.toList());
            pollDto.setOptions(options);

            // Маппим ID опций
            List<Long> optionIds = poll.getOptions().stream()
                    .map(PollOption::getId)
                    .collect(Collectors.toList());
            pollDto.setOptionIds(optionIds);

            // Маппим количество голосов
            List<Integer> voteCounts = poll.getOptions().stream()
                    .map(PollOption::getVoteCount)
                    .collect(Collectors.toList());
            pollDto.setVoteCounts(voteCounts);

            // ✅ Устанавливаем информацию о голосовании пользователя
            if (currentUserId != null) {
                pollDto.setUserVoted(poll.hasUserVoted(currentUserId));

                PollOption userVote = poll.getUserVote(currentUserId);
                if (userVote != null) {
                    pollDto.setSelectedOptionId(userVote.getId());
                }
            }
        }

        return pollDto;
    }

    public static Post mapToPost(PostDto dto, User user) {
        Post post = new Post();
        post.setPostId(dto.getPostId());
        post.setPostContent(dto.getPostContent());
        post.setPostPhotos(dto.getPostPhotos() != null ? dto.getPostPhotos() : new ArrayList<>());
        post.setPostVideos(dto.getPostVideos() != null ? dto.getPostVideos() : new ArrayList<>());
        post.setPostAudios(dto.getPostAudios() != null ? dto.getPostAudios() : new ArrayList<>());
        post.setPostLikes(dto.getPostLikes());
        post.setPostDate(dto.getPostDate());
        post.setUser(user);

        if (dto.getVisibility() != null) {
            post.setVisibility(Visibility.valueOf(dto.getVisibility()));
        } else {
            post.setVisibility(Visibility.PUBLIC);
        }

        // Note: Tags and Poll are handled separately in service layer
        // because they require additional business logic

        return post;
    }
}