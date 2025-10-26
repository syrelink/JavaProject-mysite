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
   // 发送验证码
    @PostMapping("code")
    public Result sendCode(@RequestParam String phone, HttpSession session) {
        return userService.sendCode(phone,session);
    }
    // 根据验证码手机登陆、注册
    @PostMapping("login")
    public Result login(@RequestBody LoginFormDto loginform,HttpSession session){
        return userService.login(loginform,session);
    }
    // 获取当前登陆的用户信息
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
