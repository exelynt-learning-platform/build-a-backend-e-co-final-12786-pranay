package com.service.backend_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.service.backend_service.config.security.JwtUtil;
import com.service.backend_service.dto.UserDto;
import com.service.backend_service.model.User;
import com.service.backend_service.repo.UserRepository;
import com.service.backend_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerEncodesPasswordAndSavesUser() {
        UserDto dto = new UserDto();
        dto.setPassword("plain");

        User mapped = new User();
        mapped.setPassword("plain");
        User saved = new User();
        saved.setId(1L);
        saved.setPassword("encoded");

        when(modelMapper.map(dto, User.class)).thenReturn(mapped);
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(userRepository.save(mapped)).thenReturn(saved);

        User response = userService.register(dto).getBody();

        assertEquals(1L, response.getId());
        assertEquals("encoded", mapped.getPassword());
        verify(userRepository).save(mapped);
    }

    @Test
    void loginAuthenticatesAndReturnsToken() throws Exception {
        UserDto dto = new UserDto();
        dto.setEmail("a@test.com");
        dto.setPassword("plain");

        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);
        when(jwtUtil.generateToken("a@test.com")).thenReturn("token-123");

        UserDto response = userService.login(dto).getBody();

        assertEquals("token-123", response.getToken());
        assertNull(response.getPassword());
        verify(authenticationManager).authenticate(any());
    }
}
