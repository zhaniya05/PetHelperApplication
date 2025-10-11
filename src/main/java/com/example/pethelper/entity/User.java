package com.example.pethelper.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGenerator")
    @SequenceGenerator(allocationSize=1, schema="public",  name="MySequenceGenerator", sequenceName = "mysequence")
    private Long userId;

    private String userName;

    private String email;

    private String password;

    private String role = "ROLE_USER";

   @OneToMany(mappedBy="user", fetch = FetchType.LAZY)
   List<Pet> pets;

   @OneToMany(mappedBy="user", fetch=FetchType.LAZY)
   List<Post> posts;

   @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
   List<Comment> comments;


   private String profilePicture;


   @OneToMany(mappedBy = "follower")
   private List<Follow> followingList;

   @OneToMany(mappedBy = "following")
   private List<Follow> followersList;


}
