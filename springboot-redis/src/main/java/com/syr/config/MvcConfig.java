 package com.syr.config;

 import com.syr.utils.LoginInterceptor;
 import com.syr.utils.RefreshTokenInterceptor;
 import jakarta.annotation.Resource;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
 import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
 import org.springframework.data.redis.core.StringRedisTemplate;

 @Configuration
 public class MvcConfig implements WebMvcConfigurer {

     @Resource
     private StringRedisTemplate stringRedisTemplate;

     @Override
     public void addInterceptors(InterceptorRegistry registry) {
         // 登录拦截器
         registry.addInterceptor(new LoginInterceptor())
                 .excludePathPatterns(
                         "/user/code",
                         "/user/login"
                 ).order(1);

         // token刷新拦截器
         registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).addPathPatterns("/**").order(0);
     }
 }
