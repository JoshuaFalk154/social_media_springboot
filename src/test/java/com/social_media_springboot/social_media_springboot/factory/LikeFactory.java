package com.social_media_springboot.social_media_springboot.factory;

import com.social_media_springboot.social_media_springboot.entities.Like;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;

public class LikeFactory {
    private static Long likeCounter = 1L;

    public static Like createValidLike() {
        return createValidLike(null, null);
    }

    public static Like createValidLike(User user, Post post) {
        return createLike(null, user, post);
    }

    public static Like createValidLikeWithId() {
        return createValidLikeWithId(null, null);
    }

    public static Like createValidLikeWithId(User user, Post post) {
        return createLike(generateId(), user, post);
    }

    public static Like createLike(Long id, User owner, Post post) {
        return Like.builder()
                .id(id)
                .user(owner)
                .post(post)
                .build();
    }

    private static Long generateId() {
        return likeCounter++;
    }

}