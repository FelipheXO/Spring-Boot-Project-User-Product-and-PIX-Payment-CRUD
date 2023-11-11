package com.api.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.demo.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByEmail(String email);
}
