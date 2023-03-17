package com.zdy.dao;

import com.zdy.domain.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 菜品及套餐分类 Mapper 接口
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-05-30
 */
@Mapper
public interface CategoryDao extends BaseMapper<Category> {

}
