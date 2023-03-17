package com.zdy.dao;

import com.zdy.domain.DishFlavor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 菜品口味关系表 Mapper 接口
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-06-02
 */
@Mapper
public interface DishFlavorDao extends BaseMapper<DishFlavor> {

}
