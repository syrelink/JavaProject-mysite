package com.syr.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syr.dto.LoginFormDto;
import com.syr.dto.UserDTO;
import com.syr.entity.User;
import com.syr.mapper.UserMapper;
import com.syr.service.IUserService;
import com.syr.utils.RegexUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import com.syr.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.syr.utils.RedisConstants.*;
import static com.syr.utils.SystemConstants.USER_NICK_NAME_PREFIX;


@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //  发送验证码并保存
    public Result sendCode(String phone, HttpSession session){

        if(RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("手机号格式错误！");
        }
        String code = RandomUtil.randomNumbers(6);

        // 将验证码把存到redis,指定过期时间，以及时间单位
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY+phone,code,LOGIN_CODE_TTL, TimeUnit.MINUTES);

        //假装发送成功了
        log.debug("发送验证码成功！" + code);
        return Result.ok();
    }

    // 登陆
    public Result login(LoginFormDto loginform, HttpSession session) {
        //获取用户输入的数据
        String phone = loginform.getPhone();
        String code = loginform.getCode();
        //校验手机号
        if(RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("手机格式不正确！");
        }
        // 从redis获取验证码，并校验
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
        if(cacheCode == null || !cacheCode.toString().equals(code)) {
            return Result.fail("验证码错误！");
        }
        // 查询数据库，查到user
        User user = query().eq("phone", phone).one();
        // 用户不存在，创建用户并保存
        if(user == null) {
            user = createUserWithPhone(phone);
        }
        // 保存查询到的用户到redis
        // 1. 声明随机token，作为登录令牌
        String token = UUID.randomUUID().toString();
        // 2. 将user转为hashmap存储
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        CopyOptions copyOptions = CopyOptions.create()
                .setIgnoreNullValue(true) // 忽略null值
                // 核心：设置一个字段值编辑器，把所有非null的值都转成String
                .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString());
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(), copyOptions);
        // 3. 存储到redis
        stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY+token, userMap);
        // 4. 设置token有效期
        stringRedisTemplate.expire(LOGIN_USER_KEY+token,30,TimeUnit.MINUTES);

        return Result.ok(token);
    }

    private User createUserWithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        // 生成随即用户名
        user.setNickName(USER_NICK_NAME_PREFIX +RandomUtil.randomString(10));
        // 保存用户到数据库中
        save(user);
        return user;
    }


    @Override
    public Result getById(Long userId) {
        return null;
    }



}
