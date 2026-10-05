package com.socialapi.socialapi.service;

import com.socialapi.socialapi.dto.UserDto;
import com.socialapi.socialapi.entity.Post;
import com.socialapi.socialapi.entity.User;
import com.socialapi.socialapi.mapper.DtoMapper;
import com.socialapi.socialapi.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeService {
    private final PostRepository postRepository;
    private final PostService postService;

    public LikeService(PostRepository postRepository, PostService postService) {
        this.postRepository = postRepository;
        this.postService = postService;
    }

    public List<UserDto> findLikers(Long postId) {
        Post post = postService.getPostOrThrow(postId);
        return post.getLikedBy().stream().map(DtoMapper::toDto).toList();
    }

    public void like(Long postId, User user) {
        Post post = postService.getPostOrThrow(postId);
        post.likedBy(user);
        postRepository.save(post);
    }

    public void unlike(Long postId, User user) {
        Post post = postService.getPostOrThrow(postId);
        post.dislikedBy(user);
        postRepository.save(post);
    }
}
