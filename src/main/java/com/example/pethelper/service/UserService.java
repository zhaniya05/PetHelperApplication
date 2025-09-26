package com.example.pethelper.service;


import com.example.pethelper.dto.UserDto;
import com.example.pethelper.entity.User;
import java.util.List;

public interface UserService {

    UserDto addUser(UserDto userDto);
    UserDto getUserById(Long id);
    List<UserDto> getAllUsers();
    UserDto updateUser(Long id, UserDto updatedUserDto);
    void deleteUser(Long id);
}
