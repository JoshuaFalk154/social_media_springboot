package com.social_media_springboot.social_media_springboot.repositories;

import com.social_media_springboot.social_media_springboot.entities.Like;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);
}
