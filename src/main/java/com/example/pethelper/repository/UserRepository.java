package com.example.pethelper.repository;

import com.example.pethelper.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String username);
    boolean existsByEmailAndUserIdNot(String email, Long id);
    List<User> findByUserNameContainingIgnoreCase(String keyword);

    //Long countByCreationDateAfter(LocalDate localDate);

    // countByRegistrationDateAfter(LocalDate );
}