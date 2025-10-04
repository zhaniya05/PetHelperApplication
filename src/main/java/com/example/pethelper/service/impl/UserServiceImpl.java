package com.example.pethelper.service.impl;

import com.example.pethelper.dto.UserDto;
import com.example.pethelper.entity.User;
import com.example.pethelper.mapper.UserMapper;
import com.example.pethelper.repository.UserRepository;
import com.example.pethelper.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    //private final String uploadDir = "C:\\Users\\baite\\IdeaProjects\\PetHelper\\src\\main\\resources\\static\\css\\uploads\\";
    private final String uploadDir = "C:\\Users\\baite\\IdeaProjects\\PetHelper\\uploads\\";


    @Override
    public UserDto register(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = UserMapper.mapToUser(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserDto(savedUser);
    }

    @Override
    public UserDto login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return UserMapper.mapToUserDto(user);
    }


    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto, MultipartFile avatarFile) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (userDto.getUserName() != null) {
            user.setUserName(userDto.getUserName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmailAndUserIdNot(userDto.getEmail(), id)) {
                throw new RuntimeException("Email is already in use by another user");
            }
            user.setEmail(userDto.getEmail());
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String avatarPath = saveAvatar(avatarFile, id);
                user.setProfilePicture(avatarPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save avatar: " + e.getMessage());
            }
        }

        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserDto(savedUser);
    }

    private String saveAvatar(MultipartFile avatarFile, Long userId) throws IOException {
        // Создаем директорию для аватаров, если не существует
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Генерируем уникальное имя файла
        String originalFileName = avatarFile.getOriginalFilename();
        String fileExtension = originalFileName != null ?
                originalFileName.substring(originalFileName.lastIndexOf(".")) : ".jpg";
        String fileName = "avatar_" + userId + "_" + System.currentTimeMillis() + fileExtension;

        // Сохраняем файл
        Path filePath = Paths.get(uploadDir + fileName);
        Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);


        return "/uploads/" + fileName;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDto findByUsername(String username) {
        return UserMapper.mapToUserDto(userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username)));
    }

    @Override
    public UserDto findByEmail(String email) {
        return UserMapper.mapToUserDto(userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email)));
    }
}
