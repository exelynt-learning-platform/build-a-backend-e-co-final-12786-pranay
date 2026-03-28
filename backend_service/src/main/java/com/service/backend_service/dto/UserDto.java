package com.service.backend_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;


@Getter
@Setter
public class UserDto implements Serializable {
   private String name;
   private String email;
   private String password;
   private String token;
}