package edu.scau.forestproject.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @JsonAlias({"phoneNumber", "username"})
    @NotBlank(message = "usernameOrPhone cannot be blank")
    private String usernameOrPhone;

    @NotBlank(message = "password cannot be blank")
    private String password;
}
