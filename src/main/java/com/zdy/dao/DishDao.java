package com.zdy.dao;

import com.zdy.domain.Dish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 菜品管理 Mapper 接口
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-05-30
 */
@Mapper
public interface DishDao extends BaseMapper<Dish> {

}
