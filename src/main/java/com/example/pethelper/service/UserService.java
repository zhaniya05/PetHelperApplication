package com.example.pethelper.service;


import com.example.pethelper.dto.UserDto;
import com.example.pethelper.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserDto register(UserDto userDto);
    UserDto login(String email, String password);
    UserDto getUserById(Long id);
    List<UserDto> getAllUsers();
    UserDto updateUser(Long id, UserDto userDto, MultipartFile avatarFile);
    void deleteUser(Long id);
    UserDto findByUsername(String username);
    UserDto findByEmail(String email);
    List<UserDto> searchUsersByKeyword(String keyword);
    User getCurrentUser();
}
