package com.example.pethelper.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGenerator")
    @SequenceGenerator(allocationSize=1, schema="public",  name="MySequenceGenerator", sequenceName = "mysequence")
    private Long commentId;

    private String commentContent;
    private LocalDate commentDate;

    @Column(name = "likes_count", nullable = false)
    private Integer likesCount = 0;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    // Add this relationship to track likes
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentLike> likes = new HashSet<>();

    @PrePersist
    public void prePersist() {
        this.commentDate = LocalDate.now();
    }

    public boolean isLikedByUser(User user) {
        return likes.stream().anyMatch(like -> like.getUser().getUserId().equals(user.getUserId()));
    }
}
