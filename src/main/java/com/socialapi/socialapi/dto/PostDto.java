package com.socialapi.socialapi.dto;

import java.time.Instant;
import java.util.Set;

public record PostDto(Long id, String content, Instant publishedAt, UserDto author, Set<UserDto> likedBy) {
}
