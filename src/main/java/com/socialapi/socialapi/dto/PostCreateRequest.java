package com.socialapi.socialapi.dto;

import jakarta.validation.constraints.NotBlank;

public record PostCreateRequest(@NotBlank(message = "Le champ \"content\" est obligatoire") String content) {
}
