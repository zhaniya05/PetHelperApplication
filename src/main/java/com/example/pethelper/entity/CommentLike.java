package com.example.pethelper.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment_likes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Composite unique constraint to prevent duplicate likes
    public static class CommentLikeId implements Serializable {
        private Long user;
        private Long comment;
    }
}