package com.social_media_springboot.social_media_springboot.entities;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="posts")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
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

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    private List<Like> likes;

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
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
