package com.service.backend_service.service;

import com.service.backend_service.dto.LoginResponseDto;
import com.service.backend_service.dto.UserDto;
import com.service.backend_service.model.User;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<User> register(UserDto userDto);

    ResponseEntity<LoginResponseDto> login(UserDto userDto);
}
