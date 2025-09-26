package com.example.pethelper.repository;

import com.example.pethelper.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

}