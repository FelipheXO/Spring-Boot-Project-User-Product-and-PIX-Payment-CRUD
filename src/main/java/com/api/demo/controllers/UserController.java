package com.api.demo.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.demo.entity.Users;
import com.api.demo.models.ErrorResponse;
import com.api.demo.security.AccountSecurity;
import com.api.demo.security.TokenService;
import com.api.demo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AccountSecurity AccountSecurity;
    @Autowired
    private TokenService tokenService;

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("The 'email' field is required."));
        } else if (password == null || password.length() < 6) {
            return ResponseEntity.badRequest().body(new ErrorResponse("The 'password' field is required."));
        }

        Users user = this.userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid email or password."));
        }
        boolean isPasswordValid = this.AccountSecurity.isPasswordValid(password, user.getPassword());
        if (!isPasswordValid) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid email or password."));
        }

        Map<String, Object> response = new HashMap<>();
        Users roleUsers = new Users();
        roleUsers.setId(user.getId());
        response.put("token", tokenService.generateToken(roleUsers));
        return ResponseEntity.ok(response);
    }

    @PostMapping("create")
    public ResponseEntity<?> create(@RequestBody Users user) {

        if (user.getEmail() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("The 'email' field is required."));
        } else if (user.getPassword() == null || user.getPassword().length() < 4) {
            return ResponseEntity.badRequest().body(new ErrorResponse("The 'password' field is required."));
        } else if (user.getName() == null || user.getName().length() < 1) {
            return ResponseEntity.badRequest().body(new ErrorResponse("The 'name' field is required."));
        } else if (user.getPhone() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("The 'name' field is required."));
        } else if (user.getCpf() == null || user.getCpf().length() < 14) {
            return ResponseEntity.badRequest().body(new ErrorResponse("The 'cpf' field is required."));
        }

        Users existUser = userService.findByEmail(user.getEmail());
        if (existUser != null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("The 'email' field is already in use."));
        }

        String hashPassword = this.AccountSecurity.hashPassword(user.getPassword());
        user.setPassword(hashPassword);

        Users newUser = this.userService.save(user);
        Map<String, Object> response = new HashMap<>();
        response.put("token", tokenService.generateToken(newUser));
        return ResponseEntity.ok(response);
    }

    @PostMapping("get")
    public ResponseEntity<?> get(HttpServletRequest request) {
        final var anUser = this.userService.getUser(request);
        return ResponseEntity.ok(anUser);
    }
}
