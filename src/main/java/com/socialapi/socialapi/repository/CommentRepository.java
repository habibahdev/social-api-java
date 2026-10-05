package com.socialapi.socialapi.repository;

import com.socialapi.socialapi.entity.Comment;
import com.socialapi.socialapi.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
}
