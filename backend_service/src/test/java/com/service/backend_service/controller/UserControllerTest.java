package com.service.backend_service.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.service.backend_service.dto.LoginResponseDto;
import com.service.backend_service.model.User;
import com.service.backend_service.service.UserService;
import org.springframework.context.support.StaticMessageSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController userController;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("response.request_failed", java.util.Locale.getDefault(), "Request failed");
        messageSource.addMessage("response.not_found", java.util.Locale.getDefault(), "Resource not found");
        messageSource.addMessage("response.bad_request", java.util.Locale.getDefault(), "Invalid request");
        messageSource.addMessage("response.insufficient_storage", java.util.Locale.getDefault(), "Requested quantity is unavailable");
        userController = new UserController(userService, new ResponseHelper(messageSource));
    }

    @Test
    void registerReturnsWrappedResponse() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        User user = new User();
        user.setId(1L);
        when(userService.register(org.mockito.ArgumentMatchers.any())).thenReturn(ResponseEntity.ok(user));

        mockMvc.perform(post("/users/register")
                        .contentType("application/json")
                        .content("{\"name\":\"Pranay\",\"email\":\"a@test.com\",\"password\":\"secret\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void loginReturnsTokenInWrappedResponse() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        LoginResponseDto dto = new LoginResponseDto();
        dto.setEmail("a@test.com");
        dto.setToken("token-123");
        when(userService.login(org.mockito.ArgumentMatchers.any())).thenReturn(ResponseEntity.ok(dto));

        mockMvc.perform(post("/users/login")
                        .contentType("application/json")
                        .content("{\"email\":\"a@test.com\",\"password\":\"secret\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User logged in successfully"))
                .andExpect(jsonPath("$.data.token").value("token-123"));
    }
}
