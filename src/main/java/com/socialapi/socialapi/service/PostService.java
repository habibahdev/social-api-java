package com.socialapi.socialapi.service;

import com.socialapi.socialapi.dto.PaginationMeta;
import com.socialapi.socialapi.dto.PostCreateRequest;
import com.socialapi.socialapi.dto.PostDto;
import com.socialapi.socialapi.dto.PostPageDto;
import com.socialapi.socialapi.entity.Post;
import com.socialapi.socialapi.entity.User;
import com.socialapi.socialapi.exception.ForbiddenException;
import com.socialapi.socialapi.exception.ResourceNotFoundException;
import com.socialapi.socialapi.mapper.DtoMapper;
import com.socialapi.socialapi.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostPageDto findPaginated(int page, int limit) {
        int safePage = Math.max(1, page);
        int safeLimit = Math.max(1, Math.min(50, limit));
        Page<Post> result = postRepository.findAllByOrderByPublishedAtDesc(PageRequest.of(safePage - 1, safeLimit));
        List<PostDto> items = result.getContent().stream().map(DtoMapper::toDto).toList();
        PaginationMeta meta = new PaginationMeta(safePage, safeLimit, result.getTotalElements(), result.getTotalPages());
        return new PostPageDto(items, meta);
    }

    public PostDto findById(Long id) {
        return DtoMapper.toDto(getPostOrThrow(id));
    }

    public PostDto create(PostCreateRequest request, User author) {
        Post post = Post.create(request.content(), author);
        postRepository.save(post);
        return DtoMapper.toDto(post);
    }

    public void update(Long id, PostCreateRequest request, User currentUser) {
        Post post = getPostOrThrow(id);
        assertOwner(post.getAuthor(), currentUser);
        post.setContent(request.content());
        postRepository.save(post);
    }

    public void delete(Long id, User currentUser) {
        Post post = getPostOrThrow(id);
        assertOwner(post.getAuthor(), currentUser);
        postRepository.delete(post);
    }

    public Post getPostOrThrow(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("POst " + id + "introuvable"));
    }

    private void assertOwner(User author, User currentUser) {
        if (!author.getId().equals(currentUser.getId())) {
            throw  new ForbiddenException("Vous n'êtes pas l'auteur de ce post");
        }
    }
}
