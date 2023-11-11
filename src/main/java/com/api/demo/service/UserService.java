package com.api.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.demo.entity.Users;
import com.api.demo.repository.UserRepository;
import com.api.demo.security.TokenService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {

    @Autowired
    private TokenService tokenService;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Users save(Users user) {
        return this.userRepository.save(user);
    }

    public Users getUser(HttpServletRequest request) {

        final var anAuthToken = request.getHeader("Authorization");
        final var aToken = anAuthToken.replace("Bearer ", "");
        final var anUsername = this.tokenService.validateToken(aToken);
        final var anUser = this.findById(anUsername);

        return anUser;
    }

    public Users findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public Users findById(String id) {
        Optional<Users> userOptional = this.userRepository.findById(Long.parseLong(id));
        return userOptional.orElse(null);
    }
}
