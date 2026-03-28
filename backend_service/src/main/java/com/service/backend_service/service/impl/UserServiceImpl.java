package com.service.backend_service.service.impl;

import com.service.backend_service.config.security.JwtUtil;
import com.service.backend_service.dto.UserDto;
import com.service.backend_service.model.User;
import com.service.backend_service.repo.UserRepository;
import com.service.backend_service.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final ModelMapper modelMapper;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final JwtUtil jwtUtil;

    private final AuthenticationConfiguration authenticationConfiguration;

    public UserServiceImpl(ModelMapper modelMapper,
                           UserRepository userRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           JwtUtil jwtUtil,
                           AuthenticationConfiguration authenticationConfiguration) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Override
    public ResponseEntity<User> register(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @Override
    public ResponseEntity<UserDto> login(UserDto userDto) {
        authenticate(userDto.getEmail(), userDto.getPassword());
        String token = jwtUtil.generateToken(userDto.getEmail());
        userDto.setPassword(null);
        userDto.setToken(token);
        return ResponseEntity.ok(userDto);
    }

    private void authenticate(String username, String password) {
        try {
            authenticationConfiguration.getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

        } catch (Exception e) {
            throw new RuntimeException("Invalid username or password");
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList()
        );
    }
}
