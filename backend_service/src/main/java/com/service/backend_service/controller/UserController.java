package com.service.backend_service.controller;

import com.service.backend_service.dto.ApiResponse;
import com.service.backend_service.dto.LoginResponseDto;
import com.service.backend_service.dto.UserDto;
import com.service.backend_service.model.User;
import com.service.backend_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody UserDto userDto) {
        ResponseEntity<User> response = userService.register(userDto);
        return ResponseHelper.build(response, "User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@RequestBody UserDto userDto) {
        ResponseEntity<LoginResponseDto> response = userService.login(userDto);
        return ResponseHelper.build(response, "User logged in successfully");
    }

}
