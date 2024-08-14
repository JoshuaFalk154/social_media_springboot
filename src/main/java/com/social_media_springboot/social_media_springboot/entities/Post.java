package com.social_media_springboot.social_media_springboot.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Table(name="posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(nullable = false, length = 1024)
    private String content;


    @Column()
    private boolean isPublic = false;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "post")
    private List<Like> likes;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;


    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;




}
