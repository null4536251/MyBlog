package com.tang.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tang.common.dto.LoginDto;
import com.tang.common.lang.Result;
import com.tang.entity.User;
import com.tang.service.UserService;
import com.tang.util.JwtUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletResponse;

@RestController
public class AccountController {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;
    @PostMapping("/login")
    public Result login(@RequestBody LoginDto loginDto, HttpServletResponse response){
        User user = userService.getOne(new QueryWrapper<User>().eq("username", loginDto.getUsername()));
        Assert.notNull(user, "用户不存在");
        if(!user.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))){
            return Result.fail("密码不正确");
        }
        String jwt = jwtUtils.generateToken(user.getId());
        response.setHeader("Authorization", jwt);
        response.setHeader("Access-control-Expose-Headers", "Authorization");
        return Result.succ(MapUtil.builder()
                .put("id", user.getId())
                .put("username", user.getUsername())
                .put("avatar", user.getAvatar())
                .put("email", user.getEmail())
                .map()
        );
    }
    @RequiresAuthentication
    @GetMapping("/logout")
    public Result logout() {
        System.out.println("注销成功");
        SecurityUtils.getSubject().logout();
        return Result.succ("注销成功");
    }

    @PostMapping("/register")
    @ResponseBody
    public Result register(@RequestBody User user) {
        System.out.println("111" + user);
        String username = user.getUsername();
        String password = user.getPassword();
        String emil = user.getEmail();
        username = HtmlUtils.htmlEscape(username);
        user.setUsername(username);
        user.setEmail(emil);
//        boolean exist = userService.isExist(username);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User temp = userService.getOne(wrapper);
        if(temp != null) {
            String message = "用户名字已被使用";
            System.out.println("用户已存在");
            return Result.fail(message);
        }
        else
        {
            System.out.println("username:" + temp);
            //生成盐, 默认长度为16位
            String salt = new SecureRandomNumberGenerator().nextBytes().toString();
            //设置hash算法迭代次数
            String encodedPassword = new SimpleHash("md5", password).toString();
            user.setPassword(encodedPassword);
            //        userService.add(user);
            userService.save(user);
            return Result.succ(user);
        }
    }
}
