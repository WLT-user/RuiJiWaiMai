package com.zdy.service;

import com.zdy.domain.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 菜品及套餐分类 服务类
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-05-30
 */
public interface ICategoryService extends IService<Category> {

    void deleteById(long id);
}
