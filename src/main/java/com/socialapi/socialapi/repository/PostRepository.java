package com.socialapi.socialapi.repository;

import com.socialapi.socialapi.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByOrderByPublishedAtDesc(Pageable pageable);
}
