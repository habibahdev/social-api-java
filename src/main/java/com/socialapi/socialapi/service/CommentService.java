package com.socialapi.socialapi.service;

import com.socialapi.socialapi.dto.CommentCreateRequest;
import com.socialapi.socialapi.dto.CommentDto;
import com.socialapi.socialapi.entity.Comment;
import com.socialapi.socialapi.entity.Post;
import com.socialapi.socialapi.entity.User;
import com.socialapi.socialapi.exception.ForbiddenException;
import com.socialapi.socialapi.exception.ResourceNotFoundException;
import com.socialapi.socialapi.mapper.DtoMapper;
import com.socialapi.socialapi.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;

    public CommentService(CommentRepository commentRepository, PostService postService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
    }

    public List<CommentDto> findByPost(Long postId) {
        Post post = postService.getPostOrThrow(postId);
        return commentRepository.findByPost(post).stream().map(DtoMapper::toDto).toList();
    }

    public CommentDto findById(Long id) {
        return DtoMapper.toDto(getCommentOrThrow(id));
    }

    public CommentDto create(Long postId, CommentCreateRequest request, User author) {
        Post post = postService.getPostOrThrow(postId);
        Comment comment = Comment.create(request.message(), author, post);
        commentRepository.save(comment);
        return DtoMapper.toDto(comment);
    }

    public void update(Long id, CommentCreateRequest request, User currentUser) {
        Comment comment = getCommentOrThrow(id);
        assertOwner(comment.getAuthor(), currentUser);
        comment.setMessage(request.message());
        commentRepository.save(comment);
    }

    public void delete(Long id, User currentUser) {
        Comment comment = getCommentOrThrow(id);
        assertOwner(comment.getAuthor(), currentUser);
        commentRepository.delete(comment);
    }

    private Comment getCommentOrThrow(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commentaire " + id + "introuvable"));
    }

    private void assertOwner(User author, User currentUser) {
        if (!author.getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Vous n'êtes pas l'auteur de ce commantaire");
        }
    }
}
