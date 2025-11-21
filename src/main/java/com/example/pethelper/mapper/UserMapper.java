package com.example.pethelper.mapper;


import com.example.pethelper.dto.UserDto;
import com.example.pethelper.entity.User;
import com.example.pethelper.entity.Pet;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        List<Long> petIds = user.getPets() != null
                ? user.getPets().stream().map(Pet::getPetId).collect(Collectors.toList())
                : null;

        return new UserDto(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                null, // пароль в DTO лучше не возвращать
                user.getRole(),
                petIds,
                user.getProfilePicture(),
                null
        );
    }


    public static User mapToUser(UserDto userDto) {
        User user = new User();
        user.setUserId(userDto.getUserId());
        user.setUserName(userDto.getUserName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword()); // обязательно!
        user.setRole(userDto.getRole() != null ? userDto.getRole() : "ROLE_USER");
        user.setProfilePicture(userDto.getProfilePicture());
        return user;
    }

}
