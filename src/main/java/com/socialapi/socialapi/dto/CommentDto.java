package com.socialapi.socialapi.dto;

import java.time.Instant;

public record CommentDto(Long id, String message, Instant publishedAt, UserDto author) {
}
