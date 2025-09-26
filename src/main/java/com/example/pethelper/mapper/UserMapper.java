package com.example.pethelper.mapper;

import com.example.pethelper.dto.PetDto;
import com.example.pethelper.dto.UserDto;
import com.example.pethelper.entity.Pet;
import com.example.pethelper.entity.User;

import java.util.stream.Collectors;

public class UserMapper {
    public static UserDto mapToUserDto(User user){

        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setUserName(user.getUserName());
        userDto.setEmail(user.getEmail());

        if (user.getPets() != null) {
            userDto.setPets(user.getPets().stream()
                    .map(PetMapper::mapToPetDto)
                    .collect(Collectors.toList()));
        }
        return userDto;
    }

    public static User mapToUser(UserDto userDto) {
        User user = new User();
        user.setUserId(userDto.getUserId());
        user.setUserName(userDto.getUserName());
        user.setEmail(userDto.getEmail());
        return user;
    }
}
