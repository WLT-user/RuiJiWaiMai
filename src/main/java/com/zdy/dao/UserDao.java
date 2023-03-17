package com.zdy.dao;

import com.zdy.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户信息 Mapper 接口
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-05-28
 */
@Mapper
public interface UserDao extends BaseMapper<User> {

}
