package com.zdy.dao;

import com.zdy.domain.ShoppingCart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 购物车 Mapper 接口
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-06-05
 */
@Mapper
public interface ShoppingCartDao extends BaseMapper<ShoppingCart> {

}
