package com.socialapi.socialapi.mapper;

import com.socialapi.socialapi.dto.CommentDto;
import com.socialapi.socialapi.dto.PostDto;
import com.socialapi.socialapi.dto.UserDto;
import com.socialapi.socialapi.entity.Comment;
import com.socialapi.socialapi.entity.Post;
import com.socialapi.socialapi.entity.User;

import java.util.Set;
import java.util.stream.Collectors;

public final class DtoMapper {
    private DtoMapper() {}

    public static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public static PostDto toDto(Post post) {
        Set<UserDto> likedBy = post.getLikedBy().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toSet());

        return new PostDto(
                post.getId(),
                post.getContent(),
                post.getPublishedAt(),
                toDto(post.getAuthor()),
                likedBy
        );
    }

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getMessage(),
                comment.getPublishedAt(),
                toDto(comment.getAuthor())
        );
    }
}
