package com.example.pethelper.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post {
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGenerator")
    @SequenceGenerator(allocationSize=1, schema="public",  name="MySequenceGenerator", sequenceName = "mysequence")
    private Long postId;

    private String postContent;

    @ElementCollection
    @CollectionTable(
            name = "post_photos",
            joinColumns = @JoinColumn(name = "post_id")
    )
    @Column(name = "photo_url")
    private List<String> postPhotos = new ArrayList<>();

    private int postLikes = 0;

    private LocalDate postDate;

  //  private boolean liked;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    List<Comment> comments;

    @PrePersist
    public void prePersist() {
        this.postDate = LocalDate.now();
    }
}
