package com.syr.service;

import com.syr.dto.LoginFormDto;
import jakarta.servlet.http.HttpSession;
import com.syr.dto.Result;



public interface IUserService {

    public Result sendCode(String phone,HttpSession session);

    public Result login(LoginFormDto loginform, HttpSession session);

    public Result getById(Long userId);
}
