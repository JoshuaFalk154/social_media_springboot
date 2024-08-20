package com.social_media_springboot.social_media_springboot.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "likes")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;


    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Like like = (Like) o;
        return Objects.equals(id, like.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
