package edu.scau.forestproject.controller;

import edu.scau.forestproject.pojo.Result;
import edu.scau.forestproject.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @DeleteMapping({"/api/v1/users/{id}", "/home/{id}"})
    public Result delete(@PathVariable Integer id) {
        userService.delete(id);
        return Result.success();
    }

    @GetMapping("/api/v1/users/{id}")
    public Result getById(@PathVariable Integer id) {
        return Result.success(userService.getUserBrief(id));
    }
}
