package com.social_media_springboot.social_media_springboot.repositories;

import com.social_media_springboot.social_media_springboot.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
