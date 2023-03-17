package com.zdy.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zdy.common.BaseContext;
import com.zdy.common.R;
import com.zdy.domain.User;
import com.zdy.service.IUserService;
import com.zdy.utils.SMSUtils;
import com.zdy.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户信息 前端控制器
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-05-28
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送手机验证码
     *
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {

        //1.获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            //2.生成随机的4位验证码
//            String code = ValidateCodeUtils.generateValidateCode4String(4);
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码：{}", code);
            //3.调用阿里云提供的短信服务API完成发送短信
//            SMSUtils.sendMessage("阿里云短信测试",".......",phone,code);
            //4.需要将生成的验证码保存到 Session 中
//            session.setAttribute(phone, code);

            //将生成的验证码缓存到redis中，并设置有效期为一分钟
            redisTemplate.opsForValue().set(phone,code,1, TimeUnit.MINUTES);

            return R.success("发送成功");
        }
        return R.error("发送失败");
    }

    /**
     * 用户登录
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {

        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //获取session中的手机号、验证码
//        Object codeInSession = session.getAttribute(phone);

        //获取redis中存储的验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        //进行对比
        if (codeInSession != null && codeInSession.equals(code)) {
            //登录成功
            //判断当前手机号对应的用户是否为新用户，若为新用户，自动完成注册

            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
            lqw.eq(User::getPhone, phone);
            User user = userService.getOne(lqw);

            if (user == null) {
                //注册新用户
                user = new User();
                user.setPhone(phone);
                //数据库中默认时 0：禁用，此时应设置启用
                user.setStatus(1);
                //数据库中添加新用户
                userService.save(user);
            }
            //拦截器中需要session，否则会出现闪退
            session.setAttribute("user",user.getId());

            //用户登成功，删除redis中缓存的数据
            redisTemplate.delete(phone);

            return R.success(user);
        }
        //登录失败
        return R.error("登录失败");
    }

    /**
     * 用户退出登录
     * @return
     */
    @PostMapping("/loginout")
    public R<String> loginOut(HttpSession session){
        //清理session中保存的当前登录的id
        session.removeAttribute("user");
        return R.success("退出成功");
    }
}

