package com.zdy.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zdy.common.R;
import com.zdy.dao.DishDao;
import com.zdy.domain.Category;
import com.zdy.domain.Dish;
import com.zdy.domain.DishFlavor;
import com.zdy.dto.DishDto;
import com.zdy.service.ICategoryService;
import com.zdy.service.IDishFlavorService;
import com.zdy.service.IDishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜品管理 前端控制器
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-05-30
 */

//DishFlavor、Dish类的操作基本在一起，所以公用一个DishController控制类
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private IDishService dishService;

    @Autowired
    private IDishFlavorService dishFlavorService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询所有数据
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        dishLambdaQueryWrapper.orderByAsc(Dish::getSort)
                              .orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, dishLambdaQueryWrapper);

        //进行对象的拷贝，不拷贝records属性的值
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        //获取pageInfo中的records
        List<Dish> records = pageInfo.getRecords();

        //获取records中的categoryName
        List<DishDto> dtoList = records.stream().map((item) -> {
            //定义dishDto对象
            DishDto dishDto = new DishDto();

            //进行对象的拷贝
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();
            //根据id进行条件查询
            Category category = categoryService.getById(categoryId);

            if (category != null){
                //获取对应的菜品分类名称
                String categoryName = category.getName();

                //设置dishDto中的categoryName的值
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        //设置dishDtoPage中的records值
        dishDtoPage.setRecords(dtoList);

        return R.success(dishDtoPage);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> deleteByIds(Long[] ids) {
        dishService.deleteWithFlavor(ids);
        return R.success("批量删除成功");
    }

    /**
     * 批量修改启用、禁用状态
     *
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatusByIds(@PathVariable Integer status, Long[] ids) {
        for (Long id : ids) {
            //此方法不可行
//            dishService.lambdaUpdate().set(Dish::getStatus, status)
//                    .eq(Dish::getId, id);

            //可行
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("批量修改状态成功");
    }

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody DishDto dishDto){
        //由于新增菜品涉及到两个表，则要自己写业务中的方法
        dishService.saveWithFlavor(dishDto);

         /* //清理所有菜品的缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);*/

        //精确清理某个分类下的菜品缓存数据
        //动态构造key
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("新增菜品成功");
    }

    /**
     * 查询想详情，根据id进行数据回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping()
    public R<String> update(@RequestBody DishDto dishDto){

        //根据id修改数据
        dishService.updateWithFlavor(dishDto);

       /* //清理所有菜品的缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);*/

        //精确清理某个分类下的菜品缓存数据
        //动态构造key
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("修改菜品成功");
    }

    /**
     * 查看菜品列表
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {

        //设置返回的数据为null
        List<DishDto> dtoList = null;

        //定义redis中的key
        String key = "dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        //1.先从redis中获取缓存数据
        dtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        //2.如果缓存中查到数据，直接返回，无须再查数据库
        if (dtoList != null){
            return R.success(dtoList);
        }

        //3.如果缓存中不存在，继续下面的操作，并将查询到的菜品数据缓存到Redis中
        //根据categoryId查询对应的菜品
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId())
                .eq(Dish::getStatus, 1)
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lqw);

        //获取records中的categoryName
        dtoList = list.stream().map((item) -> {
            //定义dishDto对象
            DishDto dishDto = new DishDto();
            //进行对象的拷贝
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            //根据id进行条件查询
            Category category = categoryService.getById(categoryId);
            if (category != null){
                //获取对应的菜品分类名称
                String categoryName = category.getName();
                //设置dishDto中的categoryName的值
                dishDto.setCategoryName(categoryName);
            }
            //获取给该菜品对应的口味信息
            LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
            qw.eq(DishFlavor::getDishId,item.getId());
            List<DishFlavor> flavorList = dishFlavorService.list(qw);
            dishDto.setFlavors(flavorList);
            return dishDto;
        }).collect(Collectors.toList());
        //添加到缓存中
        redisTemplate.opsForValue().set(key,dtoList,60, TimeUnit.MINUTES);
        return R.success(dtoList);
    }

}

