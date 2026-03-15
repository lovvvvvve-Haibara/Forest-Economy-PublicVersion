package edu.scau.forestproject.controller;

import edu.scau.forestproject.dto.LoginRequest;
import edu.scau.forestproject.pojo.LoginInfo;
import edu.scau.forestproject.pojo.Result;
import edu.scau.forestproject.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class LoginController {
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping({"/api/v1/auth/login", "/login"})
    public Result login(@Valid @RequestBody LoginRequest request) {
        LoginInfo loginInfo = userService.login(request);
        return Result.success(loginInfo);
    }
}
