package com.social_media_springboot.social_media_springboot.repositories;

import com.social_media_springboot.social_media_springboot.entities.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
}
