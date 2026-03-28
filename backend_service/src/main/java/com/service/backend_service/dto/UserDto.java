package com.service.backend_service.dto;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
public class UserDto implements Serializable {
   private String name;
   private String email;
   private String password;
   private String token;
}
