package com.zdy.service;

import com.zdy.domain.Dish;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zdy.dto.DishDto;

/**
 * <p>
 * 菜品管理 服务类
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-05-30
 */
public interface IDishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish,dish_flavor
    void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息，进行修改时的回显
    DishDto getByIdWithFlavor(Long id);

    //根据id修改dish的基本信息和口味信息
    void updateWithFlavor(DishDto dishDto);

    //删除dish和dish_flavor中的数据
    void deleteWithFlavor(Long[] ids);
}
