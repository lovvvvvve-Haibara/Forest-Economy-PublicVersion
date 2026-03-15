package edu.scau.forestproject.controller;

import edu.scau.forestproject.dto.RegisterRequest;
import edu.scau.forestproject.pojo.Result;
import edu.scau.forestproject.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class RegisterController {
    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping({"/api/v1/auth/register", "/register"})
    public Result register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success();
    }
}
