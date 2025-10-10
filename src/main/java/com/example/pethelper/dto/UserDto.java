package com.example.pethelper.dto;

import com.example.pethelper.entity.Pet;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long userId;

    private String userName;

    private String email;

    private String password;

    private String role;

    private List<Long> petIds;

    private String profilePicture;


}
