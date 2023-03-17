package com.zdy.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.zdy.common.CustomException;
import com.zdy.domain.Dish;
import com.zdy.dao.DishDao;
import com.zdy.domain.DishFlavor;
import com.zdy.dto.DishDto;
import com.zdy.service.ICategoryService;
import com.zdy.service.IDishFlavorService;
import com.zdy.service.IDishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜品管理 服务实现类
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-05-30
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishDao, Dish> implements IDishService {


    @Autowired
    private IDishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味
     *
     * @param dishDto
     */
    //涉及多个表，则要添加事务控制
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {

        //保存菜品的基本数据信息到菜品表dish
        this.save(dishDto);

        //获取该菜品的id
        Long dishId = dishDto.getId();

        //获取菜品的口味，口味是一个集合
        List<DishFlavor> flavors = dishDto.getFlavors();
        //该集合中的每一个dish_id都为一个菜品，可以采用foreach的方式； 也可以采用stream流的方式进行添加数据
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品的口味
        //保存集合数，用此saveBatch
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息，进行修改时的回显
     *
     * @param id
     */
    @Override
    @Transactional
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        //拷贝数据
        BeanUtils.copyProperties(dish, dishDto);

        //获取菜品的分类,v-model绑定了categoryId，此处不需要进行查询类名
//        Long categoryId = dish.getCategoryId();
//        String name = categoryService.getById(categoryId).getName();
//        dishDto.setCategoryName(name);

        //获取菜品的口味
        Long dishId = dish.getId();
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId, dishId);
        List<DishFlavor> list = dishFlavorService.list(lqw);

        dishDto.setFlavors(list);

        return dishDto;
    }

    /**
     * 根据id修改dish的基本信息和口味信息
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //因为DishDto 继承了 Dish，可以直接使用该类中的方法
        this.updateById(dishDto);

        //1.清理当前菜品对应的口味信息
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(lqw);
        //2.添加当前菜品对应的口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();

        //该集合中的每一个dish_id都为一个菜品，可以采用foreach的方式； 也可以采用stream流的方式进行添加数据
        flavors = flavors.stream().map((item) -> {
            //因为运用了逻辑删除，数据则未真正的删除，在生成新的id，即可解决
            item.setId(IdWorker.getId());
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存菜品的口味
        //保存集合数，用此saveBatch
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除dish和dish_flavor中的数据
     *
     * @param ids
     */
    @Override
    @Transactional
    public void deleteWithFlavor(Long[] ids) {
        //判断是否可以经行删除操作
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.in(Dish::getId, ids)
                .eq(Dish::getStatus, 1);
        int count = this.count(lqw);

        if (count > 0) {
            //不能删除，抛出异常
            throw new CustomException("当前菜品正在售卖，不能删除");
        }
        //先删除当前表
        this.removeByIds(Arrays.asList(ids));
        //在删除关系表
        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
        qw.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(qw);
    }
}
