package com.socialapi.socialapi.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequest(@NotBlank(message = "Le champ \"message\" est obligatoire") String message) {
}
