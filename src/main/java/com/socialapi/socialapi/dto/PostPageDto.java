package com.socialapi.socialapi.dto;

import java.util.List;

public record PostPageDto(List<PostDto> items, PaginationMeta meta) {
}
