package com.zdy.service.impl;

import com.zdy.domain.User;
import com.zdy.dao.UserDao;
import com.zdy.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-05-28
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements IUserService {

}
