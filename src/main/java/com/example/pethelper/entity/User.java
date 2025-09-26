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
    private Long userId;

    private String userName;

    private String email;

    private String password;

    @OneToMany(mappedBy="user", cascade= CascadeType.ALL, fetch= FetchType.EAGER, orphanRemoval = true)
    private List<Pet> pets;

}
