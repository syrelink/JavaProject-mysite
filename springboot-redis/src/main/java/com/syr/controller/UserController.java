package com.syr.controller;

import com.syr.dto.LoginFormDto;
import com.syr.dto.Result;
import com.syr.dto.UserDTO;
import com.syr.service.IUserService;
import com.syr.utils.UserHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    private IUserService userService;
    @PostMapping("code")
    public Result sendCode(@RequestParam String phone, HttpSession session) {
        return userService.sendCode(phone,session);
    }

    @PostMapping("login")
    public Result login(@RequestBody LoginFormDto loginform,HttpSession session){
        return userService.login(loginform,session);
    }

    @GetMapping("me")
    public Result me() {
        UserDTO user = UserHolder.getUser();
        return Result.ok(user);
    }

    @GetMapping("info/{id}")
    public Result info(@PathVariable Long userId) {
        return userService.getById(userId);
    }
}
