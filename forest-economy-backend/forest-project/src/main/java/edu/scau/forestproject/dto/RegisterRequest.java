package edu.scau.forestproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "username cannot be blank")
    @Size(min = 2, max = 20, message = "username length must be 2-20")
    private String username;

    @NotBlank(message = "phoneNumber cannot be blank")
    @Pattern(regexp = "^1\\d{10}$", message = "phoneNumber format invalid")
    private String phoneNumber;

    @NotBlank(message = "password cannot be blank")
    @Size(min = 8, max = 32, message = "password length must be 8-32")
    private String password;
}
