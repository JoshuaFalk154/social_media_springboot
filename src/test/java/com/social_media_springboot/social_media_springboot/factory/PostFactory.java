package com.social_media_springboot.social_media_springboot.factory;

import com.social_media_springboot.social_media_springboot.DTO.CreatePostDTO;
import com.social_media_springboot.social_media_springboot.entities.Like;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;

import java.util.ArrayList;
import java.util.List;

public class PostFactory {

    private static Long postCounter = 1L;

    public static Post createValidPost() {
        return createValidPost(null, null);
    }

    public static Post createValidPost(User owner, List<Like> likes) {
        return createPost(null, owner, likes);
    }

    public static Post createValidPostWithId() {
        return createValidPostWithId(null, null);
    }

    public static Post createValidPostWithId(User owner, List<Like> likes) {
        return createPost(generateId(), owner, likes);
    }

    public static Post createPost(Long id, User owner, List<Like> likes) {
        return Post.builder()
                .id(id)
                .owner(owner)
                .title("Valid Title " + (id != null ? id : ""))
                .content("Valid Content " + (id != null ? id : ""))
                .isPublic(false)
                .likes(likes != null ? likes : new ArrayList<>())
                .build();
    }

    public static CreatePostDTO createValidCreatePostDTO() {
        return createCreatePostDTO("content", "title", true);
    }

    public static CreatePostDTO createCreatePostDTO(String content, String title, boolean isPublic) {
        return CreatePostDTO.builder()
                .title(title)
                .content(content)
                .isPublic(isPublic)
                .build();


    }


    private static Long generateId() {
        return postCounter++;
    }

}