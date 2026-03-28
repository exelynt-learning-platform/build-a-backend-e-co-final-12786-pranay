package com.service.backend_service.controller;

import com.service.backend_service.dto.ApiResponse;
import com.service.backend_service.dto.UserDto;
import com.service.backend_service.model.User;
import com.service.backend_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users/")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    private ResponseEntity<ApiResponse<User>> register(@RequestBody UserDto userDto) {
        ResponseEntity<User> response = userService.register(userDto);
        return ResponseHelper.build(
                response,
                "User registered successfully",
                "User not found",
                "Invalid user request",
                "Storage limit exceeded"
        );
    }

    @PostMapping("/login")
    private ResponseEntity<ApiResponse<UserDto>> login(@RequestBody UserDto userDto) {
        ResponseEntity<UserDto> response = userService.login(userDto);
        return ResponseHelper.build(
                response,
                "User logged in successfully",
                "User not found",
                "Invalid username or password",
                "Storage limit exceeded"
        );
    }

}
