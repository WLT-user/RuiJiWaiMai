package com.zdy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zdy.common.CustomException;
import com.zdy.domain.Category;
import com.zdy.dao.CategoryDao;
import com.zdy.domain.Dish;
import com.zdy.domain.Setmeal;
import com.zdy.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zdy.service.IDishService;
import com.zdy.service.ISetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 菜品及套餐分类 服务实现类
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-05-30
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements ICategoryService {

    @Autowired
    private IDishService dishService;

    @Autowired
    private ISetmealService setmealService;

    @Override
    public void deleteById(long id) {
        LambdaQueryWrapper<Dish> dlqw = new LambdaQueryWrapper<>();
        dlqw.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dlqw);
        //1.查询当前分类是否关联了菜品，如果关联，则不能删除
        if (count1 > 0){
            //不能删除，抛出自定义异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        LambdaQueryWrapper<Setmeal> slqw = new LambdaQueryWrapper<>();
        slqw.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(slqw);
        //2.查询当前分类是否关联了套餐，如果关联，则不能删除
        if (count2 > 0){
            //抛出异常
            throw new CustomException("当前套餐下关联了菜品，不能删除");
        }

        //3.正常删除，调用父类的方法
        super.removeById(id);
    }
}
