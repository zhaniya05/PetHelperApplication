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

        UserDto userDto = new UserDto(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                null,
                user.getRole(),
                petIds,
                user.getProfilePicture(),
                null,
                // ✅ БЕЗОПАСНОЕ ПОЛУЧЕНИЕ ЗНАЧЕНИЙ С ПРОВЕРКОЙ НА NULL
                user.getExperiencePoints() != null ? user.getExperiencePoints() : 0,
                user.getLevel() != null ? user.getLevel() : 1,
                null,
                null
        );

        userDto.calculateProgress();

        return userDto;
    }

    public static User mapToUser(UserDto userDto) {
        User user = new User();
        user.setUserId(userDto.getUserId());
        user.setUserName(userDto.getUserName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setRole(userDto.getRole() != null ? userDto.getRole() : "ROLE_USER");
        user.setProfilePicture(userDto.getProfilePicture());

        // ✅ БЕЗОПАСНОЕ УСТАНОВЛЕНИЕ ЗНАЧЕНИЙ
        user.setExperiencePoints(userDto.getExperiencePoints() != null ? userDto.getExperiencePoints() : 0);
        user.setLevel(userDto.getLevel() != null ? userDto.getLevel() : 1);

        return user;
    }
}