package com.service.backend_service.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto implements Serializable {
    private String email;
    private String token;
}
